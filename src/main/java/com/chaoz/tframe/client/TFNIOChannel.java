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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by zcfrank1st on 1/18/16.
 */
public enum TFNIOChannel {
    INSTANCE;

    private static Logger logger = LoggerFactory.getLogger(TFNIOChannel.class);
    private CuratorFramework client = TFZk.INSTANCE.createClient();

    public TFChannel init(Class<?> clientClass) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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
        addConnections(host[0]);
        return buildChannel(host[0], clientClass);

    }

    private void addConnections(String host) {
        String path = "/" + host + "/cc";
        CuratorTransaction transaction = client.inTransaction();
        try {
            transaction.setData().forPath(path , (1 + Integer.parseInt(new String (client.getData().forPath(path), "UTF-8")) + "").getBytes()).and().commit();
        } catch (Exception e) {
            throw new TFException("");
        }
    }


    private TFChannel buildChannel(String host, Class<?> clientClass) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String[] hostParts = host.split(":");
        TTransport transport = new TFramedTransport(new TSocket(hostParts[0], Integer.valueOf(hostParts[1]), 15000));
        TProtocol protocol = new TCompactProtocol(transport);
        Constructor con = clientClass.getConstructor(Class.forName(TProtocol.class.getName()));

        return new TFChannel((TServiceClient) con.newInstance(protocol), transport);
    }

    // for test
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, TException, InterruptedException {
        TFChannel channel = TFNIOChannel.INSTANCE.init(Class.forName(HelloWorldService.Client.class.getName()));
        channel.open();
        HelloWorldService.Client client = (HelloWorldService.Client)channel.getClient();

        while (true) {
            System.out.println(client.sayHello("haha"));
            Thread.sleep(2000);
        }
        //channel.close();
    }
}
