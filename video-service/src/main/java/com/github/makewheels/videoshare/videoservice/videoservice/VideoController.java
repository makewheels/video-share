package com.github.makewheels.videoshare.videoservice.videoservice;

import com.github.makewheels.universaluserservice.bean.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("video")
public class VideoController {
    @Resource
    private UserService userService;

    @PostMapping("getUser")
    public User getUser() {
        User user = userService.getUserBySnowflakeId(1440303515921682432L);
        return user;
    }
}
