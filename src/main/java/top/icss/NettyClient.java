package top.icss;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import top.icss.codec.MessageCodec;
import top.icss.constants.CommonConstants;
import top.icss.enums.EnumMessageType;
import top.icss.factory.ChannelManager;
import top.icss.handler.client.ProxyServerChannelHandler;
import top.icss.handler.client.RealServerChannelHandler;
import top.icss.packet.MessagePacket;
import top.icss.utils.JsonSerializer;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author cd.wang
 * @create 2021-11-17 18:46
 */
@Slf4j
@Data
public class NettyClient {

    public final NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    public Bootstrap b;
    private long sleepTimeMill = 1000;
    private String name;
    private String serverIp;
    private int serverPort;
    private int proxyRealPort;
    private String remoteIp;
    private int remotePort;

    public NettyClient(String name, String serverIp, int serverPort, int proxyRealPort, String remoteIp, int remotePort) {
        this.name = name;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.proxyRealPort = proxyRealPort;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        start();
    }

    public NettyClient(String name, int proxyRealPort, String remoteIp, int remotePort) {
        this.name = name;
        this.proxyRealPort = proxyRealPort;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        start();
    }


    public void start(){
        Bootstrap real = new Bootstrap();
        real.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new RealServerChannelHandler());
                    }
                });

        b = new Bootstrap();
        b.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 16, 0),
                                new IdleStateHandler(CommonConstants.NETTY_CLIENT_HEART_BEAT_TIME, 0, 0, TimeUnit.MILLISECONDS),
                                new MessageCodec(),
                                new ProxyServerChannelHandler(real, NettyClient.this));
                    }
                });
    }

    public void connect(){
        connect(serverIp, serverPort);
    }
    public void connect(final String serverIp, final int serverPort){
        try {
            b.connect(serverIp, serverPort).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future){
                    if (future.isSuccess()) {
                        Channel realServerProxy = future.channel();
                        realServerProxy.attr(ChannelManager.CLIENT_NAME).set(name);
                        // 发送授权信息
                        MessagePacket packet = new MessagePacket();
                        packet.setType(EnumMessageType.AUTH);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        map.put("serverIp", serverIp);
                        map.put("port", proxyRealPort);
                        map.put("remoteIp", remoteIp);
                        map.put("remotePort", remotePort);
                        packet.setBody(JsonSerializer.serialize(map));
                        future.channel().writeAndFlush(packet);
                        sleepTimeMill = 1000;
                        log.info("connect proxy server success, {}", future.channel());
                    } else {
                        log.warn("connect proxy server failed", future.cause());
                        reconnectWait();
                        connect(serverIp, serverPort);
                    }
                }
            });
        }catch (Exception e){
            log.error("链接异常：{}", e.getMessage());
        }
    }

    private void reconnectWait() {
        try {
            if (sleepTimeMill > 60000) {
                sleepTimeMill = 1000;
            }

            synchronized (this) {
                sleepTimeMill = sleepTimeMill * 2;
                wait(sleepTimeMill);
            }
        } catch (InterruptedException e) {
        }
    }

    public void close(){
        workerGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        NettyClient client = new NettyClient("admin",
                CommonConstants.SERVER_IP,
                CommonConstants.SERVER_PORT,
                7000,
                "192.168.10.212",
                8848);
        client.connect();
    }
}
