package top.icss.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import top.icss.ClientStarter;
import top.icss.codec.MessageDecoder;
import top.icss.codec.MessageEncoder;


/**
 * @author cd.wang
 * @create 2022-04-01 10:24
 */
public class ProxyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private ClientStarter clientStarter;

    public ProxyChannelInitializer(ClientStarter clientStarter) {
        this.clientStarter = clientStarter;

    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(
                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 6, 0),
                new IdleStateHandler(0, 20, 0),
//                new LoggingHandler(),
                new MessageDecoder(),
                new MessageEncoder(),
                new ProxyChannelHandler(clientStarter)
        );
    }
}
