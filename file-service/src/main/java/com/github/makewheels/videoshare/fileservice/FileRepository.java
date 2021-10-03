package com.github.makewheels.videoshare.fileservice;

import com.github.makewheels.videoshare.common.bean.file.OssFile;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class FileRepository {
    @Resource
    private MongoTemplate mongoTemplate;

    public UpdateResult updateByMongoId(String mongoId, String key, Object value) {
        return mongoTemplate.updateFirst(
                Query.query(Criteria.where("mongoId").is(mongoId)),
                Update.update(key, value), OssFile.class);
    }

    public OssFile findOne(String key, Object value) {
        Query query = Query.query(Criteria.where(key).is(value));
        return mongoTemplate.findOne(query, OssFile.class);
    }

    public OssFile findBySnowflakeId(long snowflakeId) {
        return findOne("snowflakeId", snowflakeId);
    }

    public OssFile findByMongoId(String mongoId) {
        return mongoTemplate.findById(mongoId, OssFile.class);
    }
}
