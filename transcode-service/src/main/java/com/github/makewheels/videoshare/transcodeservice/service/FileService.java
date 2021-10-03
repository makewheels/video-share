package com.github.makewheels.videoshare.transcodeservice.service;

import com.github.makewheels.videoshare.common.bean.OssFile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("file-service")
public interface FileService {
    @PostMapping
    OssFile getOssFileByMongoId(@RequestParam String mongoId);

    @PostMapping
    OssFile getOssFileByVideoMongoId(@RequestParam String videoMongoId);
}
