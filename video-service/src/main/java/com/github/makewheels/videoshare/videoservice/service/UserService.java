package com.github.makewheels.videoshare.videoservice.service;

import com.github.makewheels.universaluserservice.common.bean.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("universal-user-service")
public interface UserService {
    /**
     * 创建空用户
     */
    @PostMapping("user/createEmpty")
    User createUser(@RequestParam String appId);

    /**
     * 创建用户：根据用户名密码
     */
    @PostMapping("user/createByUsernameAndPassword")
    User createUser(@RequestParam String appId, @RequestParam String username,
                    @RequestParam String password);

    /**
     * 通过mongo id获取用户
     */
    @PostMapping("user/getUserByMongoId")
    User getUserByMongoId(@RequestParam String mongoId);

    /**
     * 通过雪花id获取用户
     */
    @PostMapping("user/getUserBySnowflakeId")
    User getUserBySnowflakeId(@RequestParam long snowflakeId);

    @PostMapping("user/getByLoginToken")
    User getByLoginToken(@RequestParam String loginToken);
}