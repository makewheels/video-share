package com.github.makewheels.videoshare.transcodeservice;

import com.aliyun.mts20140618.models.SubmitJobsResponse;
import com.aliyun.mts20140618.models.SubmitJobsResponseBody;
import com.github.makewheels.videoshare.common.bean.OssFile;
import com.github.makewheels.videoshare.common.bean.Resolution;
import com.github.makewheels.videoshare.transcodeservice.bean.TemplateConstants;
import com.github.makewheels.videoshare.transcodeservice.bean.TranscodeJob;
import com.github.makewheels.videoshare.transcodeservice.service.FileService;
import com.github.makewheels.videoshare.transcodeservice.service.VideoService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class TranscodeService {
    @Resource
    private VideoService videoService;
    @Resource
    private FileService fileService;
    @Resource
    private AliyunMpsService aliyunMpsService;
    @Resource
    private MongoTemplate mongoTemplate;

    private void submitJob(TranscodeJob transcodeJob) {
        String resolution = transcodeJob.getTargetResolution();
        String templateId = null;
        //找到分辨率对应模板
        if (resolution.equals(Resolution.R_1080P)) {
            templateId = TemplateConstants.TEMPLATE_ID_FLV_1080P;
        } else if (resolution.equals(Resolution.R_720P)) {
            templateId = TemplateConstants.TEMPLATE_ID_FLV_720P;
        } else if (resolution.equals(Resolution.R_480P)) {
            templateId = TemplateConstants.TEMPLATE_ID_FLV_480P;
        }
        //提交转码作业
        SubmitJobsResponse response = aliyunMpsService.submitTranscodeJob(transcodeJob.getFromObject(),
                transcodeJob.getToObject(), templateId);
        SubmitJobsResponseBody.SubmitJobsResponseBodyJobResultListJobResultJob job
                = response.getBody().getJobResultList().getJobResult().get(0).getJob();
        //设置状态和jobId
        transcodeJob.setStatus(job.getState());
        transcodeJob.setJobId(job.getJobId());
    }

    private String getToObject(String userMongoId, String videoMongoId, String resolution) {
        return "video/" + userMongoId + "/transcode/" + resolution + "/" + videoMongoId + ".flv";
    }

    public void transcodeVideo(String videoMongoId) {
        OssFile file = fileService.getOssFileByVideoMongoId(videoMongoId);
        String fromObject = file.getKey();
        String userMongoId = file.getUserMongoId();

        //提交三种转码任务
        TranscodeJob job_1080p = new TranscodeJob();
        job_1080p.setVideoMongoId(videoMongoId);
        job_1080p.setFromObject(fromObject);
        job_1080p.setStatus("create");

        job_1080p.setCreateTime(new Date());
        job_1080p.setTargetResolution(Resolution.R_1080P);
        job_1080p.setToObject(getToObject(userMongoId, videoMongoId, Resolution.R_1080P));

        TranscodeJob job_720p = new TranscodeJob();
        BeanUtils.copyProperties(job_1080p, job_720p);
        job_720p.setTargetResolution(Resolution.R_720P);
        job_720p.setToObject(getToObject(userMongoId, videoMongoId, Resolution.R_720P));

        TranscodeJob job_480p = new TranscodeJob();
        BeanUtils.copyProperties(job_1080p, job_480p);
        job_480p.setTargetResolution(Resolution.R_480P);
        job_480p.setToObject(getToObject(userMongoId, videoMongoId, Resolution.R_480P));

        submitJob(job_1080p);
        submitJob(job_720p);
        submitJob(job_480p);

        //保存转码任务到数据库
        mongoTemplate.save(job_1080p);
        mongoTemplate.save(job_720p);
        mongoTemplate.save(job_480p);

        //下面要开始最难的地方了，线程池轮询查询转码结果

    }

}
