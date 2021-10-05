tar zxvf /home/admin/${PIPELINE_NAME}/package.tgz -C /home/admin/${PIPELINE_NAME}/

chmod a+x /home/admin/${PIPELINE_NAME}/video-service/deploy.sh
sh /home/admin/${PIPELINE_NAME}/video-service/deploy.sh restart

chmod a+x /home/admin/${PIPELINE_NAME}/file-service/deploy.sh
sh /home/admin/${PIPELINE_NAME}/file-service/deploy.sh restart

chmod a+x /home/admin/${PIPELINE_NAME}/transcode-service/deploy.sh
sh /home/admin/${PIPELINE_NAME}/transcode-service/deploy.sh restart

chmod a+x /home/admin/${PIPELINE_NAME}/search-service/deploy.sh
sh /home/admin/${PIPELINE_NAME}/search-service/deploy.sh restart

chmod a+x /home/admin/${PIPELINE_NAME}/api-gateway/deploy.sh
sh /home/admin/${PIPELINE_NAME}/api-gateway/deploy.sh restart

