package com.chaoz.tframe.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zcfrank1st on 1/15/16.
 */
public class TFUtils {

    public static TFConfig conf;

    static {
        String CONFIG_FILE = "config.properties";
        InputStream in = TFUtils.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        conf = new TFConfig();
        try {
            conf.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
