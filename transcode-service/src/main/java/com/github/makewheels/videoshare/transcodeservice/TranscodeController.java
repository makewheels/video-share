package com.github.makewheels.videoshare.transcodeservice;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("transcode")
public class TranscodeController {
    @Resource
    private TranscodeService transcodeService;

    @PostMapping("addTranscodeTask")
    public String addTranscodeTask(@RequestParam String videoMongoId) {
        transcodeService.transcodeVideo(videoMongoId);
        return "1111111112";
    }
}
