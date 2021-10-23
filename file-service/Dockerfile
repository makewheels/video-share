FROM registry.cn-beijing.aliyuncs.com/b4/openjdk:8-jdk-alpine
EXPOSE 5016
ADD "target/file-service-1.0.0.jar" "/app.jar"
CMD ["java","-jar","-Dspring.profiles.active=product","/app.jar"]