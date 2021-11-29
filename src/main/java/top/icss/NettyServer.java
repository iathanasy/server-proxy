package top.icss;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import top.icss.codec.MessageCodec;
import top.icss.constants.ServerConstants;
import top.icss.handler.server.ProxyChannelHandler;
import top.icss.handler.server.RealChannelHandler;
import top.icss.utils.NettyUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author cd.wang
 * @create 2021-11-17 18:46
 */
@Data
@Slf4j
public class NettyServer {
    public final EventLoopGroup bossGroup;
    public final EventLoopGroup workerGroup;
    public volatile Channel proxyChannel;
    public volatile Channel channel;

    public NettyServer() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
    }

    public void start(){
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(bossGroup, workerGroup)
                    .channel(NettyUtils.getServerSocketChannelClass())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch){
                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 16, 0),
                                    new IdleStateHandler(0, 0, ServerConstants.NETTY_SERVER_HEART_BEAT_TIME, TimeUnit.MILLISECONDS),
                                    new MessageCodec(),
                                    new ProxyChannelHandler(NettyServer.this));
                        }
                    })
                    .bind(ServerConstants.SERVER_PORT);
            log.info("proxy server start on port " + ServerConstants.SERVER_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startProxy(int port) throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch){
                        RealChannelHandler realChannelHandler = new RealChannelHandler(proxyChannel);
                        ch.pipeline().addLast(realChannelHandler);
                    }
                });
        try {
            ChannelFuture future = b.bind(port).sync();
            channel = future.channel();
            log.info("bind proxy port success {}", port);
        } catch (Exception e) {
            log.error("startProxy Exception {}", e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer();
        nettyServer.start();
    }

}
