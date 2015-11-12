
import eu.loxon.centralcontrol.CentralControl;
import eu.loxon.centralcontrol.CentralControlServiceService;
import eu.loxon.centralcontrol.StartGameRequest;
import eu.loxon.centralcontrol.StartGameResponse;
import java.util.OptionalDouble;
import java.util.stream.Stream;

/**
 * Created by Marton on 2015. 11. 12..
 */
public class Client {
    static {
        java.net.Authenticator.setDefault(new java.net.Authenticator() {
            @Override
            protected java.net.PasswordAuthentication getPasswordAuthentication() {
                return new java.net.PasswordAuthentication("jmx", "XWHD7855".toCharArray());
            }
        });
    }

    public static void main(String[] args) {
        System.out.println("Hello JMX");
        CentralControlServiceService service = new CentralControlServiceService();
        CentralControl client = service.getCentralControlPort();
        StartGameResponse res = client.startGame(new StartGameRequest());

        OptionalDouble average = Stream.iterate(0, e -> e+1)
                .limit(40)
                .mapToLong(e -> {
                    long before = System.currentTimeMillis();
                    client.startGame(new StartGameRequest());
                    return System.currentTimeMillis()-before;
                })
                .average();
        if(average.isPresent()){
            System.out.println(average.getAsDouble());
        }
    }
}
