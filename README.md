## 基于Netty做的内网穿透，暂不支持Https
## 特性
1. 支持http\tcp\websocket

## 使用
> 服务端(部署在外网服务器)
```properties
# 服务器端口
server.port=5891
```
1. 打包 `mvn clean & mvn install`
2. 复制class文件夹下的 `server.properties`,保证`lib`、`server-proxy-1.0.jar`、`server.properties` 在同一级目录
2. 启动 `java -jar server-proxy-1.0.jar`
> 客户端(部署内网环境)
```properties
# 客户端名称 不同客户端需要修改
client.name=admin
# 服务器IP
client.server.ip=127.0.0.1
# 服务器端口
client.server.port=5891
# 代理端口
client.proxy.port=7777
# 真实服务器IP
server.real.ip=127.0.0.1
# 真实服务器端口
server.real.port=8888
```
1. 启动 `java -cp server-proxy-1.0.jar top.icss.NettyClient`
