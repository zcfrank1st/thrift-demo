package com.chaoz.tframe.zk;

import com.chaoz.tframe.util.TFConstants;
import com.chaoz.tframe.util.TFConfig;
import com.chaoz.tframe.util.TFUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;


/**
 * Created by zcfrank1st on 1/15/16.
 */
public enum TFZk {
    INSTANCE;

    public CuratorFramework createClient() {
        TFConfig config = TFUtils.loadConfig();
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(config.getInt(TFConstants.ZK_RETRY_SLEEP_TIME, 1000), config.getInt(TFConstants.ZK_RETRY_TIMES, 3));
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(config.getString(TFConstants.ZK_SERVER, "localhost:2181")).namespace(TFConstants.SERVICE_NAME).retryPolicy(retryPolicy).build();
        client.start();
        return client;
    }
}
