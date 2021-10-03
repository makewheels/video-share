package com.github.makewheels.videoshare.transcodeservice;

import com.github.makewheels.videoshare.common.bean.TranscodeJob;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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

    @PostMapping("getTranscodeJobsByVideoMongoId")
    public List<TranscodeJob> getTranscodeJobsByVideoMongoId(@RequestParam String videoMongoId) {
        return transcodeService.getTranscodeJobsByVideoMongoId(videoMongoId);
    }

}
