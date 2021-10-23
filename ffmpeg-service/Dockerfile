FROM registry.cn-beijing.aliyuncs.com/b4/openjdk:8-jdk-alpine
EXPOSE 5020
ADD ffmpeg-release-amd64-static.tar.xz /usr/local

ENV FFMPEG /usr/local/ffmpeg-4.4-amd64-static/ffmpeg
ENV FFPROBE /usr/local/ffmpeg-4.4-amd64-static/ffprobe

ADD "target/ffmpeg-service-1.0.0.jar" "/app.jar"
CMD ["java","-jar","-Dspring.profiles.active=product","/app.jar"]