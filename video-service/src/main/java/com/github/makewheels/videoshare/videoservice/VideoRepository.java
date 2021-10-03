package com.github.makewheels.videoshare.videoservice;

import com.github.makewheels.videoshare.common.bean.Video;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class VideoRepository {
    @Resource
    private MongoTemplate mongoTemplate;

    public Video getVideoByMongoId(String mongoId) {
        return mongoTemplate.findById(mongoId, Video.class);
    }
}
