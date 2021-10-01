package com.github.makewheels.videoshare.videoservice;

import com.github.makewheels.universaluserservice.common.bean.User;
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
        return userService.getUserBySnowflakeId(1443616374344781824L);
    }
}
