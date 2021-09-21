package com.github.makewheels.videoshare;

import com.github.makewheels.universaluserservice.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("video")
public class VideoController {
    @Resource
    private UserService userService;
}
