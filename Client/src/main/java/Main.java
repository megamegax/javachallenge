import hu.jmx.javachallenge.client.Client;

/**
 * Created by MegaX on 2015. 11. 13..
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        Client client;
        if (args.length < 3) {
            System.out.println("No parameters, using defaults");
            client = new Client();
        } else {
            if (!args[0].equals("http://javachallenge.loxon.hu:8443/engine/CentralControl?wsdl")) {
                System.out.println("Wrong WSDL url");
            }
            client = new Client(args[1], args[2]);
        }
        client.run();
    }
}
