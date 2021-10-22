package com.github.makewheels.videoshare.ffmpegservice;

import com.alibaba.fastjson.JSON;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeJob;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class FfmpegService {
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

    private static final Map<String, TranscodeJob> map = new HashMap<>();

    public FFmpegProbeResult getProbeInfo(File file) {
        try {
            return ffprobe.probe(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void transcode(TranscodeJob transcodeJob, File inputFile, File outputFile) {
        map.put(transcodeJob.getTaskId(), transcodeJob);

        //转码
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(inputFile.getAbsolutePath())
                .addOutput(outputFile.getAbsolutePath())
                .done();

        //获取视频信息
        FFmpegProbeResult probeResult = getProbeInfo(inputFile);

        FFmpegJob fFmpegJob = fFmpegExecutor.createJob(builder, new ProgressListener() {
            // Using the FFmpegProbeResult determine the duration of the input
            final double duration_ns = probeResult.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

            @Override
            public void progress(Progress progress) {
                //更新转码进度
                double percentage = progress.out_time_ns / duration_ns;

                transcodeJob.setPercentage(percentage * 100);
                transcodeJob.setProgress(progress);

                System.out.println(JSON.toJSONString(progress));
//                log.info(
//                        "[{}] status:{} frame:{} time:{} ms fps:{}f speed:{}n",
//                        percentage * 100,
//                        progress.status,
//                        progress.frame,
//                        FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
//                        progress.fps.doubleValue(),
//                        progress.speed
//                );
            }
        });

        fFmpegJob.run();
    }

}
