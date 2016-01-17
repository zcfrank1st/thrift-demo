package com.chaoz.tframe.server;

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
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by zcfrank1st on 1/15/16.
 */
public class  TFServerTemplate {

    private static Logger logger = LoggerFactory.getLogger(TFServerTemplate.class);
    private CuratorFramework client = TFZk.INSTANCE.createClient();

    public TFServerTemplate() {
    }

    private static TFConfig config = TFUtils.loadConfig();

    private static String route = "";

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

    public TFServerTemplate startMonitor() {
        new Thread(() -> {
            while (true) {
                try {
                    client.setData().forPath(route, "heartbeat".getBytes());
                    logger.info("heartbeat info updated...");
                } catch (Exception e) {
                    logger.info("heartbeat info updated failed, caused by: " + e.getMessage());
                    throw new TFException(TFErrorCode.HEARBEAT_UPDATE_FAILED);
                }
                try {
                    Thread.sleep(config.getInt(TFConstants.HEARTBEAT, 3000));
                } catch (InterruptedException e) {
                    logger.error("thread is interrupted, caused by: " + e.getMessage());
                    throw new TFException(TFErrorCode.THREAD_INTERRUPTED);
                }
            }
        }).start();

        return this;
    }

    public TFServerTemplate register() {
        try {
            route = "/" + getServiceConnection();
            client.create().forPath(route);
            client.getData().usingWatcher((Watcher) watchedEvent -> {
                // TODO send email or text mail, now do nothing
            }).inBackground().forPath("/" + getServiceConnection());
        } catch (Exception e) {
            logger.error("runtime error, caused by: " + e.getMessage());
            throw new TFException(TFErrorCode.SERVICE_REGISTER_ERROR);
        }
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

    private String getServiceConnection() {
        return getCurrentIP() + ":" + config.getInt(TFConstants.SERVICE_PORT, 98765);
    }

    // for test
    public static void main(String[] args) {
        logger.info("server starting ...");
        new TFServerTemplate()
                .register()
                .startMonitor()
                .getServer(TServer.class, new HelloWorldService.Processor<>(new RPCService()))
                .serve();
    }
}
