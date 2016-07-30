package com.chaoz.tframe.client;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by zcfrank1st on 1/20/16.
 */
public class TFChannel {
    private TServiceClient client;
    private TTransport transport;

    public TFChannel (TServiceClient client, TTransport transport) {
        this.client = client;
        this.transport = transport;
    }

    public void open () throws TTransportException {
        transport.open();
    }

    public TServiceClient  getClient () {
        return client;
    }

    public void close () {
        // TODO zk 删除连接数
        transport.close();
    }
}
