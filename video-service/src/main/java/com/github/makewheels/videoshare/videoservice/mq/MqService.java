package com.github.makewheels.videoshare.videoservice.mq;

import com.alibaba.fastjson.JSON;
import com.github.makewheels.videoshare.common.bean.file.FileMongoId;
import com.github.makewheels.videoshare.common.bean.file.OssFile;
import com.github.makewheels.videoshare.common.bean.video.Video;
import com.github.makewheels.videoshare.common.bean.video.VideoStatus;
import com.github.makewheels.videoshare.common.mq.Group;
import com.github.makewheels.videoshare.common.mq.Topic;
import com.github.makewheels.videoshare.videoservice.service.FileService;
import com.github.makewheels.videoshare.videoservice.video.VideoRepository;
import com.github.makewheels.videoshare.videoservice.video.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 转码服务监听：
 * 当上传完成时，改变视频状态
 */
@Service
@Slf4j
@RocketMQMessageListener(
        consumerGroup = Group.GROUP_DEFAULT,
        topic = Topic.TOPIC_ORIGINAL_FILE_READY,
        messageModel = MessageModel.BROADCASTING
)
public class MqService implements RocketMQListener<String> {
    @Resource
    private FileService fileService;
    @Resource
    private VideoService videoService;
    @Resource
    private VideoRepository videoRepository;

    @Override
    public void onMessage(String message) {
        String fileMongoId = JSON.parseObject(message, FileMongoId.class).getFileMongoId();
        log.info("视频微服务监听到RocketMQ消息，开始改变视频状态, fileMongoId = " + fileMongoId);
        OssFile file = fileService.getOssFileByMongoId(fileMongoId);
        String videoMongoId = file.getVideoMongoId();
        Video video = videoService.getVideoByMongoId(videoMongoId);
        video.setStatus(VideoStatus.STATUS_ORIGINAL_FILE_READY);
        log.info("改变视频状态为：源文件就绪, videoMongoId = " + videoMongoId + " , " + JSON.toJSONString(video));
        videoRepository.updateByMongoId(videoMongoId, "status", VideoStatus.STATUS_ORIGINAL_FILE_READY);
    }
}