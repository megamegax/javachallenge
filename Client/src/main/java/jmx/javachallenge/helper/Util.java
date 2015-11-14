package jmx.javachallenge.helper;

import eu.loxon.centralcontrol.WsCoordinate;
import eu.loxon.centralcontrol.WsDirection;
import jmx.javachallenge.service.Service;

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

    public static void updateCoords(int unitID, WsDirection direction) {
        WsCoordinate oldCoordinate = Service.getInstance().builderUnits.get(unitID).getCord();
        switch (direction) {
            case LEFT:
                Service.getInstance().builderUnits.get(unitID).setCord(new WsCoordinate(oldCoordinate.getX() - 1, oldCoordinate.getY()));
                break;
            case RIGHT:
                Service.getInstance().builderUnits.get(unitID).setCord(new WsCoordinate(oldCoordinate.getX() + 1, oldCoordinate.getY()));
                break;
            case UP:
                Service.getInstance().builderUnits.get(unitID).setCord(new WsCoordinate(oldCoordinate.getX(), oldCoordinate.getY() + 1));
                break;
            case DOWN:
                Service.getInstance().builderUnits.get(unitID).setCord(new WsCoordinate(oldCoordinate.getX(), oldCoordinate.getY() - 1));
                break;
        }
    }

    public static CellType stringToCellType(String object) {
        switch (object) {
            case "ROCK":
                return CellType.ROCK;
            case "SHUTTLE":
                return CellType.SHUTTLE;
            case "OBSIDIAN":
                return CellType.OBSIDIAN;
            case "TUNNEL":
                return CellType.TUNNEL;
            case "GRANITE":
                return CellType.GRANITE;
            default:
                return CellType.UNKNOWN;
        }
    }
}
