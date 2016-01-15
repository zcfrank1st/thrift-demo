package com.chaoz.tframe.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zcfrank1st on 1/15/16.
 */
public class TFrameworkException extends RuntimeException {
    private static Logger logger = LoggerFactory.getLogger(TFrameworkException.class);

    private TErrorCode enums;

    public TFrameworkException() {
        super();
    }

    public TFrameworkException(String message) {
        super(message);
    }

    public TFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public TFrameworkException(Throwable cause) {
        super(cause);
    }

    protected TFrameworkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TFrameworkException(TErrorCode enums) {
        super(enums.getDescription());
        this.enums = enums;

        logger.error("framework error" + "{"+ enums.getCode() +"}," + "{"+ enums.getDescription() +"}");
    }
}
