package com.qin.catcat.unite.exception;

//自定义用户不存在异常类
public class UserNotExistException extends RuntimeException{
    private String message;

    public UserNotExistException(String message){
        super(message);
        this.message=message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
