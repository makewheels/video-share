package com.github.makewheels.videoshare.fileservice;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class OssFile {
    @Id
    private String mongoId;
    private Long snowflakeId;

    private String provider;
    private String key;
    private String accessUrl;
    private Long size;
    private String md5;
    private Date createTime;
}
