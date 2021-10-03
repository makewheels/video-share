package com.github.makewheels.videoshare.common.bean.file;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class OssFile {
    @Id
    private String mongoId;
    @Indexed
    private Long snowflakeId;
    @Indexed
    private String status;
    @Indexed
    private String userMongoId;
    @Indexed
    private String videoMongoId;

    private String originalFilename;

    private String provider;

    private String region;
    private String bucket;

    private String key;
    private String accessUrl;
    private String baseUrl;

    private Long size;
    private String md5;
    @Indexed
    private Date createTime;
    @Indexed
    private Date uploadFinishTime;
    @Indexed
    private String uploadToken;
}
