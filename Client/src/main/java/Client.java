import eu.loxon.centralcontrol.WsCoordinate;
import jmx.javachallenge.service.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marton on 2015. 11. 12..
 */
public class Client {


    private static Service service;


    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello JMX");
        service = new Service();
        service.getSpaceShuttlePos();
        service.getSpaceShuttlePosExit();
        service.getActionCost();
        service.startGame();
        for (int i = 0; i < 1; i++) {
            Thread.sleep(301);
            if (service.isMyTurn()) {
                doJob(i);
            }
        }

    }

    private static void doJob(int i) {
        service.startTurn();
        System.out.println("kör:" + (i + 1));
        List<WsCoordinate> coordinates = new ArrayList<>();
        coordinates.add(service.unitState.get(Service.serviceState.getBuilderUnit()).getCord());
        service.radar(coordinates);

        //service.moveUnit(0, WsDirection.RIGHT);
        //service.getStats();
    }
}

