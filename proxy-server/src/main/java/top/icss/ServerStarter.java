package top.icss;

import top.icss.config.ProxyServerConfig;
import top.icss.handler.ProxyChannelInitializer;
/**
 * @author cd.wang
 * @create 2022-04-01 14:23
 */
public class ServerStarter {

    public static void main(String[] args) throws Exception {
        int serverPort = ProxyServerConfig.getInstance().getServerPort();
        /* start tcp server **/
        NettyServer nettyServer = new NettyServer();
        nettyServer.start(new ProxyChannelInitializer(), serverPort);

    }
}
