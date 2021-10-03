package com.github.makewheels.videoshare.videoservice.bean;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Data
public class VideoInfoResponse {
    private String videoId;
    private String snowflakeId;
    private String title;
    private String description;

    private String visibility;

    private Boolean hasExpireTime;
    private Date expireTime;

    private Date createTime;
    private String status;
}
