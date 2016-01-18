package com.chaoz.tframe.monitor;

import com.chaoz.tframe.zk.TFZk;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zcfrank1st on 1/18/16.
 */
public enum TFMonitor {
    INSTANCE;

    private static Logger logger = LoggerFactory.getLogger(TFMonitor.class);
    private CuratorFramework client = TFZk.INSTANCE.createClient();

    private boolean checkIfDead() {
        // TODO 检查服务状态mtime和“当前时间对比” 阈值
        return false;
    }

    private void putToDead() {
        // TODO 将服务放置/app/dead/ip:port, 供web统计
    }

    private void rmService() {
        // TODO 删除节点 /app/ip:port
    }

    public void run() {
        while (true) {
            if (checkIfDead()) {
                putToDead();
                rmService();
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        TFMonitor.INSTANCE.run();
    }
}
