package com.github.makewheels.videoshare.videoservice.service;

import com.github.makewheels.videoshare.common.bean.OssSignRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient("file-service")
public interface FileService {
    @PostMapping("file/getSignedUrl")
    Map<String, String> getSignedUrl(@RequestBody List<OssSignRequest> ossSignRequests);
}
