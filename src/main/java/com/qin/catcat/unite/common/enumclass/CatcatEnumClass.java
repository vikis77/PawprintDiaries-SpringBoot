package com.qin.catcat.unite.common.enumclass;

/**
 * @Description 状态码枚举类
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-04 00:35
 */
public class CatcatEnumClass {
    // 数字枚举类
    public enum NumberEnum {
        ONE(1),
        TWO(2),
        THREE(3);

        private final int value;

        NumberEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // 是否删除枚举
    public enum IsDeleteEnum {
        YES(1),
        NO(0);

        private final int value;

        IsDeleteEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // 状态码枚举类
    public enum StatusCode {
        // 系统状态码
        SUCSSESS(10001,"操作成功"),
        INTERNAL_SERVER_ERROR(10002, "服务器内部错误"),
        UNKNOWNERROR(10003,"未知错误"),
        // 用户状态码
        PASSWORD_INCORRECT(20001, "密码错误"),
        USERNAME_NOT_FOUND(20002, "用户名不存在"),
        JWT_IDENTITY_VERICION_FAILED(20003,"JWT身份校验失败"),
        PASSWORD_UPDATE_FAILURE(20004,"密码更新失败"),
        USER_ALREADY_EXISTS(20005,"用户名已存在，请重新输入"),
        TOKEN_EXPIRED(20006,"身份已过期，请重新登录"),
        TOKEN_INVALID(20007,"身份无效，请重新登录"),
        REQUEST_TOO_FREQUENT(20008,"请求太频繁，请稍后再试"),
        USER_NOT_FOUND_OR_ACCOUNT_STATUS_NOT_NORMAL(20009,"用户不存在或账号状态不正常，请检查用户ID是否正确"),
        UPDATE_PROFILE_FAILURE(20010,"更新用户信息失败"),
        USER_ALREADY_FOLLOWED(20011,"已经关注"),
        USER_NOT_FOLLOWED(20012,"还没有关注，不能取关"),
        UNAUTHORIZED(20013,"当前账号没有操作权限"),
        ACCESS_DENIED(20014,"访问被拒绝"),
        USER_NOT_FOUND(20015,"用户不存在"),
        // 帖子状态码
        POST_NOT_FOUND(30001,"帖子不存在"),
        POST_UPDATE_FAILURE(30002,"帖子更新失败"),
        POST_DELETE_FAILURE(30003,"帖子删除失败"),
        POST_NOT_APPROVED(30004,"帖子未通过审核"),
        POST_NOT_AUTHOR(30005,"无权删除帖子"),
        // 猫猫状态码
        CAT_NOT_FOUND(40001,"猫猫不存在"),
        CAT_UPDATE_FAILURE(40002,"猫猫更新失败"),
        CAT_DELETE_FAILURE(40003,"猫猫删除失败"),
        // 坐标状态码
        COORDINATE_NOT_FOUND(50001,"坐标不存在"),
        COORDINATE_UPDATE_FAILURE(50002,"坐标更新失败"),
        COORDINATE_DELETE_FAILURE(50003,"坐标删除失败"),
        // 评论状态码
        COMMENT_NOT_FOUND(60001,"评论不存在"),
        COMMENT_UPDATE_FAILURE(60002,"评论更新失败"),
        COMMENT_DELETE_FAILURE(60003,"评论删除失败"),

        ;
        private final Integer code;
        private final String message;

        StatusCode(Integer code, String message) {
            this.code = code;
            this.message = message;
        }

        public Integer getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
