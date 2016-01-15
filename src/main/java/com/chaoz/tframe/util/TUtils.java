package com.chaoz.tframe.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zcfrank1st on 1/15/16.
 */
public class TUtils {
    private static String CONFIG_FILE = "config.properties";

    public static TConfig loadConfig() {
        InputStream in = TUtils.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        TConfig conf = new TConfig();
        try {
            conf.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conf;
    }
}
