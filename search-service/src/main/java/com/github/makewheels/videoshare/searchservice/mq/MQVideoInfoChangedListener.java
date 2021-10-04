package com.github.makewheels.videoshare.searchservice.mq;

import com.alibaba.fastjson.JSON;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeTask;
import com.github.makewheels.videoshare.common.bean.video.Video;
import com.github.makewheels.videoshare.common.bean.video.VideoMongoId;
import com.github.makewheels.videoshare.common.elasticsearch.EsVideo;
import com.github.makewheels.videoshare.common.mq.Group;
import com.github.makewheels.videoshare.common.mq.Topic;
import com.github.makewheels.videoshare.searchservice.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 当转码task完成时，更新视频状态
 */
@Service
@Slf4j
@RocketMQMessageListener(
        consumerGroup = Group.GROUP_DEFAULT,
        topic = Topic.TOPIC_VIDEO_INFO_CHANGED,
        messageModel = MessageModel.BROADCASTING
)
public class MQVideoInfoChangedListener implements RocketMQListener<String> {
    @Resource
    private VideoService videoService;
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void onMessage(String message) {
        String videoMongoId = JSON.parseObject(message, VideoMongoId.class).getVideoMongoId();
        log.info("搜索微服务收到RocketMQ消息：topic= {}, videoMongoId = {}",
                Topic.TOPIC_VIDEO_INFO_CHANGED, videoMongoId);
        Video video = videoService.getVideoByMongoId(videoMongoId);
        EsVideo esVideo = new EsVideo();
        BeanUtils.copyProperties(video, esVideo);
        elasticsearchRestTemplate.save(esVideo);
        log.info("向ElasticSearch插入: " + JSON.toJSONString(esVideo));
    }
}
