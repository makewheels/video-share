package com.github.makewheels.videoshare.fileservice.mq;

import com.alibaba.fastjson.JSON;
import com.github.makewheels.videoshare.common.bean.file.FileStatus;
import com.github.makewheels.videoshare.common.bean.file.OssFile;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeJob;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeStatus;
import com.github.makewheels.videoshare.common.mq.Group;
import com.github.makewheels.videoshare.common.mq.Topic;
import com.github.makewheels.videoshare.fileservice.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
@RocketMQMessageListener(
        consumerGroup = Group.GROUP_DEFAULT,
        topic = Topic.TOPIC_TRANSCODE_JOB_STATUS_CHANGED,
        messageModel = MessageModel.BROADCASTING
)
public class MQTranscodeTaskJobStatusChangedListener implements RocketMQListener<String> {
    @Resource
    private FileService fileService;
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public void onMessage(String message) {
        TranscodeJob transcodeJob = JSON.parseObject(message, TranscodeJob.class);
        //如果转码成功，新增oss记录
        //那么面试题又来了，怎么保证幂等性，避免重复插入？
        if (StringUtils.equals(transcodeJob.getStatus(), TranscodeStatus.TRANSCODE_SUCCESS)) {
            String toObject = transcodeJob.getToObject();
            OssFile ossFile = fileService.createOssFile(toObject);
            ossFile.setStatus(FileStatus.READY);
            ossFile.setUserMongoId(transcodeJob.getUserMongoId());
            ossFile.setVideoMongoId(transcodeJob.getVideoMongoId());
            mongoTemplate.save(ossFile);
        }
    }
}
