package com.chaoz.tframe.thrift.service;

import com.chaoz.tframe.thrift.gen.HelloWorldService;
import org.apache.thrift.TException;

/**
 * Created by zcfrank1st on 1/13/16.
 */
public class RPCService implements HelloWorldService.Iface {

    @Override
    public String sayHello(String username) throws TException {
        return username;
    }
}
