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
        System.out.println("from: " + builder.getCord());

        switch (direction) {
            case LEFT:
                System.out.println("to: " + new WsCoordinate(builder.getCord().getX() - 1, builder.getCord().getY()));
                return new WsCoordinate(builder.getCord().getX() - 1, builder.getCord().getY());
            case RIGHT:
                System.out.println("to: " + new WsCoordinate(builder.getCord().getX() - 1, builder.getCord().getY()));
                return new WsCoordinate(builder.getCord().getX() + 1, builder.getCord().getY());
            case UP:
                System.out.println("to: " + new WsCoordinate(builder.getCord().getX() - 1, builder.getCord().getY()));
                return new WsCoordinate(builder.getCord().getX(), builder.getCord().getY() + 1);
            case DOWN:
                System.out.println("to: " + new WsCoordinate(builder.getCord().getX() - 1, builder.getCord().getY()));
                return new WsCoordinate(builder.getCord().getX(), builder.getCord().getY() - 1);
            default:
                return builder.getCord();
        }
    }

    public static Step checkMovement(WsCoordinate simulatedCoordinate) {
        if (simulatedCoordinate.getX() < Service.getInstance().initialGameState.getSize().getX() && simulatedCoordinate.getY() < Service.getInstance().initialGameState.getSize().getY()) {
            switch (Service.getInstance().map[simulatedCoordinate.getX()][simulatedCoordinate.getY()]) {
                case -1:
                    return Step.WATCH;
                case 0:
                    return Step.STAY;
                case 1:
                    return Step.BUILD;
                case 2:
                    return Step.STAY;
                case 3:
                    return Step.MOVE;
                case 4:
                    return Step.STAY;
                case 5:
                    return Step.EXPLODE;
                default:
                    return Step.STAY;
            }
        } else return Step.STAY;
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
        String sMap ="      1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19\n";
        sMap += "  1: ";
        for (int y = 0; y < Service.getInstance().initialGameState.getSize().getY(); y++) {
            for (int x = 0; x < Service.getInstance().initialGameState.getSize().getX(); x++) {
                sMap += (Service.getInstance().map[y][x] >= 0) ? " " + Service.getInstance().map[y][x] + " " : Service.getInstance().map[y][x] + " ";
            }
            if (y < Service.getInstance().initialGameState.getSize().getY() - 1)
                sMap += "\n " + (y < 8 ? " " + (y + 2) : y + 2) + ": ";
        }
        System.out.println("---------------");
        System.out.println(sMap);
        System.out.println("---------------");
    }

    public static int convertCoordinateToMapCoordinate(int y) {
        return y;// Service.getInstance().initialGameState.getSize().getY() - (y);
    }
}
