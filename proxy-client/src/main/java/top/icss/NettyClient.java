package top.icss;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import top.icss.utils.NettyUtils;

/**
 * @author cd.wang
 * @create 2022-04-01 13:39
 */
@Slf4j
public class NettyClient {

    public ChannelFuture connect(ChannelInitializer<SocketChannel> initializer, String inetHost, int inetPort) throws InterruptedException {
        log.info("client is connecting to {}:{}...", inetHost, inetPort);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NettyUtils.getSocketChannelClass());
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(initializer);
            Channel channel = b.connect(inetHost, inetPort).sync().channel();
            return channel.closeFuture().addListener(future -> workerGroup.shutdownGracefully());
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            throw e;
        }
    }
}
