package top.icss.constants;

import top.icss.utils.PropertyUtils;

/**
 * @author cd.wang
 * @create 2021-11-17 18:52
 */
public class ServerConstants {
    // server
    public static final int SERVER_PORT = PropertyUtils.getInt("server.port", 5891);
    public static final int NETTY_SERVER_HEART_BEAT_TIME = 1000 * 60 * 3 + 1000;

}
