package com.chaoz.tframe.client;

import com.chaoz.tframe.exception.TFException;
import com.chaoz.tframe.zk.TFZk;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by zcfrank1st on 1/20/16.
 */
public class TFChannel {
    private TServiceClient client;
    private TTransport transport;
    private String host;
    private CuratorFramework c = TFZk.INSTANCE.createClient();

    public TFChannel (TServiceClient client, TTransport transport, String host) {
        this.client = client;
        this.transport = transport;
        this.host = host;
    }

    public void open () throws TTransportException {
        transport.open();
    }

    public TServiceClient  getClient () {
        return client;
    }

    public void close () {
        String path = "/" + host + "/cc";
        CuratorTransaction transaction = c.inTransaction();
        try {
            transaction.setData().forPath(path , (Integer.parseInt(new String (c.getData().forPath(path), "UTF-8")) - 1 + "").getBytes()).and().commit();
        } catch (Exception e) {
            throw new TFException("delete connections error");
        }
        transport.close();
    }
}
