package com.github.makewheels.videoshare.common.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class Video {
    @Id
    private String mongoId;
    @Indexed
    private Long snowflakeId;
    @Indexed
    private String videoId;
    @Indexed
    private String userMongoId;

    private String title;
    private String description;

    @Indexed
    private String visibility;
    private Date expireTime;

    @Indexed
    private Date createTime;
    @Indexed
    private Date uploadFinishTime;
    @Indexed
    private String originalFileMongoId;
    private String uploadPath;
    @Indexed
    private String status;
}
