package top.icss;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import top.icss.config.ProxyClientConfig;
import top.icss.handler.ProxyChannelInitializer;

/**
 * @author cd.wang
 * @create 2022-04-01 13:58
 */
@Slf4j
@Data
public class ClientStarter {
    private String serverHost = ProxyClientConfig.getInstance().getServerHost();
    private int serverPort = ProxyClientConfig.getInstance().getServerPort();

    private String proxyHost = ProxyClientConfig.getInstance().getProxyHost();
    private int proxyPort = ProxyClientConfig.getInstance().getProxyPort();

    private String username = ProxyClientConfig.getInstance().getUsername();
    private String password = ProxyClientConfig.getInstance().getPassword();
    private int remotePort = ProxyClientConfig.getInstance().getRemotePort();

    private CallMsg callMsg;

    public ClientStarter(CallMsg callMsg) {
        this.callMsg = callMsg;
    }

    public static void main(String[] args) {
        ClientStarter clientStarter = new ClientStarter(new CallMsg() {
            @Override
            public void message(String msg) {
                log.info(msg);
            }
        });
        NettyClient nettyClient = new NettyClient();
        try {
            clientStarter.getCallMsg().message(ProxyClientConfig.getInstance().toString());
            ChannelFuture future = nettyClient.connect(new ProxyChannelInitializer(clientStarter), clientStarter.getServerHost(), clientStarter.getServerPort());
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
