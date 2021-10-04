package com.github.makewheels.videoshare.transcodeservice.mq;

import com.alibaba.fastjson.JSON;
import com.github.makewheels.videoshare.common.bean.file.FileMongoId;
import com.github.makewheels.videoshare.common.bean.file.OssFile;
import com.github.makewheels.videoshare.common.mq.Group;
import com.github.makewheels.videoshare.common.mq.Topic;
import com.github.makewheels.videoshare.transcodeservice.TranscodeService;
import com.github.makewheels.videoshare.transcodeservice.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 转码服务监听：
 * 当上传完成时，发起转码
 */
@Service
@Slf4j
@RocketMQMessageListener(
        consumerGroup = Group.GROUP_DEFAULT,
        topic = Topic.TOPIC_ORIGINAL_FILE_READY,
        messageModel = MessageModel.BROADCASTING
)
public class MQOriginalFileReadyListener implements RocketMQListener<String> {
    @Resource
    private TranscodeService transcodeService;
    @Resource
    private FileService fileService;

    @Override
    public void onMessage(String message) {
        String fileMongoId = JSON.parseObject(message, FileMongoId.class)
                .getFileMongoId();
        log.info("转码服务监听到RocketMQ, fileMongoId = " + fileMongoId);
        OssFile file = fileService.getOssFileByMongoId(fileMongoId);
        String videoMongoId = file.getVideoMongoId();
        log.info("发起转码: videoMongoId = " + videoMongoId);
        transcodeService.transcodeVideo(videoMongoId);
    }
}