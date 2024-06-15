package com.qin.catcat.unite.common.enumclass;

public enum enumStatusCode {

    SUCSSESS("2000","成功"),
    PASSWORD_INCORRECT("1001", "密码错误"),
    USERNAME_NOT_FOUND("1002", "用户名不存在"),
    INTERNAL_SERVER_ERROR("5001", "服务器内部错误"),
    UNKNOWNERROR("6000","未知错误");

    private final String code;
    private final String message;

    enumStatusCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
