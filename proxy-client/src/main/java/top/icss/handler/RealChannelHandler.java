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

    private final String channelId;
    private final Channel inboundChannel;

    public RealChannelHandler(String channelId, Channel inboundChannel) {
        this.channelId = channelId;
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        ChannelHolder.put(channelId, channel);
        log.info("[proxy] connected to {}.", channel.remoteAddress().toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        Objects.requireNonNull(inboundChannel, "The proxy channel is not exists in cache pool.")
                .writeAndFlush(Message.build(channelId, MessageType.DATA, msg));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("{} exception.", ctx.channel().id().asShortText(), cause);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        ChannelHolder.remove(channelId);
        log.info("[proxy] the channel {} disconnected", ctx.channel().remoteAddress().toString());

        Objects.requireNonNull(inboundChannel, "The proxy channel is not exists in cache pool.")
                .writeAndFlush(Message.build(channelId, MessageType.DISCONNECTED));

    }
}
