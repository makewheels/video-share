package com.github.makewheels.videoshare.fileservice.mq;

import com.github.makewheels.videoshare.common.mq.Topic;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RocketMQService {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void send(String topic, Object payload) {
        rocketMQTemplate.convertAndSend(topic, payload);
    }
}
