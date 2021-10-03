package com.github.makewheels.videoshare.videoservice;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.makewheels.universaluserservice.common.bean.User;
import com.github.makewheels.videoshare.common.response.ErrorCode;
import com.github.makewheels.videoshare.common.response.Result;
import com.github.makewheels.videoshare.videoservice.bean.CreateVideoRequest;
import com.github.makewheels.videoshare.videoservice.bean.CreateVideoResponse;
import com.github.makewheels.videoshare.common.bean.Video;
import com.github.makewheels.videoshare.videoservice.bean.VideoInfoResponse;
import com.github.makewheels.videoshare.videoservice.redis.VideoRedisService;
import com.github.makewheels.videoshare.videoservice.util.VideoStatus;
import com.github.makewheels.videoshare.videoservice.util.VideoSnowflakeUtil;
import com.github.makewheels.videoshare.videoservice.util.VideoVisibility;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
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
        Long expireTimeLength = request.getExpireTimeLength();
        //设置过期时间
        if (expireTimeLength != 0) {
            Date date = new Date();
            date.setTime(System.currentTimeMillis() + request.getExpireTimeLength());
            video.setExpireTime(date);
            video.setHasExpireTime(true);
        } else {
            video.setHasExpireTime(false);
        }

        video.setStatus(VideoStatus.STATUS_CREATE);

        //上传路径
        String uploadPath = "video/" + user.getSnowflakeId() + "/" + video.getSnowflakeId() + "/upload/"
                + video.getSnowflakeId() + "." + FilenameUtils.getExtension(request.getOriginalFilename());
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

    public Result<VideoInfoResponse> getVideoInfoByVideoId(User user, String videoId) {
        Video video = videoRepository.getVideoByVideoId(videoId);
        //如果视频不存在
        if (video == null) {
            return Result.error(ErrorCode.VIDEO_ID_NOT_EXIST);
        }
        //检查权限
        String visibility = video.getVisibility();
        //如果是私有的
        if (visibility.equals(VideoVisibility.PRIVATE)) {
            //如果这个视频不是他的
            if (!user.getMongoId().equals(video.getUserMongoId())) {
                return Result.error(ErrorCode.PERMISSION_CHECK_FAIL);
            }
        }
        //检查过期时间
        if (BooleanUtils.isTrue(video.getHasExpireTime())) {
            if (System.currentTimeMillis() > video.getExpireTime().getTime()) {
                return Result.error(ErrorCode.VIDEO_EXPIRED);
            }
        }
        VideoInfoResponse videoInfo = new VideoInfoResponse();
        BeanUtils.copyProperties(video, videoInfo);
        videoInfo.setSnowflakeId(video.getSnowflakeId() + "");
        return Result.ok(videoInfo);
    }
}
