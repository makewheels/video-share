package com.github.makewheels.videoshare.fileservice.bean;

import lombok.Data;

@Data
public class GetTemporaryCredentialRequest {
    private String uploadToken;
    private String originalFilename;
}
