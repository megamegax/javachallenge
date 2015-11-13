package jmx.javachallenge.client;

import eu.loxon.centralcontrol.WsCoordinate;
import eu.loxon.centralcontrol.WsDirection;
import jmx.javachallenge.helper.Util;
import jmx.javachallenge.service.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Marton on 2015. 11. 12..
 */
public class Client {
    private Service service;

    public void run() {
        System.out.println("Hello JMX");
        service = Service.getInstance();
        service.getSpaceShuttlePos();
        service.getSpaceShuttlePosExit();
        service.getActionCost();
        service.startGame();

        System.out.println("űrkomp merre néz:" + Util.calculateDirection(service.getSpaceShuttlePos().getCord(), service.getSpaceShuttlePosExit().getCord()).name());
        int i = 1;
        while (service.turnLeft != 1) {
            Util.wait(301);
            if (service.isMyTurn()) {
                doJob(i);
                i++;
            }
        }
    }

    private void doJob(int i) {
        service.startTurn(i);
        if (isCurrentUnitInSpaceComp()) {
            service.structureTunnel(moveOutFromSpaceComp());
            service.moveUnit(moveOutFromSpaceComp());
            service.radar(radarAround());
        } else {
            //TODO építkezni, mozogni, nem visszalépni, figyelni mi merre van hajaj Marci alkoss valamit :D
            WsDirection direction = moveRandomly();
            service.structureTunnel(direction);
            service.moveUnit(direction);
            service.radar(radarAround());
        }
    }

    private WsDirection moveRandomly() {
        Random random = new Random();
        int r = random.nextInt(4) + 1;
        if (r == 1) {
            return WsDirection.RIGHT;
        } else if (r == 2) {
            return WsDirection.LEFT;
        } else if (r == 3) {
            return WsDirection.UP;
        } else return WsDirection.DOWN;
    }

    private List<WsCoordinate> radarAround() {
        List<WsCoordinate> coordinates = new ArrayList<>();
        coordinates.add(new WsCoordinate(service.currentCoordinates.getX() + 1, service.currentCoordinates.getY()));
        coordinates.add(new WsCoordinate(service.currentCoordinates.getX() - 1, service.currentCoordinates.getY()));
        coordinates.add(new WsCoordinate(service.currentCoordinates.getX(), service.currentCoordinates.getY() + 1));
        coordinates.add(new WsCoordinate(service.currentCoordinates.getX(), service.currentCoordinates.getY() - 1));
        return coordinates;
    }

    private WsDirection moveOutFromSpaceComp() {
        WsDirection direction = Util.calculateDirection(service.getSpaceShuttlePos().getCord(), service.getSpaceShuttlePosExit().getCord());
        return direction;
    }

    private boolean isCurrentUnitInSpaceComp() {
        return service.builderUnits.get(service.serviceState.getBuilderUnit()).getCord() == service.initialPos.getCord();
    }


}

