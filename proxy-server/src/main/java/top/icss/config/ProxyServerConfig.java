package top.icss.config;

import lombok.Data;
import top.icss.utils.PropertyUtils;

/**
 * @author cd.wang
 * @create 2022-04-06 17:10
 */
@Data
public class ProxyServerConfig {
    /** 代理服务器地址 */
    private String serverHost;
    /** 代理服务器端口 */
    private int serverPort;

    private ProxyServerConfig(){
        PropertyUtils.loadPropertyFile("config/proxy-server.properties");
        this.serverHost = PropertyUtils.getString("server.host", "0.0.0.0");
        this.serverPort = PropertyUtils.getInt("server.port", 5891);
    }

    private static ProxyServerConfig instance = new ProxyServerConfig();

    public static ProxyServerConfig getInstance() {
        return instance;
    }
}
