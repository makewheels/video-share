package com.github.makewheels.videoshare.transcodeservice.service;

import com.github.makewheels.videoshare.common.bean.file.OssFile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("file-service")
public interface FileService {
    @PostMapping("file/getOssFileByMongoId")
    OssFile getOssFileByMongoId(@RequestParam String mongoId);

    @PostMapping("file/getOssFileByVideoMongoId")
    OssFile getOssFileByVideoMongoId(@RequestParam String videoMongoId);

    @PostMapping("file/getSignedUrl")
    Map<String, String> getSignedUrl(@RequestBody List<OssSignRequest> ossSignRequests);
}
