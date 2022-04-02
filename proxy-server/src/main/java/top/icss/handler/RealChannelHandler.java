package top.icss.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.icss.ChannelHolder;
import top.icss.protocol.Message;
import top.icss.protocol.MessageType;
import java.util.Objects;

/**
 * @author cd.wang
 * @create 2022-04-01 10:19
 */
@Slf4j
public class RealChannelHandler extends SimpleChannelInboundHandler<byte[]> {

    private final Channel inboundChannel;

    public RealChannelHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String channelId = channel.id().asShortText();
        ChannelHolder.put(channelId, channel);
        log.info("[proxy] the client {} connected.", channel.remoteAddress().toString());

        Objects.requireNonNull(inboundChannel, "The proxy channel is not exists in cache pool.")
                .writeAndFlush(Message.build(channelId, MessageType.CONNECTED));

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        String channelId = ctx.channel().id().asShortText();
        Objects.requireNonNull(inboundChannel, "The proxy channel is not exists in cache pool.")
                .writeAndFlush(Message.build(channelId, MessageType.DATA, msg));

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asShortText();
        log.info("[proxy] the channel {} disconnected", ctx.channel().remoteAddress().toString());

        Objects.requireNonNull(inboundChannel, "The proxy channel is not exists in cache pool.")
                .writeAndFlush(Message.build(channelId, MessageType.DISCONNECTED));

    }
}
