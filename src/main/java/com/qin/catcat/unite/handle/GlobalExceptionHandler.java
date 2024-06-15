package com.qin.catcat.unite.handle;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.qin.catcat.unite.common.enumclass.enumStatusCode;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.exception.PasswordIncorrectException;
import com.qin.catcat.unite.exception.UserNotExistException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理自定义的密码错误异常
    @ExceptionHandler(PasswordIncorrectException.class)
    public Result<String> handlePasswordIncorrectException(PasswordIncorrectException ex) {
        return Result.error(ex.getMessage(),enumStatusCode.PASSWORD_INCORRECT);
    }

    // 处理自定义的密码错误异常
    @ExceptionHandler(UserNotExistException.class)
    public Result<String> handleUserNotExistException(UserNotExistException ex) {
        return Result.error(ex.getMessage(),enumStatusCode.USERNAME_NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        return new ResponseEntity<>("服务器内部错误: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}