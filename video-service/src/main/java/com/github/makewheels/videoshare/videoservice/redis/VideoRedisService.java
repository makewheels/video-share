package com.github.makewheels.videoshare.videoservice.redis;

import com.alibaba.fastjson.JSON;
import com.github.makewheels.videoshare.common.redis.RedisService;
import com.github.makewheels.videoshare.common.redis.RedisTime;
import com.github.makewheels.videoshare.common.bean.video.Video;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class VideoRedisService {
    @Resource
    private RedisService redisService;

    public void setUploadToken(String uploadToken, Video video) {
        redisService.set(RedisKey.uploadToken(uploadToken),
                JSON.toJSONString(video), RedisTime.TEN_MINUTES);
    }
}
