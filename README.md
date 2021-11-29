## 基于Netty做的内网穿透，暂不支持Https
## 特性
1. 支持http\tcp\websocket

## 使用
> 服务端(部署在外网服务器)
1. 打包 `mvn clean & mvn install`
2. 复制class文件夹下的 `server.properties`,保证`lib`、`server-proxy-1.0.jar`、`server.properties` 在同一级目录
2. 启动 `java -jar server-proxy-1.0.jar`
> 客户端(部署内网环境)
1. 启动 `java -cp server-proxy-1.0.jar top.icss.NettyClient`
