package com.github.makewheels.videoshare.transcodeservice;

import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.mts20140618.Client;
import com.aliyun.mts20140618.models.QueryJobListRequest;
import com.aliyun.mts20140618.models.QueryJobListResponse;
import com.aliyun.mts20140618.models.SubmitJobsRequest;
import com.aliyun.mts20140618.models.SubmitJobsResponse;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AliyunMpsService {
    private Client client;

    private Client getClient() {
        if (client == null) {
            Config config = new Config()
                    .setAccessKeyId("LTAI5tFnuAdF2V9ahMTxNK4B")
                    .setAccessKeySecret("NIFJ1ZBf468ytRFyxkZIvPF6qbtlyK");
            config.endpoint = "mts.cn-beijing.aliyuncs.com";
            try {
                client = new Client(config);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    public SubmitJobsResponse submitTranscodeJob(String fromObject, String toObject, String templateId) {
        SubmitJobsRequest submitJobsRequest = new SubmitJobsRequest()
                .setInput("{\"Bucket\":\"video-share-bucket\",\"Location\":\"oss-cn-beijing\",\"Object\"" +
                        ":\"" + URLUtil.encode(fromObject) + "\"}")
                .setOutputs("[{\"OutputObject\":\"" + URLUtil.encode(toObject) + "\",\"" +
                        "TemplateId\":\"" + templateId + "\"}]")
                .setOutputBucket("video-share-bucket")
                .setOutputLocation("oss-cn-beijing")
                .setPipelineId("6c126c07a9b34a85b7093e7bfa9c3ad9");
        log.info("阿里云转码任务: fromObject = " + fromObject);
        log.info("阿里云转码任务: toObject = " + toObject);
        log.info("阿里云转码任务: input = " + submitJobsRequest.getInput());
        log.info("阿里云转码任务: outputs = " + submitJobsRequest.getOutputs());
        log.info("阿里云转码任务: submitJobsRequest = " + JSON.toJSONString(submitJobsRequest));
        SubmitJobsResponse response = null;
        try {
            response = getClient().submitJobs(submitJobsRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("阿里云转码任务提交任务相应: SubmitJobsResponse = " + JSON.toJSONString(response));
        return response;
    }

    public QueryJobListResponse queryJob(String jobIds) {
        try {
            return getClient().queryJobList(new QueryJobListRequest().setJobIds(jobIds));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
