package top.icss.utils;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author cd.wang
 * @create 2021-11-22 16:57
 */
public class NetUtil {

    /**
     * 获取一个可用的端口
     *
     * @return
     */
    public static int getAvailablePort() {
        ServerSocket serverSocket=null;
        try {
            serverSocket = new ServerSocket(0);
            return serverSocket.getLocalPort();
        } catch (Throwable ignored) {
        }finally {
            if (serverSocket!=null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }
}
