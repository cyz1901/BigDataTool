package pers.cyz.BigDataTool.Master.Impl;

import org.apache.thrift.TException;
import pers.cyz.BigDataTool.Master.ThirftService.HelloWorldService;

public class HelloWorldServiceImpl implements HelloWorldService.Iface {
    @Override
    public String say(String username) throws TException {
        return "Hello " + username;
    }
}
