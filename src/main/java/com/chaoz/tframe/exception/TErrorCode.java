package com.chaoz.tframe.exception;

/**
 * Created by zcfrank1st on 1/15/16.
 */
public enum TErrorCode {
    SUCCESS("0000","调用成功"),
    SERVER_START_ERROR("1000", "服务器端启动失败"),

    UNKONWN_HOST("9000", "未知host"),
    GET_IP_ERROR("9001", "获取本机ip失败");


    // 接口返回编码
    private String code;

    // 接口返回编码描述
    private String description;

    TErrorCode(String code, String description) {
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