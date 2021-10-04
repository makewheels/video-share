package com.github.makewheels.videoshare.videoservice.mq;

import com.alibaba.fastjson.JSON;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeTask;
import com.github.makewheels.videoshare.common.bean.video.VideoMongoId;
import com.github.makewheels.videoshare.common.bean.video.VideoStatus;
import com.github.makewheels.videoshare.common.mq.Group;
import com.github.makewheels.videoshare.common.mq.Topic;
import com.github.makewheels.videoshare.videoservice.video.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 当转码task完成时，更新视频状态
 */
@Service
@Slf4j
@RocketMQMessageListener(
        consumerGroup = Group.GROUP_DEFAULT,
        topic = Topic.TOPIC_TRANSCODE_TASK_FINISHED,
        messageModel = MessageModel.BROADCASTING
)
public class MQTranscodeTaskFinishListener implements RocketMQListener<String> {
    @Resource
    private VideoRepository videoRepository;
    @Resource
    private RocketMQService rocketMQService;

    @Override
    public void onMessage(String message) {
        TranscodeTask transcodeTask = JSON.parseObject(message, TranscodeTask.class);
        log.info("视频微服务监听到RocketMQ消息：topic= {}, transcodeTask = {}",
                Topic.TOPIC_TRANSCODE_TASK_FINISHED, JSON.toJSONString(transcodeTask));
        String videoMongoId = transcodeTask.getVideoMongoId();
        videoRepository.updateByMongoId(videoMongoId, "status", VideoStatus.READY);
        //再发一条消息，通知搜索服务更新
        rocketMQService.send(Topic.TOPIC_VIDEO_INFO_CHANGED, new VideoMongoId(videoMongoId));
    }
}
