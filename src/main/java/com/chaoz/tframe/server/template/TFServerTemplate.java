package com.chaoz.tframe.server.template;

import com.chaoz.tframe.exception.TFErrorCode;
import com.chaoz.tframe.exception.TFException;
import com.chaoz.tframe.thrift.gen.HelloWorldService;
import com.chaoz.tframe.thrift.service.RPCService;
import com.chaoz.tframe.util.TFConfig;
import com.chaoz.tframe.util.TFConstants;
import com.chaoz.tframe.util.TFUtils;
import com.chaoz.tframe.zk.TFZk;
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
public enum  TFServerTemplate {
    INSTANCE;

    private static Logger logger = LoggerFactory.getLogger(TFServerTemplate.class);

    private static CuratorFramework client = TFZk.INSTANCE.createClient();
    private static TFConfig config = TFUtils.loadConfig();

    public TServer getServer(Class clazz, TProcessor processor) {
        String serverName = clazz.getName();
        TServer server;
        TServerSocket socket;
        TNonblockingServerSocket tnbSocketTransport;
        switch (serverName) {
            case "org.apache.thrift.server.TThreadPoolServer":
                try {
                    socket = new TServerSocket(config.getInt(TFConstants.SERVICE_PORT, 98765));
                } catch (TTransportException e) {
                    logger.error("TransportException, caused by: " + e.getMessage());
                    throw new TFException(TFErrorCode.THRIFT_TRANSPORT_ERROR);
                }
                TThreadPoolServer.Args ttpsArgs = new TThreadPoolServer.Args(
                        socket);
                ttpsArgs.processor(processor);
                ttpsArgs.protocolFactory(new TBinaryProtocol.Factory());
                server = new TThreadPoolServer(ttpsArgs);
                break;
            case "org.apache.thrift.server.TNonblockingServer":
                try {
                    tnbSocketTransport = new TNonblockingServerSocket(config.getInt(TFConstants.SERVICE_PORT, 98765));
                } catch (TTransportException e) {
                    logger.error("TransportException, caused by: " + e.getMessage());
                    throw new TFException(TFErrorCode.THRIFT_TRANSPORT_ERROR);
                }
                TNonblockingServer.Args tnbArgs = new TNonblockingServer.Args(tnbSocketTransport);
                tnbArgs.processor(processor);
                tnbArgs.transportFactory(new TFramedTransport.Factory());
                tnbArgs.protocolFactory(new TCompactProtocol.Factory());

                // 使用非阻塞式IO，服务端和客户端需要指定TFramedTransport数据传输的方式
                server = new TNonblockingServer(tnbArgs);
                break;
            case "org.apache.thrift.server.THsHaServer":
                try {
                    tnbSocketTransport = new TNonblockingServerSocket(config.getInt(TFConstants.SERVICE_PORT, 98765));
                } catch (TTransportException e) {
                    logger.error("TransportException, caused by: " + e.getMessage());
                    throw new TFException(TFErrorCode.THRIFT_TRANSPORT_ERROR);
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
                    socket = new TServerSocket(config.getInt(TFConstants.SERVICE_PORT, 98765));
                } catch (TTransportException e) {
                    logger.error("TransportException, caused by: " + e.getMessage());
                    throw new TFException(TFErrorCode.THRIFT_TRANSPORT_ERROR);
                }
                TServer.Args tArgs = new TServer.Args(socket);
                tArgs.processor(processor);
                tArgs.protocolFactory(new TBinaryProtocol.Factory());
                server = new TSimpleServer(tArgs);
                break;
            default:
                throw new TFException(TFErrorCode.UNKNOWN_SERVER_TYPE);
        }
       return server;
    }

    private TFServerTemplate setMonitor() {
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

        return this;
    }

    private TFServerTemplate register() {
        // TODO 将服务注册到zk
        return this;
    }

    private String getCurrentIP() {
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            logger.error(e.getMessage());
            throw new TFException(TFErrorCode.UNKONWN_HOST);
        }
        if (addr != null && null != addr.getHostAddress()) {
            return addr.getHostAddress();
        }

        throw new TFException(TFErrorCode.GET_IP_ERROR);
    }

    private String getServiceUrl() {
        return getCurrentIP() + ":" + TFConstants.SERVICE_PORT;
    }

    // for test
    public static void main(String[] args) {
        logger.info("server starting ...");
        TFServerTemplate
                .INSTANCE
                .register()
                .setMonitor()
                .getServer(TServer.class, new HelloWorldService.Processor<>(new RPCService()))
                .serve();
    }
}
