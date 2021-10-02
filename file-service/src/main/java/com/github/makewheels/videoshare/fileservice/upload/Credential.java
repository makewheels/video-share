package com.github.makewheels.videoshare.fileservice.upload;

import lombok.Data;

@Data
public class Credential {
    private String region;
    private String bucket;
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;

    private String fileSnowflakeId;
}
