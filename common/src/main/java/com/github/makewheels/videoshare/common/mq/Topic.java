package com.github.makewheels.videoshare.common.mq;

import com.github.makewheels.videoshare.common.bean.video.VideoStatus;

public class Topic {
    public static final String TOPIC_ORIGINAL_FILE_READY = VideoStatus.STATUS_ORIGINAL_FILE_READY;
    public static final String TOPIC_TRANSCODING = VideoStatus.STATUS_TRANSCODING;
    public static final String TOPIC_TRANSCODE_FINISH = "transcode-finish";
}
