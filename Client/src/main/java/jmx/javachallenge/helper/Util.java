package jmx.javachallenge.helper;

import eu.loxon.centralcontrol.WsCoordinate;
import eu.loxon.centralcontrol.WsDirection;

/**
 * Created by MegaX on 2015. 11. 13..
 */
public class Util {

    public static WsDirection calculateDirection(WsCoordinate source, WsCoordinate target) {
        if (source.getX() < target.getX())
            return WsDirection.RIGHT;
        else if (source.getX() > target.getX())
            return WsDirection.LEFT;
        else if (source.getY() < target.getY())
            return WsDirection.UP;
        else if (source.getY() > target.getY())
            return WsDirection.DOWN;
        else return WsDirection.RIGHT;
    }

    public static void wait(int wait) {
        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
