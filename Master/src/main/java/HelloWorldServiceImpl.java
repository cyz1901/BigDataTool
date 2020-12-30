import org.apache.thrift.TException;

public class HelloWorldServiceImpl implements HelloWorldService.Iface {
    @Override
    public String say(String username) throws TException {
        return "Hello " + username;
    }
}
