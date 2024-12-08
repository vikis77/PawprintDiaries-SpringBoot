package com.qin.catcat.unite.exception;

// 自定义业务异常类
public class BusinessException extends RuntimeException{

    private String message;

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
