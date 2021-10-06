FROM maven:3.6.3-jdk-8 AS builder
ADD ./pom.xml pom.xml
ADD ./src src/
RUN mvn clean package
FROM openjdk:8-jre-alpine
COPY --from=builder "target/video-service-1.0.0.jar" "app.jar"
EXPOSE 80
CMD ["java","-jar","-Dspring.profiles.active=product","app.jar"]

#FROM maven:3.6.3-jdk-8
#ADD ./pom.xml pom.xml
#ADD ./src src/
#RUN mvn clean package
#EXPOSE 5007
#CMD ["java","-jar","-Dspring.profiles.active=product","target/chat-server-0.0.1-SNAPSHOT.jar"]