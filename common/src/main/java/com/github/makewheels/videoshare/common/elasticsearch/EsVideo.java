package com.github.makewheels.videoshare.common.elasticsearch;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Data
@Document(indexName = "video")
public class EsVideo {
    @Id
    private String esId;

    private String mongoId;
    private Long snowflakeId;
    private String videoId;
    private String userMongoId;

    private String title;
    private String description;

    private String visibility;
    private Boolean hasExpireTime;
    private Date expireTime;
    private Date createTime;
    private Date uploadFinishTime;
    private String originalFileMongoId;
    private String uploadPath;
    private String status;
}
