package top.icss.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import top.icss.ChannelHolder;
import top.icss.ClientStarter;
import top.icss.NettyClient;
import top.icss.protocol.Message;
import top.icss.protocol.MessageType;
import top.icss.utils.Runner;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author cd.wang
 * @create 2022-04-01 10:24
 */
@Slf4j
public class ProxyChannelHandler extends SimpleChannelInboundHandler<Message> {

    private ClientStarter clientStarter;
    private boolean success = true;

    public ProxyChannelHandler(ClientStarter clientStarter) {
        this.clientStarter = clientStarter;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.executor().scheduleWithFixedDelay(()->{
//            ctx.writeAndFlush(Message.create());
//        },0, 10, TimeUnit.SECONDS);

        // login client information
        processAuth(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        switch (msg.getType()) {
            case KEEPALIVE:
//                System.err.println(new Date() + ": " + msg);
                // 不处理
                break;
            case AUTH_RES:
                processAuthRes(ctx, msg);
                break;
            case CONNECTED:
                processConnected(ctx, msg);
                break;
            case DATA:
                processData(ctx, msg);
                break;
            case DISCONNECTED:
                processDisconnected(ctx, msg);
                break;
            default:
                throw new Exception("未知类型: " + msg.getType());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String str = String.format("与[%s]服务器的连接断开", ctx.channel().remoteAddress().toString());
        if(success){
            str += "，正在重连！";
            NettyClient client = new NettyClient();
            client.connect(new ProxyChannelInitializer(clientStarter), clientStarter.getServerHost(), clientStarter.getServerPort());
        }
        clientStarter.getCallMsg().message(str);
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.WRITER_IDLE) {
                if(ctx.channel().isActive()) {
                    // 发送心跳
                    ctx.writeAndFlush(Message.create()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                }
            }
        }
    }

    /**
     * 授权登录
     * @param ctx
     */
    private void processAuth(ChannelHandlerContext ctx){
        Message build = Message.build(MessageType.AUTH);
        build.setUserName(clientStarter.getUsername());
        build.setPassWord(clientStarter.getPassword());
        build.setPort(clientStarter.getRemotePort());
        ctx.writeAndFlush(build);
    }

    /**
     * 授权返回
     * @param ctx
     * @param msg
     */
    private void processAuthRes(ChannelHandlerContext ctx, Message msg) {
        clientStarter.getCallMsg().message(msg.getReason());
        if (!msg.getSuccess()) {
            success = msg.getSuccess();
            ctx.close();
        }
    }

    /**
     * 链接真实服务器
     * @param ctx
     * @param msg
     */
    private void processConnected(ChannelHandlerContext ctx, Message msg) {
        final Channel inboundChannel = ctx.channel();
        new Thread(() -> {
            try {
            // 链接真实服务器
            NettyClient client = new NettyClient();
            client.connect(new RealChannelInitializer(msg.getChannelId(), inboundChannel), clientStarter.getProxyHost(), clientStarter.getProxyPort());
            } catch (Exception e) {
                ctx.writeAndFlush(Message.build(msg.getChannelId(), MessageType.DISCONNECTED));
                processDisconnected(ctx, msg);
                clientStarter.getCallMsg().message(e.getMessage());
            }
        }, "proxy").start();
        // 等待链接成功
        Runner.runWithTimeout(() -> {
            Channel ch = ChannelHolder.get(msg.getChannelId());
            return ch != null;
        }, 5000);
    }
    /**
     * 处理数据
     * @param ctx
     * @param msg
     */
    private void processData(ChannelHandlerContext ctx, Message msg){
        Objects.requireNonNull(ChannelHolder.get(msg.getChannelId()),
                        "[proxy] the proxy channel " + msg.getChannelId() + " is not exists in cache.")
                .writeAndFlush(msg.getData());
    }

    /**
     * 断开链接
     * @param ctx
     * @param msg
     */
    private void processDisconnected(ChannelHandlerContext ctx, Message msg){
        Channel channel = ChannelHolder.get(msg.getChannelId());
        if(channel != null) {
            channel.close();
            ChannelHolder.remove(msg.getChannelId());
        }
    }
}
