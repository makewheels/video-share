package com.github.makewheels.videoshare.transcodeservice.repository;

import com.github.makewheels.videoshare.common.bean.transcode.TranscodeTask;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class TranscodeTaskRepository {
    @Resource
    MongoTemplate mongoTemplate;

    public UpdateResult updateByMongoId(String mongoId, String key, Object value) {
        return mongoTemplate.updateFirst(
                Query.query(Criteria.where("mongoId").is(mongoId)),
                Update.update(key, value), TranscodeTask.class);
    }
}
