package com.chaoz.tframe.zk;

import com.chaoz.tframe.util.TConstants;
import com.chaoz.tframe.util.TConfig;
import com.chaoz.tframe.util.TUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;


/**
 * Created by zcfrank1st on 1/15/16.
 */
public enum TZK {
    INSTANCE;

    public CuratorFramework createClient() {
        TConfig config = TUtils.loadConfig();
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(config.getInt(TConstants.ZK_RETRY_SLEEP_TIME, 1000), config.getInt(TConstants.ZK_RETRY_TIMES, 3));
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(config.getString(TConstants.ZK_SERVER, "localhost:2181")).namespace(TConstants.SERVICE_NAME).retryPolicy(retryPolicy).build();
        client.start();
        return client;
    }
}
