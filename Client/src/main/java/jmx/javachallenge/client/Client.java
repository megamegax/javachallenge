package jmx.javachallenge.client;

import eu.loxon.centralcontrol.WsCoordinate;
import eu.loxon.centralcontrol.WsDirection;
import jmx.javachallenge.helper.Step;
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
        while (service.turnLeft != 0) {
            Util.wait(301);
            if (service.isMyTurn()) {
                doJob(service.selectedBuilder);
            }
        }
    }

    private void doJob(int unitID) {
        //  int unitID = chooseBuilder();
        if (isUnitInSpaceComp(unitID)) {
            System.out.println(unitID + ", is in space comp");
            if (unitID == 0) {
                service.watch(unitID);
                service.structureTunnel(unitID, moveOutFromSpaceComp());
            }
            service.moveUnit(unitID, moveOutFromSpaceComp());
            service.watch(unitID);
            //  doMove(unitID, moveOutFromSpaceComp());
        } else {
            System.out.println(unitID + ", is NOT in space comp");
            //TODO építkezni, mozogni, nem visszalépni, figyelni mi merre van hajaj Marci alkoss valamit :D
            WsDirection direction = moveRandomly();
            if (doMove(unitID, direction)) {
                doMove(unitID, direction);
            }
        }
    }

    private boolean doMove(int unitID, WsDirection direction) {
        WsCoordinate simulatedCoordinate = Util.simulateMove(service.builderUnits.get(unitID), direction);
        Step answer = Util.checkMovement(simulatedCoordinate);
        System.out.println(answer);
        switch (answer) {
            case BUILD:
                return service.structureTunnel(unitID, direction);

            case MOVE:
                return service.moveUnit(unitID, direction);

            case WATCH:
                return service.watch(unitID);

            case EXPLODE:
                return service.explode(unitID, direction);

            case STAY:
                return doMove(unitID, moveRandomly());

            case NO_POINTS:
                System.out.println("Elfogytak az elkölthető pontok");
                return false;
        }
        return false;
    }

    private WsDirection moveRandomly() {
        Random random = new Random();
        int r = random.nextInt(5) + 1;
        if (r == 1) {
            return WsDirection.RIGHT;
        } else if (r == 5) {
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
        return service.builderUnits.get(unitID).getCord() == service.initialPos.getCord();
    }


}

