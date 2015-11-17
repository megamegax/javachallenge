package jmx.javachallenge.helper;

import eu.loxon.centralcontrol.WsBuilderunit;
import eu.loxon.centralcontrol.WsCoordinate;
import eu.loxon.centralcontrol.WsDirection;
import jmx.javachallenge.service.Service;

/**
 * Created by MegaX on 2015. 11. 13..
 */
public class Util {
   private static Service service =  Service.getInstance();

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
        Logger.log("from: " + builder.getCord());

        switch (direction) {
            case LEFT:
                Logger.log("to left: " + new WsCoordinate(builder.getCord().getX() - 1, builder.getCord().getY()));
                return new WsCoordinate(builder.getCord().getX() - 1, builder.getCord().getY());
            case RIGHT:
                Logger.log("to right: " + new WsCoordinate(builder.getCord().getX() + 1, builder.getCord().getY()));
                return new WsCoordinate(builder.getCord().getX() + 1, builder.getCord().getY());
            case UP:
                Logger.log("to up: " + new WsCoordinate(builder.getCord().getX(), builder.getCord().getY() + 1));
                return new WsCoordinate(builder.getCord().getX(), builder.getCord().getY() + 1);
            case DOWN:
                Logger.log("to down: " + new WsCoordinate(builder.getCord().getX(), builder.getCord().getY() - 1));
                return new WsCoordinate(builder.getCord().getX(), builder.getCord().getY() - 1);
            default:
                return builder.getCord();
        }
    }

    public static Step checkMovement(WsCoordinate simulatedCoordinate) {
        if (simulatedCoordinate.getX() < service.initialGameState.getSize().getX() && simulatedCoordinate.getY() < service.initialGameState.getSize().getY()) {
            int tileType = service.map[Util.convertCoordinateToMapCoordinate(simulatedCoordinate.getY())][simulatedCoordinate.getX()].getTileType();
            return Step.getStep(tileType);
        } else return Step.STAY;
    }

    public static void wait(int wait) {
        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static WsCoordinate updateCoords(int unitID, WsDirection direction) {

        WsCoordinate oldCoordinate = service.builderUnits.get(unitID).getCord();
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
        Logger.log("coordinates updated for unit: " + unitID + ", " + Service.getInstance().builderUnits.get(unitID).getCord());
        return Service.getInstance().builderUnits.get(unitID).getCord();
    }

    public static TileType stringToCellType(String object) {
        switch (object) {
            case "ROCK":
                return TileType.ROCK;
            case "SHUTTLE":
                return TileType.SHUTTLE;
            case "OBSIDIAN":
                return TileType.OBSIDIAN;
            case "TUNNEL":
                return TileType.TUNNEL;
            case "GRANITE":
                return TileType.GRANITE;
            case "BUILDER_UNIT":
                return TileType.TUNNEL;
            default:
                return TileType.UNKNOWN;
        }
    }

    public static void printMap() {
        int axisY = Service.getInstance().initialGameState.getSize().getY()-1;
        String sMap = "";
        sMap += " " + axisY + "| ";
        for (int y = 0; y < Service.getInstance().initialGameState.getSize().getY(); y++) {
            for (int x = 0; x < Service.getInstance().initialGameState.getSize().getX(); x++) {
                sMap += Service.getInstance().map[y][x].toString();
            }
            axisY--;
            if (y < Service.getInstance().initialGameState.getSize().getY() - 1)
                sMap += "\n " + (axisY <= 9 ? " " + (axisY) : axisY) + "| ";
            else{
                sMap += "\n   -------------------------------------------------------------------------------------------------------------------------";
                sMap += "\n      0     1     2     3     4     5     6     7     8     9    10    11    12    13    14    15    16    17    18    19";
            }
        }
        System.out.println("---------------");
        System.out.println(sMap);
        System.out.println("---------------");
        System.out.println(Service.getInstance().serviceState.getScore().toLog());
    }

    public static int convertCoordinateToMapCoordinate(int y) {
        return Service.getInstance().initialGameState.getSize().getY() - (y + 1);
    }
}
