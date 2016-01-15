package com.chaoz.zk;

import com.chaoz.util.Config;
import com.chaoz.util.Constants;
import com.chaoz.util.Utils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;


/**
 * Created by zcfrank1st on 1/15/16.
 */
public enum ZK {
    INSTANCE;

    public CuratorFramework createClient() {
        Config config = Utils.loadConfig();
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(config.getInt(Constants.ZK_RETRY_SLEEP_TIME, 1000), config.getInt(Constants.ZK_RETRY_TIMES, 3));
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(config.getString(Constants.ZK_SERVER, "localhost:2181")).namespace(Constants.SERVICE_NAME).retryPolicy(retryPolicy).build();
        client.start();
        return client;
    }
}
