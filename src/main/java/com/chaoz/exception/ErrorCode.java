package com.chaoz.exception;

/**
 * Created by zcfrank1st on 1/15/16.
 */
public enum ErrorCode {
    SUCCESS("0000","调用成功"),
    GET_IP_ERROR("9001", "获取本机ip失败");


    // 接口返回编码
    private String code;

    // 接口返回编码描述
    private String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
