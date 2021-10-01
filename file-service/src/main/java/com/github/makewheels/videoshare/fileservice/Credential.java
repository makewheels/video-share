package com.github.makewheels.videoshare.fileservice;

import lombok.Data;

@Data
public class Credential {
    private String region;
    private String bucket;
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
}
