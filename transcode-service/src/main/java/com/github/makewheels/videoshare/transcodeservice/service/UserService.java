package com.github.makewheels.videoshare.transcodeservice.service;

import com.github.makewheels.universaluserservice.common.bean.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("universal-user-service")
public interface UserService {

    /**
     * 通过mongo id获取用户
     *
     * @param mongoId
     * @return
     */
    @PostMapping("user/getUserByMongoId")
    User getUserByMongoId(@RequestParam String mongoId);

    /**
     * 通过雪花id获取用户
     *
     * @param snowflakeId
     * @return
     */
    @PostMapping("user/getUserBySnowflakeId")
    User getUserBySnowflakeId(@RequestParam long snowflakeId);

}
