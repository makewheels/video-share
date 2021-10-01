package com.github.makewheels.videoshare.videoservice;

import com.github.makewheels.universaluserservice.common.bean.User;
import com.github.makewheels.videoshare.common.response.Result;
import com.github.makewheels.videoshare.videoservice.bean.CreateVideoRequest;
import com.github.makewheels.videoshare.videoservice.bean.CreateVideoResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("video")
public class VideoController {
    @Resource
    private UserService userService;
    @Resource
    private VideoService videoService;

    @PostMapping("createVideo")
    private Result<CreateVideoResponse> createVideo(
            HttpServletRequest request, @RequestBody CreateVideoRequest createVideoRequest) {
        User user = userService.getByLoginToken(request.getHeader("loginToken"));
        return videoService.createVideo(user, createVideoRequest);
    }
}
