package com.github.makewheels.videoshare.videoservice.bean.getvideolist;

import lombok.Data;

import java.util.Date;

@Data
public class GetVideoListResponse {
    private String videoId;
    private String snowflakeId;
    private String title;

    private String visibility;

    private Boolean hasExpireTime;
    private Date expireTime;

    private Date createTime;
    private String status;
}
