package com.github.makewheels.videoshare.videoservice.mq;

import com.alibaba.fastjson.JSON;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeStatus;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeTask;
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

    @Override
    public void onMessage(String message) {
        TranscodeTask transcodeTask = JSON.parseObject(message, TranscodeTask.class);
        String videoMongoId = transcodeTask.getVideoMongoId();
        videoRepository.updateByMongoId(videoMongoId, "status", VideoStatus.STATUS_READY);
    }
}
