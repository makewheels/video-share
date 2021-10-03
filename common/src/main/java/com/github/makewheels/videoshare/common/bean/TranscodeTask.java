package com.github.makewheels.videoshare.common.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document
public class TranscodeTask {
    @Id
    private String mongoId;

    @Indexed
    private String taskId;

    private String videoMongoId;
    private String userMongoId;

    private Date createTime;

}
