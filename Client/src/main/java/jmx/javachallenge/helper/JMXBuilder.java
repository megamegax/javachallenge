package jmx.javachallenge.helper;

import eu.loxon.centralcontrol.WsBuilderunit;
import eu.loxon.centralcontrol.WsCoordinate;
import eu.loxon.centralcontrol.WsDirection;
import jmx.javachallenge.logger.Logger;
import jmx.javachallenge.service.Service;

import java.util.Random;

/**
 * Created by megam on 2015. 11. 18..
 */
public class JMXBuilder extends WsBuilderunit {
    private boolean ownWill = false;
    private Service service;

    public JMXBuilder() {
        super();
        this.service = Service.getInstance();
    }

    public boolean hasOwnWill() {
        return ownWill;
    }

    public void setOwnWill(boolean will) {
        this.ownWill = will;
    }

    public void step() {
        Logger.log(unitid + ", has own will");
        //TODO építkezni, mozogni, nem visszalépni, figyelni mi merre van hajaj Marci alkoss valamit :D
        WsDirection direction = moveRandomly();
        service.watch(unitid);
        if (doMove(unitid, direction)) {
            doMove(unitid, direction);
        }
    }

    private boolean doMove(int unitID, WsDirection direction) {
        WsCoordinate simulatedCoordinate = Util.simulateMove(service.builderUnits.get(unitID), direction);
        Step step = Util.checkMovement(simulatedCoordinate);
        Logger.log(step);
        switch (step) {
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
