FROM registry.cn-beijing.aliyuncs.com/b4/openjdk:8-jdk-alpine
EXPOSE 5019
ADD "target/search-service-1.0.0.jar" "/app.jar"
CMD ["java","-jar","-Dspring.profiles.active=product","/app.jar"]