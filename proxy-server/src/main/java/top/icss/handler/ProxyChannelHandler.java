package top.icss.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import top.icss.ChannelHolder;
import top.icss.NettyServer;
import top.icss.protocol.Message;
import top.icss.protocol.MessageType;
import top.icss.utils.ChannelUtils;

import java.net.BindException;
import java.util.Objects;

/**
 * @author cd.wang
 * @create 2022-04-01 10:24
 */
@Slf4j
public class ProxyChannelHandler extends SimpleChannelInboundHandler<Message> {
    private int lossConnectCount = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        lossConnectCount = 0;
        switch (msg.getType()) {
            case KEEPALIVE:
                ctx.writeAndFlush(Message.create());
                break;
            case AUTH:
                processAuth(ctx, msg);
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
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.err.println("已经30秒未收到客户端的消息了！" + lossConnectCount);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                lossConnectCount++;
                if (lossConnectCount > 2){
                    System.err.println("关闭这个不活跃通道！");
                    ctx.close();
                }
            }else {
                super.userEventTriggered(ctx,evt);
            }
        }
    }

    /**
     * 处理数据
     * @param ctx
     * @param msg
     */
    private void processAuth(ChannelHandlerContext ctx, Message msg){
        final Channel inboundChannel = ctx.channel();
        Message res = Message.build(MessageType.AUTH_RES);
        int tempPort = 0;
        int port = msg.getPort();
        if(port > 0){
           tempPort = port;
        }
        String userName = msg.getUserName();
        String passWord = msg.getPassWord();

        // 查询这个用户是否是合法的，不是合法的直接干掉
        boolean auth = authLogin(userName, passWord);
        if(!auth){
            res.setSuccess(false);
            res.setReason("用户非法，有疑问请联系管理员");
            ctx.writeAndFlush(res);
            return;
        }
        try {
            /* start proxy server **/
            NettyServer proxyServer = new NettyServer();
            proxyServer.start(new RealChannelInitializer(inboundChannel), tempPort);
            res.setSuccess(true);
            res.setReason("注册成功，外网地址是:  "+ ChannelUtils.toAddress(ctx.channel()).getIp() +":" + tempPort);
            System.err.println(String.format("用户[%s]注册成功，外网地址是: [%s]", userName, ChannelUtils.toAddress(ctx.channel()).getIp()  +":" + tempPort));
        } catch (Exception e) {
            e.printStackTrace();
            String err = e.getMessage();
            if(e instanceof BindException){
                err = String.format("端口[%s],已被绑定，请更换服务端口！", tempPort);
            }
            res.setSuccess(false);
            res.setReason(err);
            System.err.println(String.format("用户[%s]授权失败，失败原因：%s", userName, err));
        }
        ctx.writeAndFlush(res);
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

    /**
     * 授权
     * @param username
     * @param password
     * @return
     */
    public boolean authLogin(String username, String password){
        // 授权登录
        return true;
    }
}
