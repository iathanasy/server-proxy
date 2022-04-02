package top.icss.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

/**
 * @author cd.wang
 * @create 2022-04-01 10:20
 */
public class RealChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final Channel inboundChannel;

    public RealChannelInitializer(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(
                new ByteArrayDecoder(),
                new ByteArrayEncoder(),
                new RealChannelHandler(inboundChannel));
    }
}
