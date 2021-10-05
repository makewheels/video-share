nohup java -jar -Dspring.profiles.active=product \
/home/admin/video-share/file-service/target/file-service-1.0.0.jar \
>> /home/admin/video-share/file-service/logs/start.log 2>&1 &

nohup java -jar -Dspring.profiles.active=product \
/home/admin/video-share/search-service/target/search-service-1.0.0.jar \
>> /home/admin/video-share/search-service/logs/start.log 2>&1 &

nohup java -jar -Dspring.profiles.active=product \
/home/admin/video-share/transcode-service/target/transcode-service-1.0.0.jar \
>> /home/admin/video-share/transcode-service/logs/start.log 2>&1 &

nohup java -jar -Dspring.profiles.active=product \
/home/admin/video-share/video-service/target/video-service-1.0.0.jar \
>> /home/admin/video-share/video-service/logs/start.log 2>&1 &
