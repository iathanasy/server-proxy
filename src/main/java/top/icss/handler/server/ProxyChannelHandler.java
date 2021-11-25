package top.icss.handler.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import top.icss.NettyServer;
import top.icss.factory.ChannelManager;
import top.icss.packet.MessagePacket;
import top.icss.utils.JsonSerializer;
import top.icss.utils.NetUtil;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author cd.wang
 * @create 2021-11-22 16:06
 */
@Slf4j
public class ProxyChannelHandler extends SimpleChannelInboundHandler<MessagePacket> {

    private NettyServer nettyServer;

    private AtomicBoolean auth = new AtomicBoolean(false);

    private int port;

    public ProxyChannelHandler(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessagePacket msg) throws Exception {
        HashMap map = JsonSerializer.deserialize(msg.getBody(), HashMap.class);
        String name = map.get("name").toString();
        switch (msg.getType()) {
            case AUTH:
                processAuth(ctx, msg);
                break;
            case DATA:
                processData(ctx, msg);
                break;
            case DISCONNECTED:
                processDisconnected(ctx, msg);
                break;
            case KEEPALIVE:
//                log.debug("{}, {}, {}", name, ctx.channel(), msg.getType());
                ctx.channel().writeAndFlush(msg);
                break;
            default:
                if (!auth.get()){
                    ctx.close();
                }
                throw new Exception("未知类型: " + msg.getType());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String name = ctx.channel().attr(ChannelManager.CLIENT_NAME).get();
        ChannelManager.clientChannel.remove(name);
        if (auth.get()) {
            if (nettyServer.getChannel() != null) {
                nettyServer.getChannel().close();
            }
            log.info("停止服务器的端口: {}， {}", name, port);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 授权
     *
     * @param ctx
     * @param msg
     */
    private void processAuth(ChannelHandlerContext ctx, MessagePacket msg) {
        HashMap<String, Object> map = JsonSerializer.deserialize(msg.getBody(), HashMap.class);
        String name = map.get("name").toString();
        Object serverIp = map.get("serverIp");
        Object port = map.get("port");
        Object remoteIp = map.get("remoteIp");
        Object remotePort = map.get("remotePort");
        int tempPort = Objects.isNull(port) ? NetUtil.getAvailablePort() : (int) port;

        try {
            final Channel proxyChannel = ctx.channel();
            proxyChannel.attr(ChannelManager.CLIENT_NAME).set(name);
            nettyServer.setProxyChannel(proxyChannel);
            nettyServer.startProxy(tempPort);
            this.port = tempPort;

            ChannelManager.clientChannel.put(name, proxyChannel);
            auth.set(true);
            log.info("{},客户端[{}]授权成功！{} --> {}", name, proxyChannel, String.format("%s:%s", serverIp, port), String.format("%s:%s", remoteIp, remotePort));
        } catch (Exception e) {
            log.error("客户端：{},{} 授权失败！{}", name, tempPort, e.getMessage());
            if (!auth.get()) {
                ctx.close();
            }
        }
    }


    /**
     * step 4
     * 读取数据
     * @param ctx
     * @param msg
     */
    private void processData(ChannelHandlerContext ctx, MessagePacket msg) {
        if (!auth.get()){
            ctx.close();
        }
        HashMap<String, Object> map = JsonSerializer.deserialize(msg.getBody(), HashMap.class);
        ChannelManager.channels.writeAndFlush(Unpooled.wrappedBuffer(msg.getData()), channel -> channel.id().asLongText().equals(map.get("channelId")));
    }

    /**
     * 断开链接
     * @param ctx
     * @param msg
     */
    private void processDisconnected(ChannelHandlerContext ctx, MessagePacket msg) {
        if (!auth.get()){
            ctx.close();
        }
        HashMap<String, Object> map = JsonSerializer.deserialize(msg.getBody(), HashMap.class);
        ChannelManager.channels.close(channel -> channel.id().asLongText().equals(map.get("channelId")));
    }

}
