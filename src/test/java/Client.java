import com.chaoz.tframe.thrift.gen.HelloWorldService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by zcfrank1st on 1/13/16.
 */
public class Client {
    public static final String SERVER_IP = "localhost";
    public static final int SERVER_PORT = 11111;
    public static final int TIMEOUT = 30000;

    /**
     *
     * @param userName
     */
    public void startClient(String userName) {
        TTransport transport = null;
        try {
            transport = new TFramedTransport(new TSocket(SERVER_IP,
                    SERVER_PORT, TIMEOUT));
            // 协议要和服务端一致
            TProtocol protocol = new TCompactProtocol(transport);
            HelloWorldService.Client client = new HelloWorldService.Client(
                    protocol);
            transport.open();
            String result = client.sayHello(userName);
            System.out.println("Thrift client result =: " + result);
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            if (null != transport) {
                transport.close();
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
//        Client client = new Client();
//        client.startClient("{\"body\":{\"mid\":1,\"shopCarts\":[{\"brandSideId\":222,\"shopName\":\"LV\",\"cartItems\":[{\"itemDetailId\":1,\"quantity\":1,\"itemName\":\"mac air\",\"itemPhotoUrl\":\"//pic/1\",\"price\":500.0,\"stock\":1212,\"totalDiscountPrice\":500.0,\"slogans\":[[{\"type\":\"and\",\"sloganList\":[{\"used\":true,\"text\":\"限购1件\"}]}]],\"items\":[],\"extra\":{\"limit\":1},\"activities\":[[{\"selected\":0}]],\"isValid\":1}],\"shopTotalPrice\":500.0,\"shopPostageTotalPrice\":0.0,\"shopActivities\":[{\"used\":true,\"text\":\"包邮\"}]}]},\"code\":\"ITEM_0000\",\"description\":\"calculate successfully\"}");
    }
}
