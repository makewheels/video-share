package com.github.makewheels.videoshare.videoservice.bean;

import lombok.Data;

@Data
public class CreateVideoRequest {
    private String title;
    private String description;
    private String visibility;
    private String originalFilename;
}
