## 基于Netty做的内网穿透，暂不支持Https

## 特性
1. 支持http\tcp\ws

## 使用
> 服务端(部署在外网服务器) proxy-server.properties
```properties
# 外网服务器地址
server.host=127.0.0.1
# 服务器端口
server.port=5891
```
1. 打包 `mvn clean & mvn install`
2. 复制class文件夹下的 `config/proxy-server.properties`,保证`lib`、`proxy-server.jar`、`config/proxy-server.properties` 在同一级目录
3. 启动 `java -jar proxy-server.jar`

> 客户端(部署内网环境) proxy-client.properties
```properties
# 代理服务器地址端口
server.host=127.0.0.1
server.port=5891
# 用户名密码
user.username=admin
user.password=123456
# 代理地址
proxy.host=127.0.0.1
proxy.port=8080
# 远程访问端口
remote.port=7000

```
1. 打包 `mvn clean & mvn install`
2. 复制class文件夹下的 `config/proxy-client.properties`,保证`lib`、`proxy-client.jar`、`config/proxy-client.properties` 在同一级目录
3. 启动 `java -jar proxy-client.jar`
