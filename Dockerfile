#FROM openjdk:8-jdk-alpine
FROM hub.c.163.com/dwyane/openjdk:8
MAINTAINER 14163548@qq.com
EXPOSE 5891
EXPOSE 7000
CMD echo "---Docker容器环境配置成功，即将运行---"
COPY ./target/*.jar /app.jar
COPY ./target/lib /lib
ENTRYPOINT ["java","-jar","/app.jar"]
CMD echo "---服务开启成功，访问端口:5891---"