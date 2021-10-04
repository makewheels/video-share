package com.github.makewheels.videoshare.videoservice.service;

import com.github.makewheels.videoshare.common.bean.transcode.TranscodeJob;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeTask;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("transcode-service")
public interface TranscodeService {
    @PostMapping("transcode/transcodeVideo")
    TranscodeTask transcodeVideo(@RequestParam String videoMongoId);

    @PostMapping("transcode/getTranscodeJobsByVideoMongoId")
    List<TranscodeJob> getTranscodeJobsByVideoMongoId(@RequestParam String videoMongoId);
}
