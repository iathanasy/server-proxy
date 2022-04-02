package top.icss;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import top.icss.handler.ProxyChannelInitializer;

/**
 * @author cd.wang
 * @create 2022-04-01 13:58
 */
@Data
public class ClientStarter {
    private String serverHost = "127.0.0.1";
    private int serverPort = 5891;

    private String proxyHost = "192.168.10.173";
    private int proxyPort = 8848;

    private String username = "admin";
    private String password = "123456";
    private int remotePort = 7000;

    private CallMsg callMsg;

    public ClientStarter(CallMsg callMsg) {
        this.callMsg = callMsg;
    }

    public static void main(String[] args) {
        ClientStarter clientStarter = new ClientStarter(new CallMsg() {
            @Override
            public void message(String msg) {
                System.err.println(msg);
            }
        });
        NettyClient nettyClient = new NettyClient();
        ChannelFuture future = null;
        try {
            future = nettyClient.connect(new ProxyChannelInitializer(clientStarter), clientStarter.getServerHost(), clientStarter.getServerPort());
            future.addListener(new GenericFutureListener() {
                @Override
                public void operationComplete(Future future) throws Exception {
                    clientStarter.getCallMsg().message("断开了连接");
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
            clientStarter.getCallMsg().message(e.getMessage());
        }


    }
}
