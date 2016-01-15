package com.chaoz.tframe.server.factory;

import com.chaoz.tframe.exception.TErrorCode;
import com.chaoz.tframe.exception.TFrameworkException;
import com.chaoz.tframe.thrift.gen.HelloWorldService;
import com.chaoz.tframe.thrift.service.RPCService;
import com.chaoz.tframe.util.TConstants;
import com.chaoz.tframe.zk.TZK;
import org.apache.curator.framework.CuratorFramework;
import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by zcfrank1st on 1/15/16.
 */
public class TServerFactory {
    private static Logger logger = LoggerFactory.getLogger(TServerFactory.class);

    private static CuratorFramework client = TZK.INSTANCE.createClient();

    public void getServer(Class clazz) {

    }

    // TODO 多版本 server
    public void run(TBaseProcessor processor, int port) {
        try {
            logger.info("service start registering ...");
            register();
            logger.info("service registered ...");

            logger.info("monitor start ...");
            startMonitor();
            logger.info("monitor running ...");

            logger.info("TNonblockingServer start ....");

            TNonblockingServerSocket tnbSocketTransport = new TNonblockingServerSocket(port);

            TNonblockingServer.Args tnbArgs = new TNonblockingServer.Args(
                    tnbSocketTransport);
            tnbArgs.processor(processor);
            tnbArgs.transportFactory(new TFramedTransport.Factory());
            tnbArgs.protocolFactory(new TCompactProtocol.Factory());

            TServer server = new TNonblockingServer(tnbArgs);
            server.serve();
        } catch (Exception e) {
            logger.error("Server start error!!!");
            throw new TFrameworkException(TErrorCode.SERVER_START_ERROR);
        }
    }

    private void startMonitor() {
        new Thread(() -> {
            while (true) {
                // TODO zk 交互更新心跳状态

                logger.info("heartbeat info updated...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void register() {
        // TODO 将服务注册到zk
    }

    private String getCurrentIP() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            logger.error(e.getMessage());
            throw new TFrameworkException(TErrorCode.UNKONWN_HOST);
        }
        if (addr != null && null != addr.getHostAddress()) {
            return addr.getHostAddress();
        }

        throw new TFrameworkException(TErrorCode.GET_IP_ERROR);
    }

    private String getServiceUrl() {
        return getCurrentIP() + ":" + TConstants.SERVICE_PORT;
    }

    // for test
    public static void main(String[] args) {
        TServerFactory TServerFactory = new TServerFactory();
        TServerFactory.run(new HelloWorldService.Processor<HelloWorldService.Iface>(
                new RPCService()), 11111);
    }
}
