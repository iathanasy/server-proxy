package top.icss.handler.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.icss.enums.EnumMessageType;
import top.icss.factory.ChannelManager;
import top.icss.packet.MessagePacket;
import top.icss.utils.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cd.wang
 * @create 2021-11-23 10:10
 */
@Slf4j
public class RealServerChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private String getName(Channel proxyChannel) throws Exception {
        if(proxyChannel != null){
            return proxyChannel.attr(ChannelManager.CLIENT_NAME).get();
        }
        throw new Exception("proxyChannel is null !");
    }


    // step 3
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        Channel proxyChannel = ctx.channel().attr(ChannelManager.CHANNEL_ID).get();
        String channelId = ctx.channel().attr(ChannelManager.CHANNEL_ID_STR).get();
        String name = getName(proxyChannel);

        MessagePacket packet = new MessagePacket();
        packet.setType(EnumMessageType.DATA);
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("channelId", channelId);
        packet.setBody(JsonSerializer.serialize(map));
        packet.setData(bytes);
        proxyChannel.writeAndFlush(packet);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel proxyChannel = ctx.channel().attr(ChannelManager.CHANNEL_ID).get();
        String channelId = ctx.channel().attr(ChannelManager.CHANNEL_ID_STR).get();
        String name = getName(proxyChannel);
        MessagePacket packet = new MessagePacket();
        packet.setType(EnumMessageType.DISCONNECTED);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("channelId", channelId);
        packet.setBody(JsonSerializer.serialize(map));
        proxyChannel.writeAndFlush(packet);
    }
}
