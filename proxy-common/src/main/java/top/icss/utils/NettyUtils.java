package top.icss.utils;

import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * NettyUtils
 */
public class NettyUtils {

    private NettyUtils() {
    }

    public static boolean useEpoll() {
        String osName = System.getProperty("os.name");
        if (!osName.toLowerCase().contains("linux")) {
            return false;
        }
        if (!Epoll.isAvailable()) {
            return false;
        }
        String enableNettyEpoll = System.getProperty("netty.epoll.enable", "true");
        return Boolean.parseBoolean(enableNettyEpoll);
    }

    public static Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
        if (useEpoll()) {
            return EpollServerSocketChannel.class;
        }
        return NioServerSocketChannel.class;
    }

    public static Class<? extends SocketChannel> getSocketChannelClass() {
        if (useEpoll()) {
            return EpollSocketChannel.class;
        }
        return NioSocketChannel.class;
    }

}
