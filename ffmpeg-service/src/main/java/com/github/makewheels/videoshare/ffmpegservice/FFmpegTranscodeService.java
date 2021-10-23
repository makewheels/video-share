package com.github.makewheels.videoshare.ffmpegservice;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpUtil;
import com.github.makewheels.videoshare.common.bean.file.OssFile;
import com.github.makewheels.videoshare.common.bean.file.OssSignRequest;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeJob;
import com.github.makewheels.videoshare.common.time.TimeInMillis;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FFmpegTranscodeService {
    @Resource
    private FFmpegService ffmpegService;
    @Resource
    private FileService fileService;

    private static final Map<String, TranscodeJob> map = new HashMap<>();

    public void transcode(TranscodeJob transcodeJob, File inputFile, File outputFile) {
        map.put(transcodeJob.getFfmpegJobId(), transcodeJob);
        //创建目录
        File folder = new File(SystemUtils.getUserHome(), "/transcode/" +
                transcodeJob.getVideoMongoId() + "/" + transcodeJob.getFfmpegJobId() + "/");
        if (!folder.exists()) folder.mkdirs();

        //输入输出文件路径
        OssFile originalOssFile = fileService.getOssFileByMongoId(transcodeJob.getOriginalFileMongoId());
        File input = new File(folder, "original/" + originalOssFile.getMongoId()
                + originalOssFile.getExtension());
        File output = new File(folder, "transcode/" + transcodeJob.getTargetResolution() + "/"
                + transcodeJob.getOriginalFileMongoId() + ".m3u8");

        String fromObject = transcodeJob.getFromObject();

        //获取源文件内网下载地址
        List<OssSignRequest> ossSignRequests = new ArrayList<>(1);
        OssSignRequest ossSignRequest = new OssSignRequest();
        ossSignRequest.setKey(transcodeJob.getFromObject());
        ossSignRequest.setTime(System.currentTimeMillis() + TimeInMillis.T_2_HOURS);
        ossSignRequests.add(ossSignRequest);
        Map<String, String> signedUrlMap = fileService.getgetInternalSignedUrl(ossSignRequests);
        String originalFileUrl = signedUrlMap.get(fromObject);
        //下载源文件
        HttpUtil.downloadFile(originalFileUrl, inputFile);

        //开始转码
        ffmpegService.transcodeToM3u8(transcodeJob, inputFile, outputFile);
    }

    public TranscodeJob queryTranscodeJobByFFmpegJobId(String ffmpegJobId) {
        return map.get(ffmpegJobId);
    }
}
