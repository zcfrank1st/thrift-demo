package com.chaoz.tframe.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zcfrank1st on 1/15/16.
 */
public class TFUtils {
    private static String CONFIG_FILE = "config.properties";

    public static TFConfig loadConfig() {
        InputStream in = TFUtils.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        TFConfig conf = new TFConfig();
        try {
            conf.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conf;
    }
}
