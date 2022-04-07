package top.icss.config;

import lombok.Data;
import top.icss.utils.PropertyUtils;

/**
 * @author cd.wang
 * @create 2022-04-06 17:18
 */
@Data
public class ProxyClientConfig {
    private String serverHost;
    private int serverPort;

    private String username;
    private String password;

    private String proxyHost;
    private int proxyPort;

    private int remotePort;

    private ProxyClientConfig(){
        PropertyUtils.loadPropertyFile("config/proxy-client.properties");
        this.serverHost = PropertyUtils.getString("server.host", "0.0.0.0");
        this.serverPort = PropertyUtils.getInt("server.port", 5891);
        this.username = PropertyUtils.getString("user.username", "admin");
        this.password = PropertyUtils.getString("user.password", "123456");
        this.proxyHost = PropertyUtils.getString("proxy.host", "0.0.0.0");
        this.proxyPort = PropertyUtils.getInt("proxy.port", 80);
        this.remotePort = PropertyUtils.getInt("remote.port", 7000);
    }

    private static ProxyClientConfig instance = new ProxyClientConfig();

    public static ProxyClientConfig getInstance() {
        return instance;
    }


}
