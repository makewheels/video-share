package com.github.makewheels.videoshare.transcodeservice;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.mts20140618.models.QueryJobListResponse;
import com.aliyun.mts20140618.models.QueryJobListResponseBody;
import com.aliyun.mts20140618.models.SubmitJobsResponse;
import com.aliyun.mts20140618.models.SubmitJobsResponseBody;
import com.github.makewheels.universaluserservice.common.bean.User;
import com.github.makewheels.videoshare.common.bean.file.OssFile;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeJob;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeStatus;
import com.github.makewheels.videoshare.common.bean.transcode.TranscodeTask;
import com.github.makewheels.videoshare.common.bean.video.Resolutions;
import com.github.makewheels.videoshare.common.bean.video.Video;
import com.github.makewheels.videoshare.common.mq.Topic;
import com.github.makewheels.videoshare.transcodeservice.mq.RocketMQService;
import com.github.makewheels.videoshare.transcodeservice.repository.TranscodeJobRepository;
import com.github.makewheels.videoshare.transcodeservice.repository.TranscodeTaskRepository;
import com.github.makewheels.videoshare.transcodeservice.service.FileService;
import com.github.makewheels.videoshare.transcodeservice.service.UserService;
import com.github.makewheels.videoshare.transcodeservice.service.VideoService;
import com.github.makewheels.videoshare.transcodeservice.util.TemplateIds;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
    @Resource
    private TranscodeTaskRepository transcodeTaskRepository;
    @Resource
    private TranscodeJobRepository transcodeJobRepository;
    @Resource
    private RocketMQService rocketMQService;

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
        transcodeJob.setAliyunJobId(job.getJobId());
    }

    private String getToObject(String userId, String videoId, String resolution) {
        return "video/" + userId + "/" + videoId + "/transcode/" + resolution + "/" + videoId;
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
        job_1080p.setUserMongoId(userMongoId);
        job_1080p.setFromObject(fromObject);
        job_1080p.setStatus(TranscodeStatus.CREATE);

        job_1080p.setCreateTime(new Date());
        job_1080p.setTargetResolution(Resolutions.R_1080P);
        job_1080p.setToObject(getToObject(userSnowflakeId + "", videoSnowflakeId + "",
                Resolutions.R_1080P));
        job_1080p.setTargetWidth(1920);
        job_1080p.setTargetHeight(1080);

        TranscodeJob job_720p = new TranscodeJob();
        BeanUtils.copyProperties(job_1080p, job_720p);
        job_720p.setTargetResolution(Resolutions.R_720P);
        job_720p.setToObject(getToObject(userSnowflakeId + "", videoSnowflakeId + "",
                Resolutions.R_720P));
        job_720p.setTargetWidth(1280);
        job_720p.setTargetHeight(720);

        TranscodeJob job_480p = new TranscodeJob();
        BeanUtils.copyProperties(job_1080p, job_480p);
        job_480p.setTargetResolution(Resolutions.R_480P);
        job_480p.setToObject(getToObject(userSnowflakeId + "", videoSnowflakeId + "",
                Resolutions.R_480P));
        job_480p.setTargetHeight(844);
        job_480p.setTargetHeight(480);

        submitJob(job_1080p);
        submitJob(job_720p);
        submitJob(job_480p);

        TranscodeTask transcodeTask = new TranscodeTask();
        String taskId = IdUtil.simpleUUID();
        transcodeTask.setTaskId(taskId);
        transcodeTask.setCreateTime(new Date());
        transcodeTask.setUserMongoId(userMongoId);
        transcodeTask.setVideoMongoId(videoMongoId);
        transcodeTask.setStatus(TranscodeStatus.CREATE);
        mongoTemplate.save(transcodeTask);

        job_1080p.setTaskId(taskId);
        job_720p.setTaskId(taskId);
        job_480p.setTaskId(taskId);

        //保存转码任务到数据库
        mongoTemplate.save(job_1080p);
        mongoTemplate.save(job_720p);
        mongoTemplate.save(job_480p);

        //下面要开始最难的地方了，线程池轮询查询转码结果
        addQueryTask(user, video, transcodeTask, Lists.newArrayList(job_1080p, job_720p, job_480p));
        return transcodeTask;
    }

    private boolean isFinishedStatus(String jobState) {
        return StringUtils.equalsAny(jobState, TranscodeStatus.TRANSCODE_SUCCESS,
                TranscodeStatus.TRANSCODE_FAIL, TranscodeStatus.TRANSCODE_CANCELLED);
    }

    /**
     * 添加阿里云转码查询任务
     */
    private void addQueryTask(User user, Video video, TranscodeTask transcodeTask,
                              List<TranscodeJob> transcodeJobs) {
        log.info("添加阿里云转码查询任务: transcodeTask = " + JSON.toJSONString(transcodeTask)
                + " , transcodeJobs = " + JSON.toJSONString(transcodeJobs));
        Map<String, TranscodeJob> map = transcodeJobs.stream().collect(
                Collectors.toMap(TranscodeJob::getAliyunJobId, transcodeJob -> transcodeJob));
        List<String> jobIds = transcodeJobs.stream().map(TranscodeJob::getAliyunJobId).collect(Collectors.toList());
        long startTime = System.currentTimeMillis();
        do {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //如果4小时都没转完，就跳出
            if ((System.currentTimeMillis() - startTime) > 4 * 60 * 60 * 1000L) {
                log.error("4小时都没转完，来个人看看这是个啥 video = {}, transcodeTask = {}, transcodeJobs = {}",
                        JSON.toJSONString(video), JSON.toJSONString(transcodeTask), JSON.toJSONString(transcodeJobs));
                break;
            }
            QueryJobListResponse response = aliyunMpsService.queryJob(StringUtils.join(jobIds, ","));
            List<QueryJobListResponseBody.QueryJobListResponseBodyJobListJob> jobs
                    = response.getBody().getJobList().getJob();
            for (QueryJobListResponseBody.QueryJobListResponseBodyJobListJob job : jobs) {
                String jobId = job.getJobId();
                String jobState = job.getState();
                log.info("查询结果: videoMongoId = {}, taskId = {}, jobId = {}, jobState = {}, jobJSON = {}",
                        video.getMongoId(), transcodeTask.getTaskId(), jobId, jobState, JSON.toJSONString(job));
                TranscodeJob transcodeJob = map.get(job.getJobId());
                //如果转码成功或失败或取消，也就是说完成了，之后状态也不会再变了，那就移除任务
                if (isFinishedStatus(jobState)) {
                    log.info("任务已完成：jobId = {}, transcodeJob = {}", jobId,
                            JSON.toJSONString(transcodeJob));
                    jobIds.remove(jobId);
                }
                //如果状态不同
                if (!StringUtils.equals(jobState, transcodeJob.getStatus())) {
                    onJobStatusChanged(user, video, transcodeTask, transcodeJobs, transcodeJob, job);
                }
            }
        } while (!jobIds.isEmpty());
    }

    /**
     * 当转码任务完成时
     * <p>
     * 作业状态：
     * Submitted            作业已提交。
     * Transcoding          转码中。
     * TranscodeSuccess     转码成功。
     * TranscodeFail        转码失败。
     * TranscodeCancelled   转码取消。
     */
    private void onJobStatusChanged(
            User user, Video video, TranscodeTask transcodeTask, List<TranscodeJob> transcodeJobs,
            TranscodeJob transcodeJob, QueryJobListResponseBody.QueryJobListResponseBodyJobListJob job) {
        String jobState = job.getState();
        String transcodeJobMongoId = transcodeJob.getMongoId();
        //如果阿里云最新的状态和我现在的状态不一致，更新 transcode job
        if (!StringUtils.equals(transcodeJob.getStatus(), jobState)) {
            transcodeJob.setStatus(jobState);
            transcodeJobRepository.updateByMongoId(transcodeJobMongoId, "status", jobState);
            //如果是已完成状态，把任务详情更新到数据库
            if (isFinishedStatus(jobState)) {
                transcodeJobRepository.updateByMongoId(transcodeJobMongoId, "jobDetail",
                        JSONObject.parse(JSON.toJSONString(job)));
            }
            //发消息队列：job完成
            rocketMQService.send(Topic.TOPIC_TRANSCODE_JOB_STATUS_CHANGED, transcodeJob);
        }

        //TODO 面试点来了，如何保证数据一致性？如何保证消费幂等性？
        //判断task是否都完成，如果都完成，修改 transcode task状态
        boolean jobAllFinished = false;
        for (TranscodeJob each : transcodeJobs) {
            jobAllFinished = isFinishedStatus(each.getStatus());
        }
        //如果task都完成
        //更新数据库
        transcodeTaskRepository.updateByMongoId(transcodeTask.getMongoId(), "status",
                TranscodeStatus.FINISH);
        if (jobAllFinished) {
            //发消息队列：task完成
            rocketMQService.send(Topic.TOPIC_TRANSCODE_TASK_FINISHED, transcodeTask);
        }
    }

    /**
     * 根据videoMongoId获取转码jobs
     */
    public List<TranscodeJob> getTranscodeJobsByVideoMongoId(String videoMongoId) {
        Query query = Query.query(Criteria.where("videoMongoId").is(videoMongoId));
        return mongoTemplate.find(query, TranscodeJob.class);
    }
}
