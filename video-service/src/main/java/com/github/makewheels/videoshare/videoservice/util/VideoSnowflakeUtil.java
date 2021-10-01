package com.github.makewheels.videoshare.videoservice.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

public class VideoSnowflakeUtil {
    private static final Snowflake snowflake = IdUtil.getSnowflake(1L);

    public static Long get() {
        return snowflake.nextId();
    }
}
