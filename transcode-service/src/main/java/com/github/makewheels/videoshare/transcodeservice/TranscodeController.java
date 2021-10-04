package com.github.makewheels.videoshare.transcodeservice;

import com.github.makewheels.videoshare.common.bean.transcode.TranscodeJob;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeTask;
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

    @PostMapping("transcodeVideo")
    public TranscodeTask transcodeVideo(@RequestParam String videoMongoId) {
        return transcodeService.transcodeVideo(videoMongoId);
    }

    @PostMapping("getTranscodeJobsByVideoMongoId")
    public List<TranscodeJob> getTranscodeJobsByVideoMongoId(@RequestParam String videoMongoId) {
        return transcodeService.getTranscodeJobsByVideoMongoId(videoMongoId);
    }

}
