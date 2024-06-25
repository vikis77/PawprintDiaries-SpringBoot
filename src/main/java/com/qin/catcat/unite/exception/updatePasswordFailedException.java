package com.qin.catcat.unite.exception;

//自定义 更新密码失败异常类
public class updatePasswordFailedException extends RuntimeException{
    private String message;

    public updatePasswordFailedException(String message){
        super(message);
        this.message=message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
