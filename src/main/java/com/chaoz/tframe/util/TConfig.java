package com.chaoz.tframe.util;

import java.util.Properties;

/**
 * Created by zcfrank1st on 1/15/16.
 */
public class TConfig extends Properties {

    public TConfig() {
        super();
    }

    public int getInt(String key, int defaultValue) {
        String value = this.getProperty(key);
        if ("".equals(value))
            return defaultValue;

        return Integer.valueOf(value);
    }

    public String getString(String key, String defaultValue) {
        return this.getProperty(key, defaultValue);
    }
}
