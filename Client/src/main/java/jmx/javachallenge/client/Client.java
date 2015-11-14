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
        service.startGame();
        service.getSpaceShuttlePos();
        service.getSpaceShuttlePosExit();
        service.getActionCost();


        System.out.println("űrkomp merre néz:" + Util.calculateDirection(service.getSpaceShuttlePos().getCord(), service.getSpaceShuttlePosExit().getCord()).name());
        int i = 1;
        while (service.turnLeft != 0) {
            Util.wait(301);
            if (service.isMyTurn()) {
                doJob(i);
                i++;
            }
        }
    }

    private void doJob(int i) {
        service.startTurn(i);
        if (isUnitInSpaceComp(0)) {
            service.structureTunnel(0, moveOutFromSpaceComp());
            service.moveUnit(0, moveOutFromSpaceComp());
            service.watch(0);
          while(service.serviceState.getActionPointsLeft()>0){

          }
        } else {
            //TODO építkezni, mozogni, nem visszalépni, figyelni mi merre van hajaj Marci alkoss valamit :D

            WsDirection direction = moveRandomly();
            service.structureTunnel(0, direction);
            service.moveUnit(0, direction);
            service.radar(0, radarAround(0));
        }
    }

    private WsDirection moveRandomly() {
        Random random = new Random();
        int r = random.nextInt(10) + 1;
        if (r < 5) {
            return WsDirection.RIGHT;
        } else if (r <= 7) {
            return WsDirection.LEFT;
        } else if (r < 9) {
            return WsDirection.UP;
        } else return WsDirection.DOWN;
    }

    private List<WsCoordinate> radarAround(int unitID) {
        List<WsCoordinate> coordinates = new ArrayList<>();
        coordinates.add(new WsCoordinate(service.selectBuilder(unitID).getCord().getX() + 1, service.selectBuilder(unitID).getCord().getY()));
        coordinates.add(new WsCoordinate(service.selectBuilder(unitID).getCord().getX() - 1, service.selectBuilder(unitID).getCord().getY()));
        coordinates.add(new WsCoordinate(service.selectBuilder(unitID).getCord().getX(), service.selectBuilder(unitID).getCord().getY() + 1));
        coordinates.add(new WsCoordinate(service.selectBuilder(unitID).getCord().getX(), service.selectBuilder(unitID).getCord().getY() - 1));
        return coordinates;
    }

    private WsDirection moveOutFromSpaceComp() {
        WsDirection direction = Util.calculateDirection(service.getSpaceShuttlePos().getCord(), service.getSpaceShuttlePosExit().getCord());
        return direction;
    }

    private boolean isUnitInSpaceComp(int unitID) {
        return service.builderUnits.get(unitID).getCord() == service.initialPos.getCord();
    }


}

