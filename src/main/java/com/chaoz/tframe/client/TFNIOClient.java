package com.chaoz.tframe.client;

import com.chaoz.tframe.exception.TFException;
import com.chaoz.tframe.thrift.gen.HelloWorldService;
import com.chaoz.tframe.zk.TFZk;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by zcfrank1st on 1/18/16.
 */
public enum TFNIOClient {
    INSTANCE;

    private static Logger logger = LoggerFactory.getLogger(TFNIOClient.class);
    private CuratorFramework client = TFZk.INSTANCE.createClient();

    public TFClient init(Class<?> clientClass) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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
        return buildClient(host[0], clientClass);

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


    private TFClient buildClient(String host, Class<?> clientClass) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String[] hostParts = host.split(":");
        TTransport transport = new TFramedTransport(new TSocket(hostParts[0], Integer.valueOf(hostParts[1]), 15000));
        TProtocol protocol = new TCompactProtocol(transport);
        Constructor con = clientClass.getConstructor(Class.forName(TProtocol.class.getName()));
        TFClient tfClient = new TFClient();
        tfClient.setClient((TServiceClient) con.newInstance(protocol));
        tfClient.setTransport(transport);
        return tfClient;
    }

    // for test
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        TFClient client = TFNIOClient.INSTANCE.init(Class.forName(HelloWorldService.Client.class.getName()));
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
