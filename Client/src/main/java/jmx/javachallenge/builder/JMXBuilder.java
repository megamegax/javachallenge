package jmx.javachallenge.builder;

import eu.loxon.centralcontrol.WsBuilderunit;
import eu.loxon.centralcontrol.WsCoordinate;
import eu.loxon.centralcontrol.WsDirection;
import jmx.javachallenge.helper.Step;
import jmx.javachallenge.helper.Util;
import jmx.javachallenge.logger.Logger;
import jmx.javachallenge.service.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by megam on 2015. 11. 18..
 */
public class JMXBuilder extends WsBuilderunit {
    private Service service;
    private Strategy strategy;
    private Set<WsCoordinate> tabooCoordinates;

    public JMXBuilder(int unitid, Strategy strategy) {
        super();
        this.service = Service.getInstance();
        this.strategy = strategy;
        this.unitid = unitid;
        tabooCoordinates = new HashSet<>();
    }

    public void step() {
        Logger.log(unitid + ", has own will");

        //TODO lekérni a strategy objektumtól a következő mezőt és a watch alapján eldönteni h mit akarunk
        //TODO már bejárt koordináták megjegyzése h ne lépjünk vissza rájuk
        //TODO megnézni, h a tabuhalmaz miatt nem ragadnak-e be a builderek
        //TODO ha a radarozás kiderítené h a kövi koordináta obszidián, felvenni a tabu listába és kikerülni
        //amíg el nem érjük a kövi célt, vagy ki nem derül róla, h obszidián, addig közelítünk hozzá
        //különben új koordinátát kérünk
        //az ExplorerStrategy null-t visszaadhat kövi koordinátaként,
        //ez azt jelenti, hogy stratégiát kell a buildernek váltania
        //(abban a távoli pontban indítana egy defenzív algoritmust?)

        //a Client.javaból át lehet emelni a logikát, ami eldönti, hogy lépünk, fúrunk, vagy mit csinálunk


        // WsDirection direction = moveRandomly();
        service.watch(unitid);
        WsCoordinate newCoordinate = strategy.nextCoordinate();
        if (doMove(unitid, Util.calculateDirection(unitid, newCoordinate))) {

        }
        // if (doMove(unitid, direction)) {
        //     doMove(unitid, direction);
        // }
    }

    private boolean doMove(int unitID, WsDirection direction) {
        WsCoordinate simulatedCoordinate = Util.simulateMove(service.builderUnits.get(unitID), direction);
        Step step = Util.checkMovement(simulatedCoordinate);
        Logger.log(step);
        switch (step) {
            case BUILD:
                if (service.structureTunnel(unitID, direction)) {
                    return service.builderUnits.get(unitID).strategy.done();
                }
                return false;

            case MOVE:
                if (service.moveUnit(unitID, direction)) {
                    return service.builderUnits.get(unitID).strategy.done();
                }
                return false;

            case WATCH:
                return service.watch(unitID);

            case EXPLODE:
                return service.explode(unitID, direction);

            case STAY:
                if (doMove(unitID, moveRandomly())) {
                    return true;//service.builderUnits.get(unitID).strategy.done();
                }
                return false;

            case NO_POINTS:
                Logger.log("Elfogytak az elkölthető pontok");
                return false;
        }
        return false;
    }

    private WsDirection moveRandomly() {
        Random random = new Random();
        int r = random.nextInt(6) + 1;
        if (r == 1) {
            return WsDirection.RIGHT;
        } else if (r == 5) {
            return WsDirection.RIGHT;
        } else if (r == 2) {
            return WsDirection.LEFT;
        } else if (r == 3) {
            return WsDirection.UP;
        } else if (r == 6) {
            return WsDirection.DOWN;
        } else return WsDirection.DOWN;
    }

}
