package com.qin.catcat.unite.exception;

//自定义密码错误异常类
public class PasswordIncorrectException extends RuntimeException {
    private String message;

    public PasswordIncorrectException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}