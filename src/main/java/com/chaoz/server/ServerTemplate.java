package com.chaoz.server;

import com.chaoz.exception.ErrorCode;
import com.chaoz.exception.FrameworkException;
import com.chaoz.thrift.service.RPCService;
import com.chaoz.thrift.gen.HelloWorldService;
import com.chaoz.util.Constants;
import com.chaoz.zk.ZK;
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
public class ServerTemplate {
    private static Logger logger = LoggerFactory.getLogger(ServerTemplate.class);

    private static CuratorFramework client = ZK.INSTANCE.createClient();

    public void createServer(Class clazz) {

    }

    // TODO logback
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
            e.printStackTrace();
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
            throw new FrameworkException(ErrorCode.UNKONWN_HOST);
        }
        if (addr != null && null != addr.getHostAddress()) {
            return addr.getHostAddress();
        }

        throw new FrameworkException(ErrorCode.GET_IP_ERROR);
    }

    private String getServiceUrl() {
        return getCurrentIP() + ":" + Constants.SERVICE_PORT;
    }

    // for test
    public static void main(String[] args) {
        ServerTemplate serverTemplate = new ServerTemplate();
        serverTemplate.run(new HelloWorldService.Processor<HelloWorldService.Iface>(
                new RPCService()), 11111);
    }
}
