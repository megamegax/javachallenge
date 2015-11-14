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
                doJob(i,0);
                i++;
            }
        }
    }

    private void doJob(int i,int unit) {
        service.startTurn(i);
      //  int unit = chooseBuilder();
        if (isUnitInSpaceComp(unit)) {
            System.out.println(unit+", is in space comp");
            service.structureTunnel(unit, moveOutFromSpaceComp());
            service.moveUnit(unit, moveOutFromSpaceComp());
            service.watch(unit);
            int repeat = 4;
            while (repeat >= 0 && service.serviceState.getActionPointsLeft() > 0) {
                repeat--;
                WsDirection direction = moveRandomly();
                service.structureTunnel(unit, direction);
                service.moveUnit(unit, direction);
            }
        } else {
            System.out.println(unit+", is NOT in space comp");

            //TODO építkezni, mozogni, nem visszalépni, figyelni mi merre van hajaj Marci alkoss valamit :D
            int repeat = 4;
            while (repeat >= 0 && service.serviceState.getActionPointsLeft() > 0) {
                repeat--;
                WsDirection direction = moveRandomly();
                if (service.structureTunnel(unit, direction)) {
                    service.moveUnit(unit, direction);
                    service.watch(unit);
                }else{
                    service.moveUnit(unit, direction);
                }
            }
        }
    }

    private int chooseBuilder() {
        return new Random().nextInt(3);
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
        System.out.println("getting unit: "+unitID);
        return service.builderUnits.get(unitID).getCord() == service.initialPos.getCord();
    }


}

