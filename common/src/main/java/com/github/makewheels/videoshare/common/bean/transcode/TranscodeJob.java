package com.github.makewheels.videoshare.common.bean.transcode;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import net.bramp.ffmpeg.progress.Progress;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class TranscodeJob {
    @Id
    private String mongoId;

    private String taskId;

    private String videoMongoId;
    private String userMongoId;

    private Date createTime;
    private Date finishTime;

    private String originalFileMongoId;

    private String fromObject;
    private String toObject;

    private String targetResolution;

    private String ffmpegJobId;
    private String aliyunJobId;
    private JSONObject aliyunJobDetail;

    private String status;

    private Progress progress;
    private Double percentage;
}
