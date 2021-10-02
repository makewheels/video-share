package com.github.makewheels.videoshare.fileservice.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

public class FileSnowflakeUtil {
    private static final Snowflake snowflake = IdUtil.getSnowflake(2L);

    public static long get() {
        return snowflake.nextId();
    }
}
