package com.qin.catcat.unite.controller.handle;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.qin.catcat.unite.common.enumclass.enumStatusCode;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.exception.JWTIdentityVerificationFailedException;
import com.qin.catcat.unite.exception.PasswordIncorrectException;
import com.qin.catcat.unite.exception.UserNotExistException;
import com.qin.catcat.unite.exception.updatePasswordFailedException;
import com.qin.catcat.unite.exception.UserAlreadyExistsException;
import com.qin.catcat.unite.exception.BusinessException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException ex) {
        return Result.error(ex.getMessage(),enumStatusCode.BUSINESS_ERROR);
    }

    // 处理自定义的密码错误异常
    @ExceptionHandler(PasswordIncorrectException.class)
    public Result<String> handlePasswordIncorrectException(PasswordIncorrectException ex) {
        return Result.error(ex.getMessage(),enumStatusCode.PASSWORD_INCORRECT);
    }

    // 处理自定义的用户不存在异常
    @ExceptionHandler(UserNotExistException.class)
    public Result<String> handleUserNotExistException(UserNotExistException ex) {
        return Result.error(ex.getMessage(),enumStatusCode.USERNAME_NOT_FOUND);
    }

    // 处理自定义JWT身份校验失败异常
    @ExceptionHandler(JWTIdentityVerificationFailedException.class)
    public Result<String> handleJWTIdentityVerificationFailedException(JWTIdentityVerificationFailedException ex) {
        return Result.error(ex.getMessage(),enumStatusCode.JWT_IDENTITY_VERICION_FAILED);
    }

    // 处理自定义 更新密码失败异常
    @ExceptionHandler(updatePasswordFailedException.class)
    public Result<String> handleJWTIdentityVerificationFailedException(updatePasswordFailedException ex) {
        return Result.error(ex.getMessage(),enumStatusCode.PASSWORD_UPDATE_FAILURE);
    }

    // 处理自定义 用户已存在异常
    @ExceptionHandler(UserAlreadyExistsException.class)
    public Result<String> UserAlreadyExistsException(UserAlreadyExistsException ex) {
        return Result.error(ex.getMessage(),enumStatusCode.USER_ALREADY_EXISTS);
    }
    
    // @ExceptionHandler(UnloginUserRequestHomePostException.class){
    // public ResponseEntity<String> UnloginUserRequestHomePostException(UnloginUserRequestHomePostException ex) {
    //     return new ResponseEntity<>("请先登录", HttpStatus.UNAUTHORIZED);
    // }
    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<String> handleAllExceptions(Exception ex) {
    //     return new ResponseEntity<>("服务器内部错误: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    // }
}