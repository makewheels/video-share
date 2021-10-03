package com.github.makewheels.videoshare.videoservice.bean.createvideo;

import lombok.Data;

@Data
public class CreateVideoRequest {
    private String title;
    private String description;
    private String visibility;
    private Long expireTimeLength;
    private String originalFilename;
}
