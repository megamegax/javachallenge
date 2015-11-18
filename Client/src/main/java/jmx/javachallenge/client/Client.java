package jmx.javachallenge.client;

import eu.loxon.centralcontrol.WsCoordinate;
import eu.loxon.centralcontrol.WsDirection;
import jmx.javachallenge.helper.JMXBuilder;
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
        service = Service.getInstance();
        service.startGame();
        service.getSpaceShuttlePos();
        service.getSpaceShuttlePosExit();
        service.getActionCost();
        while (service.turnLeft != 0) {
            Util.wait(301);
            if (service.isMyTurn()) {
                doJob();
            }
        }
    }

    private void doJob() {
        JMXBuilder builder = service.selectedBuilder;
        int unitID = builder.getUnitid();
        //  int unitID = chooseBuilder();
        if (builder.hasOwnWill()) {
            builder.step();
        } else {
            if (unitID == 0 && service.turnLeft == 70) {
                service.watch(unitID);
                service.structureTunnel(unitID, moveOutFromSpaceComp());
                //TODO maradék pontból radarozni?
            } else if (unitID == 1 && service.turnLeft == 70) {
                service.moveUnit(unitID, moveOutFromSpaceComp());
                WsDirection direction = WsDirection.DOWN;
                service.structureTunnel(unitID, direction);
                service.moveUnit(unitID, direction);
                service.watch(unitID);
                builder.setOwnWill(true);
            } else if (unitID == 2 && service.turnLeft == 70) {
                service.moveUnit(unitID, moveOutFromSpaceComp());
                WsDirection direction = WsDirection.UP;
                service.structureTunnel(unitID, direction);
                service.moveUnit(unitID, direction);
                service.watch(unitID);
                builder.setOwnWill(true);
            } else if (unitID == 3 && service.turnLeft == 70) {
                service.moveUnit(unitID, moveOutFromSpaceComp());
                WsDirection direction = WsDirection.RIGHT;
                service.structureTunnel(unitID, direction);
                service.moveUnit(unitID, direction);
                service.watch(unitID);
                builder.setOwnWill(true);
            } else if (unitID == 0 && service.turnLeft == 69) {
                service.moveUnit(unitID, moveOutFromSpaceComp());
                builder.setOwnWill(true);
            }
        }
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

