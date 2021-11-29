package top.icss.handler.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import top.icss.NettyClient;
import top.icss.enums.EnumMessageType;
import top.icss.factory.ChannelManager;
import top.icss.packet.MessagePacket;
import top.icss.utils.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cd.wang
 * @create 2021-11-23 10:12
 */
@Slf4j
@Data
public class ProxyServerChannelHandler extends SimpleChannelInboundHandler<MessagePacket> {

    private Bootstrap real;
    private NettyClient client;

    public ProxyServerChannelHandler(Bootstrap real, NettyClient client) {
        this.real = real;
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessagePacket msg) throws Exception {
        HashMap map = JsonSerializer.deserialize(msg.getBody(), HashMap.class);
        String name = map.get("name").toString();
        switch (msg.getType()) {
            case RES:
                processRes(ctx, msg);
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
            case KEEPALIVE:
//                log.debug("{}, {}, {}", name, ctx, msg.getType());
                break;
            default:
                throw new Exception("未知类型: " + msg.getType());
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        client.close();
        log.info("与服务器[{}]断开链接！", ctx.channel().remoteAddress());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            MessagePacket packet = new MessagePacket();
            Map<String, Object> map = new HashMap<>();
            packet.setType(EnumMessageType.KEEPALIVE);
            map.put("name", client.getName());
            packet.setBody(JsonSerializer.serialize(map));
            ctx.writeAndFlush(packet);
        }else{
            super.userEventTriggered(ctx, evt);
        }
    }

    private void processRes(ChannelHandlerContext ctx, MessagePacket msg) {
        HashMap map = JsonSerializer.deserialize(msg.getBody(), HashMap.class);
        String name = map.get("name").toString();
        String message = map.get("message").toString();
        log.error(message);
    }

    // step 2
    private void processConnected(ChannelHandlerContext ctx, MessagePacket msg) throws InterruptedException {
        HashMap map = JsonSerializer.deserialize(msg.getBody(), HashMap.class);
        String name = map.get("name").toString();
        String channelId = map.get("channelId").toString();
        //链接真实服务器
        final String remoteIp = client.getRemoteIp();
        final int remotePort = client.getRemotePort();
        try {
            ChannelFuture future = real.connect(remoteIp, remotePort).sync();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()) {
                        final Channel realServerChannel = future.channel();
                        realServerChannel.attr(ChannelManager.CHANNEL_ID_STR).set(channelId);
                        realServerChannel.attr(ChannelManager.CHANNEL_ID).set(ctx.channel());

                        log.warn("connect real server success, {}", realServerChannel);
                        // 保存真实服务器Channel
                        ChannelManager.channelMap.put(channelId, realServerChannel);
                    }else{
                        log.error("connect real server failed, {}:{}", remoteIp, remotePort);
                        msg.setType(EnumMessageType.DISCONNECTED);
                        ctx.writeAndFlush(msg);
                        ChannelManager.channelMap.remove(channelId);
                    }
                }
            });
        }catch (Exception e){
            msg.setType(EnumMessageType.DISCONNECTED);
            ctx.writeAndFlush(msg);
            log.error("connect real server exception, {}:{}", remoteIp, remotePort);
            ChannelManager.channelMap.remove(channelId);
            throw e;
        }

    }

    private void processData(ChannelHandlerContext ctx, MessagePacket msg) {
        HashMap map = JsonSerializer.deserialize(msg.getBody(), HashMap.class);
        String name = map.get("name").toString();
        String channelId = map.get("channelId").toString();
        // 发送给真实服务器
        Channel channel = ChannelManager.channelMap.get(channelId);
        if(channel != null){
            ByteBuf byteBuf = Unpooled.wrappedBuffer(msg.getData());
            channel.writeAndFlush(byteBuf);
        }
    }

    private void processDisconnected(ChannelHandlerContext ctx, MessagePacket msg) {
        HashMap map = JsonSerializer.deserialize(msg.getBody(), HashMap.class);
        String name = map.get("name").toString();
        String channelId = map.get("channelId").toString();
        Channel channel = ChannelManager.channelMap.get(channelId);
        if(channel != null){
            channel.close();
            ChannelManager.channelMap.remove(channelId);
        }
    }
}
