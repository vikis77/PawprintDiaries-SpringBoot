package com.qin.catcat.unite.common.result;

import lombok.Data;

import java.io.Serializable;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import com.qin.catcat.unite.common.enumclass.enumStatusCode;

/**
 * 后端统一返回结果
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {

    private String code; //编码：1成功，0和其它数字为失败
    private String msg; //信息
    private T data; //数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = enumStatusCode.SUCSSESS.getCode();
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = enumStatusCode.SUCSSESS.getCode();
        return result;
    }

    public static <T> Result<T> success(String msg) {
        Result<T> result = new Result<T>();
        result.msg = msg;
        result.code = enumStatusCode.SUCSSESS.getCode();
        return result;
    }

    public static <T> Result<T> success(T object,String msg) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.msg = msg;
        result.code = enumStatusCode.SUCSSESS.getCode();
        return result;
    }

    public static <T> Result<T> error(String msg,enumStatusCode enumStatusCode) {
        Result<T> result = new Result<T>();
        result.msg = msg;
        result.code = enumStatusCode.getCode();
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<T>();
        result.msg = msg;
        result.code = enumStatusCode.UNKNOWNERROR.getCode();
        return result;
    }

}
