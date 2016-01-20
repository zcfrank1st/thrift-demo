package com.chaoz.tframe.exception;

/**
 * Created by zcfrank1st on 1/15/16.
 */
public enum TFErrorCode {
    SUCCESS("0000","调用成功"),

    SERVER_START_ERROR("1000", "服务器端启动失败"),
    UNKNOWN_SERVER_TYPE("1001", "未知的服务器类型"),

    SERVICE_REGISTER_ERROR("2000", "服务注册错误"),
    HEARBEAT_UPDATE_FAILED("2001", "心跳更新失败"),
    GET_PATH_STATUS_ERROR("2002", "获取路径状态失败"),
    OBTAIN_CHILDREN_NODE_ERROR("2003", "获取子节点信息失败"),
    PUT_NODE_TO_DEAD_ERROR("2004", "将服务节点信息注册到dead失败"),
    REMOVE_SERVICE_ERROR("2005", "删除服务节点信息失败"),
    CREATE_DEAD_PATH_ERROR("2006", "创建死路径失败"),


    THRIFT_TRANSPORT_ERROR("8000", "thrift transport error"),
    UNKONWN_HOST("9000", "未知host"),
    GET_IP_ERROR("9001", "获取本机ip失败"),
    THREAD_INTERRUPTED("9002", "线程被中断");


    // 接口返回编码
    private String code;

    // 接口返回编码描述
    private String description;

    TFErrorCode(String code, String description) {
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
