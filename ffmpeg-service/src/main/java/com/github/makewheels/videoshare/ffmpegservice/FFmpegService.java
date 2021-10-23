package com.github.makewheels.videoshare.ffmpegservice;

import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class FFmpegService {
    private static FFmpeg ffmpeg;
    private static FFprobe ffprobe;
    private static FFmpegExecutor fFmpegExecutor;

    static {
        //初始化
        try {
            ffmpeg = new FFmpeg();
            ffprobe = new FFprobe();
            fFmpegExecutor = new FFmpegExecutor(ffmpeg, ffprobe);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FFmpegProbeResult getProbeInfo(File file) {
        try {
            return ffprobe.probe(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FFmpegExecutor getFFmpegExecutor() {
        return fFmpegExecutor;
    }

}
