package jmx.javachallenge.client;

import eu.loxon.centralcontrol.WsCoordinate;
import jmx.javachallenge.helper.Util;
import jmx.javachallenge.service.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marton on 2015. 11. 12..
 */
public class Client {
    private Service service;

    public void run() {
        System.out.println("Hello JMX");
        service = new Service();
        service.getSpaceShuttlePos();
        service.getSpaceShuttlePosExit();
        service.getActionCost();
        service.startGame();

        System.out.println("űrkomp merre néz:" + Util.calculateDirection(service.getSpaceShuttlePos().getCord(), service.getSpaceShuttlePosExit().getCord()).name());
        for (int i = 0; i < 1; i++) {
            Util.wait(300);
            if (service.isMyTurn()) {
                doJob(i);
            }
        }

    }


    private void doJob(int i) {
        service.startTurn(i);
        List<WsCoordinate> coordinates = new ArrayList<>();
        //service.serviceState.getBuilderUnit()
        coordinates.add(service.unitState.get(Service.serviceState.getBuilderUnit()).getCord());
        service.radar(coordinates);

        //service.moveUnit(0, WsDirection.RIGHT);
        //service.getStats();
    }


}

