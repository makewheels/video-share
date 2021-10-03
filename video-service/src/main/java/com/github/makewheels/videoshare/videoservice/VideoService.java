package com.github.makewheels.videoshare.videoservice;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.makewheels.universaluserservice.common.bean.User;
import com.github.makewheels.videoshare.common.response.Result;
import com.github.makewheels.videoshare.videoservice.bean.CreateVideoRequest;
import com.github.makewheels.videoshare.videoservice.bean.CreateVideoResponse;
import com.github.makewheels.videoshare.common.bean.Video;
import com.github.makewheels.videoshare.videoservice.redis.VideoRedisService;
import com.github.makewheels.videoshare.videoservice.util.VideoConstants;
import com.github.makewheels.videoshare.videoservice.util.VideoSnowflakeUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class VideoService {
    @Resource
    private VideoRepository videoRepository;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private VideoRedisService videoRedisService;

    /**
     * 上传前创建视频
     */
    public Result<CreateVideoResponse> createVideo(User user, CreateVideoRequest request) {
        //创建视频
        Video video = new Video();
        video.setVideoId(RandomUtil.randomString(11));
        video.setSnowflakeId(VideoSnowflakeUtil.get());

        video.setUserMongoId(user.getMongoId());
        video.setCreateTime(new Date());
        video.setDescription(request.getDescription());
        video.setTitle(request.getTitle());
        video.setVisibility(request.getVisibility());
        video.setStatus(VideoConstants.STATUS_CREATE);

        //上传路径
        String uploadPath = "video/" + user.getSnowflakeId() + "/upload/" + video.getSnowflakeId()
                + "." + FilenameUtils.getExtension(request.getOriginalFilename());
        video.setUploadPath(uploadPath);

        //保存视频到数据库
        mongoTemplate.save(video);

        String uploadToken = IdUtil.simpleUUID();
        videoRedisService.setUploadToken(uploadToken, video);

        CreateVideoResponse response = new CreateVideoResponse();
        response.setUploadToken(uploadToken);
        response.setUploadPath(uploadPath);
        return Result.ok(response);
    }

    public Video getVideoByMongoId(String videoMongoId) {
        //TODO 应该改成从缓存获取
        return videoRepository.getVideoByMongoId(videoMongoId);
    }
}
