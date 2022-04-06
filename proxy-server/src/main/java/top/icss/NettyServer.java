package top.icss;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
import top.icss.utils.NettyUtils;

/**
 * @author cd.wang
 * @create 2022-04-01 10:14
 */
@Slf4j
public class NettyServer {

    private Channel channel;

    public void start(ChannelInitializer<SocketChannel> initializer, int port) throws Exception {
        log.info("server starting {}", port);
        final EventLoopGroup parentGroup = new NioEventLoopGroup();
        final EventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(parentGroup, childGroup)
                    .channel(NettyUtils.getServerSocketChannelClass())
                    .childHandler(initializer);

            channel = b.bind(port).sync().channel();
            channel.closeFuture().addListener((ChannelFutureListener) future -> {
                parentGroup.shutdownGracefully();
                childGroup.shutdownGracefully();
            });
        } catch (Exception e) {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
            throw e;
        }
    }

    public void destroy() {
        if (channel == null) return;
        this.channel.close();
    }


}
