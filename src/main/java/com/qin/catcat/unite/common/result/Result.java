package com.qin.catcat.unite.common.result;

import lombok.Data;

import java.io.Serializable;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import com.qin.catcat.unite.common.enumclass.CatcatEnumClass;
/**
 * 后端统一返回结果
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败
    private String msg; //信息
    private Integer totalPages; //总页数
    private T data; //数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = CatcatEnumClass.StatusCode.SUCSSESS.getCode();
        result.msg = "操作成功";
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = CatcatEnumClass.StatusCode.SUCSSESS.getCode();
        return result;
    }

    public static <T> Result<T> success(String msg) {
        Result<T> result = new Result<T>();
        result.msg = msg;
        result.code = CatcatEnumClass.StatusCode.SUCSSESS.getCode();
        return result;
    }

    public static <T> Result<T> success(T object,String msg) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.msg = msg;
        result.code = CatcatEnumClass.StatusCode.SUCSSESS.getCode();
        return result;
    }

    public static <T> Result<T> success(T object,Integer totalPages) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = CatcatEnumClass.StatusCode.SUCSSESS.getCode();
        result.totalPages = totalPages;
        return result;
    }

    public static <T> Result<T> error(String msg,Integer enumStatusCode) {
        Result<T> result = new Result<T>();
        result.msg = msg;
        result.code = enumStatusCode;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<T>();
        result.msg = msg;
        result.code = CatcatEnumClass.StatusCode.UNKNOWNERROR.getCode();
        return result;
    }

    public static <T> Result<T> fail() {
        Result<T> result = new Result<T>();
        result.code = CatcatEnumClass.StatusCode.UNKNOWNERROR.getCode();
        result.msg = "操作失败";
        return result;
    }

    public static <T> Result<T> fail(String msg) {
        Result<T> result = new Result<T>();
        result.msg = msg;
        result.code = CatcatEnumClass.StatusCode.UNKNOWNERROR.getCode();
        return result;
    }

    public static <T> Result<T> fail(Integer code,String msg) {
        Result<T> result = new Result<T>();
        result.msg = msg;
        result.code = code;
        return result;
    }

}
