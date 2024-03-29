package com.github.makewheels.videoshare.videoservice.video;

import com.github.makewheels.videoshare.common.bean.file.OssFile;
import com.github.makewheels.videoshare.common.bean.video.Video;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class VideoRepository {
    @Resource
    private MongoTemplate mongoTemplate;

    public Video getVideoByMongoId(String mongoId) {
        return mongoTemplate.findById(mongoId, Video.class);
    }

    public Video findOne(String key, Object value) {
        Query query = Query.query(Criteria.where(key).is(value));
        return mongoTemplate.findOne(query, Video.class);
    }

    public List<Video> find(String key, Object value) {
        Query query = Query.query(Criteria.where(key).is(value));
        return mongoTemplate.find(query, Video.class);
    }

    public Video getVideoByVideoId(String videoId) {
        return findOne("videoId", videoId);
    }

    public Video getVideoBySnowflakeId(long snowflakeId) {
        return findOne("snowflakeId", snowflakeId);
    }

    public UpdateResult updateByMongoId(String mongoId, String key, Object value) {
        return mongoTemplate.updateFirst(
                Query.query(Criteria.where("mongoId").is(mongoId)),
                Update.update(key, value), Video.class);
    }
}
