package top.icss.constants;

import top.icss.utils.PropertyUtils;

/**
 * @author cd.wang
 * @create 2021-11-29 14:35
 */
public class ClientConstants {
    static {
        PropertyUtils.loadPropertyFile("client.properties");
    }
    // client

    public static final String CLIENT_NAME = PropertyUtils.getString("client.name", "admin");
    public static final String CLIENT_SERVER_IP = PropertyUtils.getString("client.server.ip", "127.0.0.1");
    public static final int CLIENT_SERVER_PORT = PropertyUtils.getInt("client.server.port", 5891);
    public static final int NETTY_CLIENT_HEART_BEAT_TIME = 1000 * 6;


    public static final int CLIENT_PROXY_PORT = PropertyUtils.getInt("client.proxy.port", 7777);
    public static final String SERVER_REAL_IP = PropertyUtils.getString("server.real.ip", "127.0.0.1");
    public static final int SERVER_REAL_PORT = PropertyUtils.getInt("server.real.port", 8888);
}
