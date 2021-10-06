FROM openjdk:8-jre-alpine
EXPOSE 5019
ADD "target/search-service-1.0.0.jar" "/app.jar"
CMD ["java","-jar","-Dspring.profiles.active=product","/app.jar"]