
import eu.loxon.centralcontrol.*;
import jmx.javachallenge.service.Service;

import java.util.OptionalDouble;
import java.util.stream.Stream;

/**
 * Created by Marton on 2015. 11. 12..
 */
public class Client {


    private static Service service;

    public static void main(String[] args) {
        System.out.println("Hello JMX");
        service = new Service();
        service.getSpaceShuttlePos();
        service.getSpaceShuttlePosExit();
        service.getActionCost();
    }
}

