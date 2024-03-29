package com.github.makewheels.videoshare.searchservice.service;

import com.github.makewheels.videoshare.common.bean.video.Video;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("video-service")
public interface VideoService {

    @PostMapping("video/getVideoByMongoId")
    Video getVideoByMongoId(@RequestParam String videoMongoId);

}
