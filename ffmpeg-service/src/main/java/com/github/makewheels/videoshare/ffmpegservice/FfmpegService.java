package com.github.makewheels.videoshare.ffmpegservice;

import com.github.makewheels.videoshare.common.bean.transcode.TranscodeJob;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FfmpegService {
    private Map<String, TranscodeJob> map = new HashMap<>();

    public void transcode(TranscodeJob transcodeJob) {
        map.put(transcodeJob.getTaskId(), transcodeJob);

    }
}
