package com.qin.catcat.unite.controller.handle;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.enumclass.CatcatEnumClass;
import com.qin.catcat.unite.common.enumclass.CatcatEnumClass.StatusCode;
import com.qin.catcat.unite.exception.JWTIdentityVerificationFailedException;
import com.qin.catcat.unite.exception.PasswordIncorrectException;
import com.qin.catcat.unite.exception.UserNotExistException;
import com.qin.catcat.unite.exception.updatePasswordFailedException;

import lombok.extern.slf4j.Slf4j;

import com.qin.catcat.unite.exception.UserAlreadyExistsException;
import com.qin.catcat.unite.exception.BusinessException;

import java.sql.SQLException;

/**
 * @Description 全局异常处理
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-04 01:17
 */
@RestControllerAdvice // 只能处理Controller的异常
@Slf4j
public class GlobalExceptionHandler {

    /**
     * @Description 处理业务异常
     * 返回HTTP 200状态码，但在响应体中包含具体的业务状态码
     * @Author liuyun
     * @Version 1.0
     * @Since 2025-01-04 01:17
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<String>> handleBusinessException(BusinessException ex) {
        log.error("业务异常处理: {}", ex.getMessage());
        Result<String> result = Result.error(ex.getStatusCode(), ex.getMessage());
        // 返回HTTP 200状态码，但在响应体中包含业务错误信息
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * @Description 处理SQL异常
     * @Author liuyun
     * @Version 1.0
     * @Since 2025-01-04 01:17
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Result<String>> handleSQLException(SQLException ex) {
        log.error("数据库操作异常: {}", ex.getMessage());
        Result<String> result = Result.error(StatusCode.INTERNAL_SERVER_ERROR.getCode(), "数据库操作失败：" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * @Description 处理其他运行时异常
     * @Author liuyun
     * @Version 1.0
     * @Since 2025-01-04 01:17
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<String>> handleRuntimeException(RuntimeException ex) {
        log.error("运行时异常: {}", ex.getMessage());
        Result<String> result = Result.error(StatusCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部错误：" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * @Description 处理所有未处理的异常
     * @Author liuyun
     * @Version 1.0
     * @Since 2025-01-04 01:17
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<String>> handleException(Exception ex) {
        log.error("未知异常: {}", ex.getMessage());
        Result<String> result = Result.error(StatusCode.INTERNAL_SERVER_ERROR.getCode(), "系统发生未知错误");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}