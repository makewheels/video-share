package com.github.makewheels.videoshare.videoservice.video;

import com.github.makewheels.universaluserservice.common.bean.User;
import com.github.makewheels.videoshare.common.bean.Video;
import com.github.makewheels.videoshare.common.response.Result;
import com.github.makewheels.videoshare.videoservice.bean.createvideo.CreateVideoRequest;
import com.github.makewheels.videoshare.videoservice.bean.createvideo.CreateVideoResponse;
import com.github.makewheels.videoshare.videoservice.bean.playurl.PlayUrl;
import com.github.makewheels.videoshare.videoservice.bean.playurl.PlayUrlRequest;
import com.github.makewheels.videoshare.videoservice.bean.videoinfo.VideoInfoRequest;
import com.github.makewheels.videoshare.videoservice.bean.videoinfo.VideoInfoResponse;
import com.github.makewheels.videoshare.videoservice.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("video")
public class VideoController {
    @Resource
    private UserService userService;
    @Resource
    private VideoService videoService;

    @PostMapping("createVideo")
    public Result<CreateVideoResponse> createVideo(
            HttpServletRequest request, @RequestBody CreateVideoRequest createVideoRequest) {
        User user = userService.getByLoginToken(request.getHeader("loginToken"));
        return videoService.createVideo(user, createVideoRequest);
    }

    @PostMapping("getVideoByMongoId")
    public Video getVideoByMongoId(@RequestParam String videoMongoId) {
        return videoService.getVideoByMongoId(videoMongoId);
    }

    @PostMapping("getVideoInfoByVideoId")
    public Result<VideoInfoResponse> getVideoInfoByVideoId(
            HttpServletRequest request, @RequestBody VideoInfoRequest videoInfoRequest) {
        User user = userService.getByLoginToken(request.getHeader("loginToken"));
        return videoService.getVideoInfoByVideoId(user, videoInfoRequest.getVideoId());
    }

    @PostMapping("getPlayUrl")
    public Result<List<PlayUrl>> getPlayUrl(
            HttpServletRequest request, @RequestBody PlayUrlRequest playUrlRequest) {
        User user = userService.getByLoginToken(request.getHeader("loginToken"));
        return videoService.getPlayUrl(user, Long.parseLong(playUrlRequest.getSnowflakeId()));
    }
}
