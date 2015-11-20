package jmx.javachallenge.helper;

import eu.loxon.centralcontrol.CommonResp;
import eu.loxon.centralcontrol.WsBuilderunit;
import eu.loxon.centralcontrol.WsCoordinate;
import eu.loxon.centralcontrol.WsDirection;
import jmx.javachallenge.logger.Logger;
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

    public static WsDirection calculateDirection(int unitID, WsCoordinate target) {
        WsCoordinate source = service.builderUnits.get(unitID).getCord();
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
            int tileType = service.getMapTile(simulatedCoordinate.getX(), simulatedCoordinate.getY()).getTileTypeIndex();
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
        System.out.print("    ");
        for (int x = 0; x < Service.getInstance().initialGameState.getSize().getX(); x++) {
            System.out.print(x%10 + " ");
        }
        System.out.println();
        System.out.println("    -----------------------------------------");
        for (int y = Service.getInstance().initialGameState.getSize().getY()-1; y >= 0; y--) {
            String rowString = "";
            System.out.print((y <= 9 ? " " + (y) : y) + "| ");
            for (int x = 0; x < Service.getInstance().initialGameState.getSize().getX(); x++) {
                Tile tile = Service.getInstance().getMapTile(x, y);
                if (tile.getUnitId() >= 0) {
                    rowString += Integer.toString(tile.getUnitId()) + " ";
                } else {
                    rowString += getTileTypeChar(tile.getTileType()) + " ";
                }
            }
            System.out.println(rowString);
        }
        System.out.println("    -----------------------------------------");
        System.out.print("    ");
        for (int x = 0; x < Service.getInstance().initialGameState.getSize().getX(); x++) {
            System.out.print(x%10 + " ");
        }
        System.out.println();
        System.out.println(Service.getInstance().serviceState.getScore().toLog());
    }

    private static String getTileTypeChar(TileType tileType) {
        switch (tileType) {
            case UNKNOWN:
                return " ";
            case ROCK:
                return ".";
            case GRANITE:
                return ";";
            case OBSIDIAN:
                return "#";
            case SHUTTLE:
                return "@";
            case ENEMY_SHUTTLE:
                return "!";
            case TUNNEL:
                return "+";
            case ENEMY_TUNNEL:
                return "-";
            case BUILDER:
                return "5";
            case ENEMY_BUILDER:
                return "B";
        }
        return "?";
    }

    public static int convertCoordinateToMapCoordinate(int y) {
        return Service.getInstance().initialGameState.getSize().getY() - (y + 1);
    }

    public static void printResult(CommonResp result) {
        System.out.println(result.toString());
    }
}
