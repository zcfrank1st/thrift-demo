package com.chaoz.server;

import com.chaoz.thrift.service.RPCService;
import com.chaoz.thrift.gen.HelloWorldService;
import com.chaoz.zk.ZK;
import org.apache.curator.framework.CuratorFramework;
import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;

/**
 * Created by zcfrank1st on 1/15/16.
 */
public class ServerTemplate {
    private static CuratorFramework client = ZK.INSTANCE.createClient();

    // TODO logback
    // TODO 多版本 server

    public void run(TBaseProcessor processor, int port) {
        try {
            System.out.println("start registering ...");
            register();
            System.out.println("registered ...");

            System.out.println("monitor start ...");
            startMonitor();
            System.out.println("monitor running ...");

            System.out.println("TNonblockingServer start ....");

            TNonblockingServerSocket tnbSocketTransport = new TNonblockingServerSocket(port);

            TNonblockingServer.Args tnbArgs = new TNonblockingServer.Args(
                    tnbSocketTransport);
            tnbArgs.processor(processor);
            tnbArgs.transportFactory(new TFramedTransport.Factory());
            tnbArgs.protocolFactory(new TCompactProtocol.Factory());

            // 使用非阻塞式IO，服务端和客户端需要指定TFramedTransport数据传输的方式
            TServer server = new TNonblockingServer(tnbArgs);
            server.serve();
        } catch (Exception e) {
            System.out.println("Server start error!!!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerTemplate serverTemplate = new ServerTemplate();
        serverTemplate.run(new HelloWorldService.Processor<HelloWorldService.Iface>(
                new RPCService()), 11111);
    }

    private void startMonitor() {
        new Thread(() -> {
            while (true) {
                System.out.println("heartbeat info trans...");
                // TODO zk 交互更新心跳状态

                System.out.println("heartbeat info updated...");
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
}
