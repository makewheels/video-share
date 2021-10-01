package com.github.makewheels.videoshare.fileservice;

public class RedisKey {
    private static final String ROOT = "video-file-service";

    public static String uploadToken(String uploadToken) {
        return ROOT + ":uploadToken:" + uploadToken;
    }
}
