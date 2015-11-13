package jmx.javachallenge.client;

import eu.loxon.centralcontrol.WsCoordinate;
import eu.loxon.centralcontrol.WsDirection;
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
        if (isCurrentUnitInSpaceComp()) {
            service.moveUnit(moveOutFromSpaceComp());
            service.radar(radarAround());
        } else {
            //TODO építkezni, mozogni, nem visszalépni, figyelni mi merre van hajaj Marci alkoss valamit :D
        }
    }

    private List<WsCoordinate> radarAround() {
        List<WsCoordinate> coordinates = new ArrayList<>();
        coordinates.add(new WsCoordinate(Service.currentCoordinates.getX() + 1, Service.currentCoordinates.getY()));
        coordinates.add(new WsCoordinate(Service.currentCoordinates.getX() - 1, Service.currentCoordinates.getY()));
        coordinates.add(new WsCoordinate(Service.currentCoordinates.getX(), Service.currentCoordinates.getY() + 1));
        coordinates.add(new WsCoordinate(Service.currentCoordinates.getX(), Service.currentCoordinates.getY() - 1));
        return coordinates;
    }

    private WsDirection moveOutFromSpaceComp() {
        WsDirection direction = Util.calculateDirection(service.getSpaceShuttlePos().getCord(), service.getSpaceShuttlePosExit().getCord());
        return direction;
    }

    private boolean isCurrentUnitInSpaceComp() {
        return service.unitState.get(Service.serviceState.getBuilderUnit()).getCord() == Service.initialPos.getCord();
    }


}

