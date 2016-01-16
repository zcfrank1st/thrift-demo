package com.chaoz.tframe.server.factory;

import com.chaoz.tframe.exception.TErrorCode;
import com.chaoz.tframe.exception.TFrameworkException;
import com.chaoz.tframe.util.TConfig;
import com.chaoz.tframe.util.TConstants;
import com.chaoz.tframe.util.TUtils;
import com.chaoz.tframe.zk.TZK;
import org.apache.curator.framework.CuratorFramework;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.*;
import org.apache.thrift.transport.*;
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
    private static TConfig config = TUtils.loadConfig();

    public TServer getServer(Class clazz, TProcessor processor) {
        String serverName = clazz.getName();
        TServer server = null;
        TServerSocket socket = null;
        TNonblockingServerSocket tnbSocketTransport = null;
        switch (serverName) {
            case "org.apache.thrift.server.TThreadPoolServer":
                try {
                    socket = new TServerSocket(config.getInt(TConstants.SERVICE_PORT, 98765));
                } catch (TTransportException e) {
                    e.printStackTrace();
                }
                TThreadPoolServer.Args ttpsArgs = new TThreadPoolServer.Args(
                        socket);
                ttpsArgs.processor(processor);
                ttpsArgs.protocolFactory(new TBinaryProtocol.Factory());
                server = new TThreadPoolServer(ttpsArgs);
                break;
            case "org.apache.thrift.server.TNonblockingServer":
                tnbSocketTransport = null;
                try {
                    tnbSocketTransport = new TNonblockingServerSocket(config.getInt(TConstants.SERVICE_PORT, 98765));
                } catch (TTransportException e) {
                    e.printStackTrace();
                }
                TNonblockingServer.Args tnbArgs = new TNonblockingServer.Args(tnbSocketTransport);
                tnbArgs.processor(processor);
                tnbArgs.transportFactory(new TFramedTransport.Factory());
                tnbArgs.protocolFactory(new TCompactProtocol.Factory());

                // 使用非阻塞式IO，服务端和客户端需要指定TFramedTransport数据传输的方式
                server = new TNonblockingServer(tnbArgs);
                break;
            case "org.apache.thrift.server.THsHaServer":
                tnbSocketTransport = null;
                try {
                    tnbSocketTransport = new TNonblockingServerSocket(config.getInt(TConstants.SERVICE_PORT, 98765));
                } catch (TTransportException e) {
                    e.printStackTrace();
                }
                THsHaServer.Args thhsArgs = new THsHaServer.Args(tnbSocketTransport);
                thhsArgs.processor(processor);
                thhsArgs.transportFactory(new TFramedTransport.Factory());
                thhsArgs.protocolFactory(new TBinaryProtocol.Factory());

                //半同步半异步的服务模型
                server = new THsHaServer(thhsArgs);
                break;
            case "org.apache.thrift.server.TServer":
                try {
                    socket = new TServerSocket(config.getInt(TConstants.SERVICE_PORT, 98765));
                } catch (TTransportException e) {
                    e.printStackTrace();
                }
                TServer.Args tArgs = new TServer.Args(socket);
                tArgs.processor(processor);
                tArgs.protocolFactory(new TBinaryProtocol.Factory());
                server = new TSimpleServer(tArgs);
                break;
        }
       return server;
    }

    // TODO 多版本 server
    public void run(TServer server) {
        try {
            logger.info("service start registering ...");
            register();
            logger.info("service registered ...");

            logger.info("monitor start ...");
            startMonitor();
            logger.info("monitor running ...");

            logger.info("TNonblockingServer start ....");

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
//        TServerFactory.run(new HelloWorldService.Processor<HelloWorldService.Iface>(
//                new RPCService()), 11111);
    }
}
