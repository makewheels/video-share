package com.github.makewheels.videoshare.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "响应")
public class Result<T> {

    @ApiModelProperty(value = "状态码", required = true, example = "1", position = 1)
    private int code;

    @ApiModelProperty(value = "返回描述信息", required = true, example = "success", position = 2)
    private String message;

    @ApiModelProperty(value = "响应数据", position = 3)
    private T data;

    public Result() {
        this.code = ErrorCode.SUCCESS.getCode();
        this.message = ErrorCode.SUCCESS.getValue();
    }

    public Result(ErrorCode rc) {
        this.code = rc.getCode();
        this.message = rc.getValue();
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public Result(ErrorCode rc, T data) {
        this.code = rc.getCode();
        this.message = rc.getValue();
        this.data = data;
    }

    public Result(ErrorCode code, String message) {
        this.code = code.getCode();
        this.message = message;
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result<Void> ok() {
        return new Result<>(ErrorCode.SUCCESS);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(ErrorCode.SUCCESS, data);
    }

    public static <T> Result<T> error(ErrorCode code) {
        return new Result<T>(code);
    }

    public static <T> Result<T> error(ErrorCode code, T data) {
        return new Result<T>(code, data);
    }

}
