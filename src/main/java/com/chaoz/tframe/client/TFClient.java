package com.chaoz.tframe.client;

import com.chaoz.tframe.zk.TFZk;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zcfrank1st on 1/18/16.
 */
public enum TFClient {
    INSTANCE;

    private static Logger logger = LoggerFactory.getLogger(TFClient.class);
    private CuratorFramework client = TFZk.INSTANCE.createClient();

    public void init() {
        // TODO check zk info, get the live and min connection one
        // TODO update connections
        // TODO buildClient
    }

    private String checkAndGetConnectionURL () {
        return "";
    }

    private void updateConnections() {

    }


    private void buildClient() {

    }

    // for test
    public static void main(String[] args) {

    }
}
