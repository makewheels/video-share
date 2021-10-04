package com.github.makewheels.videoshare.transcodeservice;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.mts20140618.models.SubmitJobsResponse;
import com.aliyun.mts20140618.models.SubmitJobsResponseBody;
import com.github.makewheels.universaluserservice.common.bean.User;
import com.github.makewheels.videoshare.common.bean.file.OssFile;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeJob;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeTask;
import com.github.makewheels.videoshare.common.bean.video.Resolutions;
import com.github.makewheels.videoshare.common.bean.video.Video;
import com.github.makewheels.videoshare.transcodeservice.service.FileService;
import com.github.makewheels.videoshare.transcodeservice.service.UserService;
import com.github.makewheels.videoshare.transcodeservice.service.VideoService;
import com.github.makewheels.videoshare.transcodeservice.util.TemplateIds;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TranscodeService {
    @Resource
    private UserService userService;
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
        if (resolution.equals(Resolutions.R_1080P)) {
            templateId = TemplateIds.TEMPLATE_ID_FLV_1080P;
        } else if (resolution.equals(Resolutions.R_720P)) {
            templateId = TemplateIds.TEMPLATE_ID_FLV_720P;
        } else if (resolution.equals(Resolutions.R_480P)) {
            templateId = TemplateIds.TEMPLATE_ID_FLV_480P;
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

    private String getToObject(String userId, String videoId, String resolution) {
        return "video/" + userId + "/" + videoId + "/transcode/" + resolution + "/" + videoId + ".flv";
    }

    public TranscodeTask transcodeVideo(String videoMongoId) {
        log.info("新建转码任务: videoMongoId = " + videoMongoId);
        OssFile file = fileService.getOssFileByVideoMongoId(videoMongoId);
        String fromObject = file.getKey();
        String userMongoId = file.getUserMongoId();

        User user = userService.getUserByMongoId(userMongoId);
        long userSnowflakeId = user.getSnowflakeId();

        Video video = videoService.getVideoByMongoId(videoMongoId);
        long videoSnowflakeId = video.getSnowflakeId();

        //提交三种转码任务
        TranscodeJob job_1080p = new TranscodeJob();
        job_1080p.setVideoMongoId(videoMongoId);
        job_1080p.setFromObject(fromObject);
        job_1080p.setStatus("create");

        job_1080p.setCreateTime(new Date());
        job_1080p.setTargetResolution(Resolutions.R_1080P);
        job_1080p.setToObject(getToObject(userSnowflakeId + "", videoSnowflakeId + "",
                Resolutions.R_1080P));

        TranscodeJob job_720p = new TranscodeJob();
        BeanUtils.copyProperties(job_1080p, job_720p);
        job_720p.setTargetResolution(Resolutions.R_720P);
        job_720p.setToObject(getToObject(userSnowflakeId + "", videoSnowflakeId + "",
                Resolutions.R_720P));

        TranscodeJob job_480p = new TranscodeJob();
        BeanUtils.copyProperties(job_1080p, job_480p);
        job_480p.setTargetResolution(Resolutions.R_480P);
        job_480p.setToObject(getToObject(userSnowflakeId + "", videoSnowflakeId + "",
                Resolutions.R_480P));

        submitJob(job_1080p);
        submitJob(job_720p);
        submitJob(job_480p);

        TranscodeTask transcodeTask = new TranscodeTask();
        String taskId = IdUtil.simpleUUID();
        transcodeTask.setTaskId(taskId);
        transcodeTask.setCreateTime(new Date());
        transcodeTask.setUserMongoId(userMongoId);
        transcodeTask.setVideoMongoId(videoMongoId);
        mongoTemplate.save(transcodeTask);

        job_1080p.setTaskId(taskId);
        job_720p.setTaskId(taskId);
        job_480p.setTaskId(taskId);

        //保存转码任务到数据库
        mongoTemplate.save(job_1080p);
        mongoTemplate.save(job_720p);
        mongoTemplate.save(job_480p);

        //下面要开始最难的地方了，线程池轮询查询转码结果
        addQueryTask(transcodeTask, Lists.newArrayList(job_1080p, job_720p, job_480p));
        return transcodeTask;
    }

    /**
     * 添加阿里云转码查询任务
     *
     * @param transcodeTask
     * @param transcodeJobs
     */
    private void addQueryTask(TranscodeTask transcodeTask, ArrayList<TranscodeJob> transcodeJobs) {
        log.info("添加任务: " + JSON.toJSONString(transcodeTask)
                + " " + JSON.toJSONString(transcodeJobs));
    }

    /**
     * 根据videoMongoId获取转码jobs
     */
    public List<TranscodeJob> getTranscodeJobsByVideoMongoId(String videoMongoId) {
        Query query = Query.query(Criteria.where("videoMongoId").is(videoMongoId));
        return mongoTemplate.find(query, TranscodeJob.class);
    }

}
