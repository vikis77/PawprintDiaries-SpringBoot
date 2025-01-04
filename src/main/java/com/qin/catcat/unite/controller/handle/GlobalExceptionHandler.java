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
import com.qin.catcat.unite.exception.UserAlreadyExistsException;
import com.qin.catcat.unite.exception.BusinessException;

/**
 * @Description 全局异常处理
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-04 01:17
 */
@RestControllerAdvice
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
        Result<String> result = Result.error(ex.getMessage(), ex.getStatusCode());
        // 返回HTTP 200状态码，但在响应体中包含业务错误信息
        // return ResponseEntity.ok(result);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}