docker run -d -p 9000:9000 --name=portainer --restart=always \
-v /var/run/docker.sock:/var/run/docker.sock \
-v portainer_data:/data portainer/portainer

docker run -itd -p 9200:9200 -p 9300:9300 \
--restart=always \
--name elasticsearch elasticsearch

docker run --env MODE=standalone --name nacos -itd \
--restart=always -p 8848:8848 nacos/nacos-server

docker run -itd --name redis -p 6379:6379 --restart=always redis

docker run -itd --name=mongodb -p 27017:27017 --restart=always \
mongo --bind_ip_all
