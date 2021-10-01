package com.github.makewheels.videoshare.fileservice;

import lombok.Data;

@Data
public class Credential {
    private String region;
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
    private String bucket;
}
