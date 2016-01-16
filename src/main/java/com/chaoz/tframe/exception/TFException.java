package com.chaoz.tframe.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zcfrank1st on 1/15/16.
 */
public class TFException extends RuntimeException {
    private static Logger logger = LoggerFactory.getLogger(TFException.class);

    private TFErrorCode enums;

    public TFException() {
        super();
    }

    public TFException(String message) {
        super(message);
    }

    public TFException(String message, Throwable cause) {
        super(message, cause);
    }

    public TFException(Throwable cause) {
        super(cause);
    }

    protected TFException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TFException(TFErrorCode enums) {
        super(enums.getDescription());
        this.enums = enums;

        logger.error("framework error" + "{"+ enums.getCode() +"}," + "{"+ enums.getDescription() +"}");
    }
}
