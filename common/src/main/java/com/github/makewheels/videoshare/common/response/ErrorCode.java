package com.github.makewheels.videoshare.common.response;

public enum ErrorCode {
    SUCCESS(0, "success"),
    FAIL(1, "fail"),

    WRONG_PARAM(2, "传入参数错误"),
    NOT_SUPPORT(3, "不支持此操作"),

    //用户相关
    NEED_LOGIN(1000, "请先登录"),
    CHECK_LOGIN_TOKEN_ERROR(1001, "检查loginToken错误"),
    REGISTER_LOGIN_NAME_ALREADY_EXISTS(1002, "登录名已存在"),
    LOGIN_LOGIN_NAME_PASSWORD_WRONG(1003, "登录名或密码错误"),
    SEARCH_USER_LOGIN_NAME_NOT_EXIST(1004, "搜索登录名不存在"),
    LOGIN_JPUSH_REGISTRATION_ID_IS_EMPTY(1005, "极光推送id为空，请稍后再试"),
    MODIFY_PASSWORD_OLD_PASSWORD_WRONG(1006, "老密码错误"),
    MODIFY_PHONE_PASSWORD_WRONG(1007, "密码错误"),
    MODIFY_PHONE_PHONE_SAME(1008, "新手机号与原手机号相同"),
    MODIFY_PHONE_VERIFICATION_CODE_EXPIRE(1010, "验证码过期"),
    MODIFY_PHONE_VERIFICATION_CODE_WRONG(1011, "验证码错误"),

    //视频相关
    UPLOAD_TOKEN_EXPIRED(2001, "上传令牌过期"),
    VIDEO_ID_NOT_EXIST(2002, "视频ID不存在"),
    PERMISSION_CHECK_FAIL(2003, "权限校验失败，需要登录上传者账号观看"),
    VIDEO_EXPIRED(2004, "视频已过期"),

    RUBBISH(1415926535, "我是垃圾，请忽略我");

    private final int code;
    private final String value;

    ErrorCode(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

}
