package com.chaoz.exception;

/**
 * Created by zcfrank1st on 1/15/16.
 */
public class FrameworkException extends RuntimeException {
    private ErrorCode enums;

    public FrameworkException() {
        super();
    }

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrameworkException(Throwable cause) {
        super(cause);
    }

    protected FrameworkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FrameworkException(ErrorCode enums) {
        super(enums.getDescription());
        this.enums = enums;

        //logger
    }
}
