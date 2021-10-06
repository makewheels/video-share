FROM openjdk:8-jre-alpine
EXPOSE 5018
ADD "target/api-gateway-1.0.0.jar" "/app.jar"
CMD ["java","-jar","-Dspring.profiles.active=product","/app.jar"]