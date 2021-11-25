package top.icss.handler.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.icss.enums.EnumMessageType;
import top.icss.factory.ChannelManager;
import top.icss.packet.MessagePacket;
import top.icss.utils.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cd.wang
 * @create 2021-11-19 11:03
 */
public class RealChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private Channel proxyChannel;


    public RealChannelHandler(Channel proxyChannel) {
        this.proxyChannel = proxyChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // step 1
        String name = proxyChannel.attr(ChannelManager.CLIENT_NAME).get();

        MessagePacket packet = new MessagePacket();
        packet.setType(EnumMessageType.CONNECTED);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("channelId", ctx.channel().id().asLongText());
        packet.setBody(JsonSerializer.serialize(map));
        proxyChannel.writeAndFlush(packet);
        ChannelManager.channels.add(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        String name = proxyChannel.attr(ChannelManager.CLIENT_NAME).get();
        MessagePacket packet = new MessagePacket();
        packet.setType(EnumMessageType.DATA);
        Map<String, Object> map = new HashMap<>();
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        map.put("name", name);
        map.put("channelId", ctx.channel().id().asLongText());
        packet.setBody(JsonSerializer.serialize(map));
        packet.setData(bytes);
        proxyChannel.writeAndFlush(packet);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String name = proxyChannel.attr(ChannelManager.CLIENT_NAME).get();
        MessagePacket packet = new MessagePacket();
        packet.setType(EnumMessageType.DISCONNECTED);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("channelId", ctx.channel().id().asLongText());
        packet.setBody(JsonSerializer.serialize(map));
        proxyChannel.writeAndFlush(packet);
    }
}
