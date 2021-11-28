# 先删除之前的容器
echo "移除之前的容器>>>"
docker ps -a | grep server-proxy | awk '{print $1}'| xargs docker rm -f
# 删除之前的镜像
echo "移除之前的镜像>>>"
docker rmi server-proxy
# 构建镜像
mvn docker:build -t server-proxy
# 打印当前镜像
echo "当前镜像>>>"
docker images | grep server-proxy
# 启动容器
echo "容器启动中>>>"
docker run -d -p --name my-server-proxy  server-proxy
# 打印当前容器
echo "当前容器>>>"
docker images | grep my-server-proxy
echo "启动服务成功！"
