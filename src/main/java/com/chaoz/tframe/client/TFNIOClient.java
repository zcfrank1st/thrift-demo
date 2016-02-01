package com.chaoz.tframe.client;

import com.chaoz.tframe.exception.TFException;
import com.chaoz.tframe.thrift.gen.HelloWorldService;
import com.chaoz.tframe.zk.TFZk;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by zcfrank1st on 1/18/16.
 */
public enum TFNIOClient {
    INSTANCE;

    private static Logger logger = LoggerFactory.getLogger(TFNIOClient.class);
    private CuratorFramework client = TFZk.INSTANCE.createClient();

    public TFClient init() {
        List<String> hosts = null;
        try {
            hosts = client.getChildren().forPath("/");
        } catch (Exception e) {
            throw new TFException("");
        }

        final int[] minConnections = {Integer.MAX_VALUE};
        final String[] host = {""};

        hosts.forEach(e -> {
            if (! "dead".equals(e)) {
                byte[] bytes = null;
                try {
                    bytes = client.getData().forPath("/" + e + "/cc");
                } catch (Exception e1) {
                    throw new TFException("");
                }
                int connections = 0;
                try {
                    connections = Integer.parseInt(new String(bytes,"UTF-8"));
                } catch (UnsupportedEncodingException e1) {
                    throw new TFException();
                }
                if (minConnections[0] > connections) {
                    host[0] = e;
                    minConnections[0] = connections;
                }
            }
        });

        // FIXME 最小连接算法,存在误差(忽略)
        updateConnections(host[0]);
        return buildClient(host[0]);

    }

    private void updateConnections(String host) {
        String path = "/" + host + "/cc";
        CuratorTransaction transaction = client.inTransaction();
        try {
            transaction.setData().forPath(path , (1 + Integer.parseInt(new String (client.getData().forPath(path), "UTF-8")) + "").getBytes()).and().commit();
        } catch (Exception e) {
            throw new TFException("");
        }
    }


    private TFClient buildClient(String host) {
        String[] hostParts = host.split(":");
        TTransport transport = null;
        try {
            transport = new TFramedTransport(new TSocket(hostParts[0],
                    Integer.valueOf(hostParts[1]), 15000));
            TProtocol protocol = new TCompactProtocol(transport);
            //TODO service to build
            HelloWorldService.Client client = new HelloWorldService.Client(
                    protocol);
            TFClient tfClient = new TFClient();
            tfClient.setClient(client);
            tfClient.setTransport(transport);
            return tfClient;
        } finally {
            if (null != transport) {
                transport.close();
            }
        }
    }

    // for test
    public static void main(String[] args) {
        TFClient client = TFNIOClient.INSTANCE.init();
        try {
            client.getTransport().open();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
        HelloWorldService.Client client1 = (HelloWorldService.Client)client.getClient();
        try {
            System.out.println(client1.sayHello("haha"));
        } catch (TException e) {
            e.printStackTrace();
        }
        client.getTransport().close();
    }
}
