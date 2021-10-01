package com.github.makewheels.videoshare.common.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class Video {
    @Id
    private String mongoId;
    private Long snowflakeId;
    private String videoId;

    private String userMongoId;

    private String title;
    private String description;
    private String visibility;

    private Date createTime;
    private Date uploadFinishTime;

    private String originalFileMongoId;
    private String uploadPath;

    private String status;
}
