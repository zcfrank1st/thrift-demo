package com.chaoz.tframe.monitor;

import com.chaoz.tframe.exception.TFErrorCode;
import com.chaoz.tframe.exception.TFException;
import com.chaoz.tframe.util.TFConstants;
import com.chaoz.tframe.util.TFUtils;
import com.chaoz.tframe.zk.TFZk;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zcfrank1st on 1/18/16.
 */
//TODO 时间同步
public enum TFMonitor {
    INSTANCE;

    private static Logger logger = LoggerFactory.getLogger(TFMonitor.class);

    private static long THRESHOLD_TIME = TFUtils.conf.getInt(TFConstants.MONITOR_THRESHOLD_TIME, 15000);
    private static long TICK_TIME = TFUtils.conf.getInt(TFConstants.MONITOR_TICK_TIME, 3000);

    private CuratorFramework client = TFZk.INSTANCE.createClient();

    private void checkAllIfDead() {
        try {
            List<String> childrenNames = client.getChildren().forPath("/");
            childrenNames.forEach(e -> {
                if (! "dead".equals(e)) {
                    String path = "/" + e;
                    try {
                        long mTime = client.checkExists().forPath(path).getMtime();
                        long now = System.currentTimeMillis();

                        if (now - mTime > THRESHOLD_TIME) {
                            rmService(e);
                            putToDead(e);
                        }
                    } catch (Exception e1) {
                        logger.error("get path status failed, caused by : " + e1.getMessage());
                        throw new TFException(TFErrorCode.GET_PATH_STATUS_ERROR);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("obtain children node error, caused by: " + e.getMessage());
            throw new TFException(TFErrorCode.OBTAIN_CHILDREN_NODE_ERROR);
        }
    }

    private void putToDead(String path) {
        String deadPath = "/dead/" + path;
        try {
            if (null == client.checkExists().forPath(deadPath))
                client.create().forPath(deadPath);
        } catch (Exception e) {
            logger.error("put to dead error, caused by: " + e.getMessage());
            throw new TFException(TFErrorCode.PUT_NODE_TO_DEAD_ERROR);
        }
    }

    private void rmService(String path) {
        try {
            client.delete().deletingChildrenIfNeeded().forPath("/" + path);
        } catch (Exception e) {
            logger.error("remove service error ,caused by: " + e.getMessage());
            throw new TFException(TFErrorCode.REMOVE_SERVICE_ERROR);
        }
    }

    public void run() {
        try {
            if (null == client.checkExists().forPath("/dead")) {
                client.create().forPath("/dead");
            }
        } catch (Exception e) {
            logger.error("add dead path error, caused by: "  + e.getMessage());
            throw new TFException(TFErrorCode.CREATE_DEAD_PATH_ERROR);
        }
        while (true) {
            checkAllIfDead();
            try {
                Thread.sleep(TICK_TIME);
            } catch (InterruptedException e) {
                logger.error("sleep thread was interrupted, caused by: " + e.getMessage());
                throw new TFException(TFErrorCode.THREAD_INTERRUPTED);
            }
        }
    }

    public static void main(String[] args) {
        TFMonitor.INSTANCE.run();
    }
}
