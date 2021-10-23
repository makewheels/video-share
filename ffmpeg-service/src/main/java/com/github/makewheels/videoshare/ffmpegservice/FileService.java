package com.github.makewheels.videoshare.ffmpegservice;

import com.github.makewheels.videoshare.common.bean.file.OssFile;
import com.github.makewheels.videoshare.common.bean.file.OssSignRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient("file-service")
public interface FileService {
    @PostMapping("file/getOssFileByMongoId")
    OssFile getOssFileByMongoId(@RequestParam String mongoId);

    @PostMapping("file/getOssFileByVideoMongoId")
    OssFile getOssFileByVideoMongoId(@RequestParam String videoMongoId);

    @PostMapping("file/getSignedUrl")
    Map<String, String> getSignedUrl(@RequestBody List<OssSignRequest> ossSignRequests);

    @PostMapping("file/getInternalSignedUrl")
    Map<String, String> getgetInternalSignedUrl(@RequestBody List<OssSignRequest> ossSignRequests);
}
