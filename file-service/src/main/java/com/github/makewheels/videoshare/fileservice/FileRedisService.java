package com.github.makewheels.videoshare.fileservice;

import com.alibaba.fastjson.JSON;
import com.github.makewheels.videoshare.common.bean.Video;
import com.github.makewheels.videoshare.common.redis.RedisService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FileRedisService {
    @Resource
    private RedisService redisService;

    public Video getVideoByUploadToken(String uploadToken) {
        String json = (String) redisService.get(RedisKey.uploadToken(uploadToken));
        return JSON.parseObject(json, Video.class);
    }
}
