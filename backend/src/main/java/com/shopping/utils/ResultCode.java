package com.shopping.utils;

public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或token已过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "资源不存在");

    private Integer code;
    private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
