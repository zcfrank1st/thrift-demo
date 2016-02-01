package com.chaoz.tframe.client;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.transport.TTransport;

/**
 * Created by zcfrank1st on 1/20/16.
 */
public class TFClient {
    private TServiceClient client;
    private TTransport transport;

    public TServiceClient getClient() {
        return client;
    }

    public void setClient(TServiceClient client) {
        this.client = client;
    }

    public TTransport getTransport() {
        return transport;
    }

    public void setTransport(TTransport transport) {
        this.transport = transport;
    }
}
