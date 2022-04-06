package top.icss;

import top.icss.handler.ProxyChannelInitializer;
/**
 * @author cd.wang
 * @create 2022-04-01 14:23
 */
public class ServerStarter {
    private static int serverPort = 5891;

    public static void main(String[] args) throws Exception {

        /* start tcp server **/
        NettyServer nettyServer = new NettyServer();
        nettyServer.start(new ProxyChannelInitializer(), serverPort);

    }
}
