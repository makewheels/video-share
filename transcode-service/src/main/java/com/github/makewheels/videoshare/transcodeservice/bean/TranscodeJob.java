package com.github.makewheels.videoshare.transcodeservice.bean;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class TranscodeJob {
    @Id
    private String mongoId;

    private String videoMongoId;
    private Date createTime;
    private Date finishTime;

    private String originalFileMongoId;

    private String fromObject;
    private String toObject;

    private String targetResolution;

    private String jobId;
    private JSONObject aliyunJobResponse;
    private String status;
}
