package com.chaoz.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zcfrank1st on 1/15/16.
 */
public class Utils {
    private static String CONFIG_FILE = "config.properties";

    public static Config loadConfig() {
        InputStream in = Utils.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        Config conf = new Config();
        try {
            conf.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conf;
    }
}
