package com.github.makewheels.videoshare.ffmpegservice;

import com.alibaba.fastjson.JSON;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeJob;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class FFmpegTranscodeService {
    @Resource
    private FFmpegService ffmpegService;
    private static final Map<String, TranscodeJob> map = new HashMap<>();

    public void transcode(TranscodeJob transcodeJob, File inputFile, File outputFile) {
        map.put(transcodeJob.getFfmpegJobId(), transcodeJob);

        //转码
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(inputFile.getAbsolutePath())
                .addOutput(outputFile.getAbsolutePath())
                .done();

        //获取视频信息
        FFmpegProbeResult probeResult = ffmpegService.getProbeInfo(inputFile);

        FFmpegJob ffmpegJob = ffmpegService.getFFmpegExecutor().createJob(builder, new ProgressListener() {
            // Using the FFmpegProbeResult determine the duration of the input
            final double duration_ns = probeResult.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

            @Override
            public void progress(Progress progress) {
                //更新转码进度
                double percentage = progress.out_time_ns / duration_ns;

                transcodeJob.setPercentage(percentage * 100);
                transcodeJob.setProgress(progress);

                log.info(JSON.toJSONString(progress));
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

        ffmpegJob.run();
    }

    public TranscodeJob queryTranscodeJobByFFmpegJobId(String ffmpegJobId) {
        return map.get(ffmpegJobId);
    }
}
