package com.github.makewheels.videoshare.fileservice.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(consumerGroup = "GID_g", topic = "tttttttt", messageModel = MessageModel.BROADCASTING)
public class MqService implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        log.info(message);
    }
}
