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
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(TFUtils.conf.getInt(TFConstants.ZK_RETRY_SLEEP_TIME, 1000), TFUtils.conf.getInt(TFConstants.ZK_RETRY_TIMES, 3));
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(TFUtils.conf.getString(TFConstants.ZK_SERVER, "localhost:2181")).namespace(TFUtils.conf.getString(TFConstants.SERVICE_NAME, "app")).retryPolicy(retryPolicy).build();
        client.start();
        return client;
    }
}
