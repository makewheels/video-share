package com.github.makewheels.videoshare.videoservice.bean;

import lombok.Data;

@Data
public class CreateVideoResponse {
    private String uploadToken;
    private String uploadPath;
}
