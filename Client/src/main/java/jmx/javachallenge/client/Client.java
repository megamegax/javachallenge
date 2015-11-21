package jmx.javachallenge.client;

import eu.loxon.centralcontrol.WsCoordinate;
import eu.loxon.centralcontrol.WsDirection;
import jmx.javachallenge.builder.JMXBuilder;
import jmx.javachallenge.helper.Util;
import jmx.javachallenge.logger.LogLevels;
import jmx.javachallenge.logger.Logger;
import jmx.javachallenge.service.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marton on 2015. 11. 12..
 */
public class Client {
    private Service service;

    public void run() {
        System.out.println((char) 27 + "[30mHello JMX");
        Logger.init(new LogLevels[]{LogLevels.DEBUG,LogLevels.MAP});
//        Logger.init(new LogLevels[]{});
        service = Service.getInstance();

        service.startGame();
        service.saveSpaceShuttlePos();
        service.getSpaceShuttlePosExit();
        service.saveActionCost();
        service.setStrategies();

        int i = 0;
        while (service.turnLeft != 0) {
            i++;
            Logger.log(i);
            Util.wait(300);
            if (service.isMyTurn()) {
                //TODO move logic to strategy objects
                JMXBuilder builder = service.selectedBuilder;
                int unitID = builder.getUnitid();
                service.watch(unitID);
                builder.step(service.getCurrentMap());
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
        return Util.calculateDirection(service.getSpaceShuttleCoord(), service.getSpaceShuttlePosExit().getCord());
    }



}

