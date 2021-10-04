package com.github.makewheels.videoshare.videoservice.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class RocketMQService {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void send(String topic, Object payload) {
        log.info("发送消息队列: topic = " + topic + " , payload = " + JSON.toJSONString(payload));
        rocketMQTemplate.convertAndSend(topic, payload);
    }
}
