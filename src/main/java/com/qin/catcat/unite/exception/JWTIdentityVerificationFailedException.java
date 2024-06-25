package com.qin.catcat.unite.exception;

//自定义JWT身份校验失败异常类
public class JWTIdentityVerificationFailedException extends RuntimeException{
    
    private String message;

    public JWTIdentityVerificationFailedException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}