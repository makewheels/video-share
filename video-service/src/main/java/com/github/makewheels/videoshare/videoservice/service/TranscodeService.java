package com.github.makewheels.videoshare.videoservice.service;

import com.github.makewheels.videoshare.common.bean.TranscodeJob;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("transcode-service")
public interface TranscodeService {
    @PostMapping("transcode/getTranscodeJobsByVideoMongoId")
    List<TranscodeJob> getTranscodeJobsByVideoMongoId(@RequestParam String videoMongoId);
}
