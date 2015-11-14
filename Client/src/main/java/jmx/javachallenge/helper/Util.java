package jmx.javachallenge.helper;

import eu.loxon.centralcontrol.WsBuilderunit;
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

    public static WsCoordinate simulateMove(WsBuilderunit builder, WsDirection direction) {
        switch (direction) {
            case LEFT:
                return new WsCoordinate(builder.getCord().getX() - 1, builder.getCord().getY());
            case RIGHT:
                return new WsCoordinate(builder.getCord().getX() + 1, builder.getCord().getY());
            case UP:
                return new WsCoordinate(builder.getCord().getX(), builder.getCord().getY() + 1);
            case DOWN:
                return new WsCoordinate(builder.getCord().getX(), builder.getCord().getY() - 1);
            default:
                return builder.getCord();
        }
    }

    public static boolean checkMovement(WsCoordinate simulatedCoordinate) {
        if ((Service.getInstance().map[simulatedCoordinate.getX()][simulatedCoordinate.getY()] != 0) &&
                (Service.getInstance().map[simulatedCoordinate.getX()][simulatedCoordinate.getY()] != 4) &&
                (Service.getInstance().map[simulatedCoordinate.getX()][simulatedCoordinate.getY()] != 3)) {
            return true;
        } else return false;
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

    public static void printMap() {
        String sMap = "";
        for (int x = 0; x < Service.getInstance().initialGameState.getSize().getX(); x++) {
            for (int y = 0; y < Service.getInstance().initialGameState.getSize().getY(); y++) {
                sMap += (Service.getInstance().map[x][y] >= 0) ? " " + Service.getInstance().map[x][y] + " " : Service.getInstance().map[x][y] + " ";
            }
            sMap += "\n";
        }
        System.out.println("---------------");
        System.out.println(sMap);
        System.out.println("---------------");
    }
}
