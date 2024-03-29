package com.github.makewheels.videoshare.videoservice.video;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.makewheels.universaluserservice.common.bean.User;
import com.github.makewheels.videoshare.common.bean.file.OssSignRequest;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeJob;
import com.github.makewheels.videoshare.common.response.ErrorCode;
import com.github.makewheels.videoshare.common.response.Result;
import com.github.makewheels.videoshare.videoservice.bean.createvideo.CreateVideoRequest;
import com.github.makewheels.videoshare.videoservice.bean.createvideo.CreateVideoResponse;
import com.github.makewheels.videoshare.common.bean.video.Video;
import com.github.makewheels.videoshare.videoservice.bean.getvideolist.GetVideoListRequest;
import com.github.makewheels.videoshare.videoservice.bean.getvideolist.GetVideoListResponse;
import com.github.makewheels.videoshare.videoservice.bean.playurl.PlayUrl;
import com.github.makewheels.videoshare.videoservice.bean.videoinfo.VideoInfoResponse;
import com.github.makewheels.videoshare.videoservice.redis.VideoRedisService;
import com.github.makewheels.videoshare.videoservice.service.FileService;
import com.github.makewheels.videoshare.videoservice.service.TranscodeService;
import com.github.makewheels.videoshare.common.bean.video.VideoStatus;
import com.github.makewheels.videoshare.videoservice.util.VideoSnowflakeUtil;
import com.github.makewheels.videoshare.common.bean.video.VideoVisibility;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class VideoService {
    @Resource
    private VideoRepository videoRepository;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private VideoRedisService videoRedisService;
    @Resource
    private TranscodeService transcodeService;
    @Resource
    private FileService fileService;

    @Getter
    @Value("${domain}")
    private String domain;

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

        video.setStatus(VideoStatus.CREATE);

        //上传路径
        String uploadPath = "video/" + user.getSnowflakeId() + "/" + video.getSnowflakeId() + "/original/"
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

    /**
     * 检查视频
     *
     * @param user
     * @param video
     * @return
     */
    private ErrorCode checkVideo(User user, Video video) {
        //如果视频不存在
        if (video == null) {
            return ErrorCode.VIDEO_ID_NOT_EXIST;
        }
        //检查权限
        String visibility = video.getVisibility();
        //如果是私有的
        if (visibility.equals(VideoVisibility.PRIVATE)) {
            //如果这个视频不是他的
            if (!user.getMongoId().equals(video.getUserMongoId())) {
                return ErrorCode.PERMISSION_CHECK_FAIL;
            }
        }
        //检查过期时间
        if (BooleanUtils.isTrue(video.getHasExpireTime())) {
            if (System.currentTimeMillis() > video.getExpireTime().getTime()) {
                return ErrorCode.VIDEO_EXPIRED;
            }
        }
        //视频是否已就绪
        if (!video.getStatus().equals(VideoStatus.READY)) {
            return ErrorCode.VIDEO_NOT_READY;
        }
        return null;
    }

    /**
     * 根据videoId获取视频信息
     *
     * @param user
     * @param videoId
     * @return
     */
    public Result<VideoInfoResponse> getVideoInfoByVideoId(User user, String videoId) {
        Video video = videoRepository.getVideoByVideoId(videoId);
        ErrorCode errorCode = checkVideo(user, video);
        if (errorCode != null) {
            return Result.error(errorCode);
        }
        VideoInfoResponse videoInfo = new VideoInfoResponse();
        BeanUtils.copyProperties(video, videoInfo);
        videoInfo.setSnowflakeId(video.getSnowflakeId() + "");
        return Result.ok(videoInfo);
    }

    /**
     * 根据雪花id获取视频播放信息
     *
     * @param user
     * @param snowflakeId
     * @return
     */
    public Result<List<PlayUrl>> getPlayUrl(User user, long snowflakeId) {
        Video video = videoRepository.getVideoBySnowflakeId(snowflakeId);
        ErrorCode errorCode = checkVideo(user, video);
        if (errorCode != null) {
            return Result.error(errorCode);
        }
        //获取转码文件
        List<TranscodeJob> transcodeJobs = transcodeService.getTranscodeJobsByVideoMongoId(video.getMongoId());

        //获取签名地址
        List<OssSignRequest> ossSignRequests = new ArrayList<>(transcodeJobs.size());
        for (TranscodeJob transcodeJob : transcodeJobs) {
            OssSignRequest ossSignRequest = new OssSignRequest();
            ossSignRequest.setKey(transcodeJob.getToObject() + ".m3u8");
            //TODO 应该设置为视频时长的两倍
            ossSignRequest.setTime(3 * 60 * 60 * 1000L);
            ossSignRequests.add(ossSignRequest);
        }
        Map<String, String> map = fileService.getSignedUrl(ossSignRequests);

        //组装结果
        List<PlayUrl> playUrls = new ArrayList<>(transcodeJobs.size());
        for (TranscodeJob transcodeJob : transcodeJobs) {
            PlayUrl playUrl = new PlayUrl();
            playUrl.setResolution(transcodeJob.getTargetResolution());
            playUrl.setUrl(map.get(transcodeJob.getToObject() + ".m3u8"));
            playUrls.add(playUrl);
        }
        return Result.ok(playUrls);
    }

    private String getWatchUrl(String videoId) {
        return "http://" + getDomain() + "/watch?v=" + videoId;
    }

    public Result<List<GetVideoListResponse>> getVideoList(User user, GetVideoListRequest getVideoListRequest) {
        Query query = Query.query(Criteria.where("userMongoId").is(user.getMongoId()));
        query.addCriteria(Criteria.where("status").ne(VideoStatus.CREATE));
        List<Video> videos = mongoTemplate.find(query, Video.class);
        List<GetVideoListResponse> list = new ArrayList<>(videos.size());
        videos.forEach(video -> {
            GetVideoListResponse videoInfo = new GetVideoListResponse();
            BeanUtils.copyProperties(video, videoInfo);
            videoInfo.setSnowflakeId(video.getSnowflakeId() + "");
            videoInfo.setWatchUrl(getWatchUrl(video.getVideoId()));
            list.add(videoInfo);
        });
        return Result.ok(list);
    }
}
