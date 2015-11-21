package jmx.javachallenge.helper;

import com.sun.istack.internal.Nullable;
import eu.loxon.centralcontrol.CommonResp;
import eu.loxon.centralcontrol.WsBuilderunit;
import eu.loxon.centralcontrol.WsCoordinate;
import eu.loxon.centralcontrol.WsDirection;
import jmx.javachallenge.logger.Logger;
import jmx.javachallenge.service.GameMap;
import jmx.javachallenge.service.Service;

import java.util.*;

/**
 * Created by MegaX on 2015. 11. 13..
 */
public class Util {
    private static final int INFINITY = 1234567890;
    private static Service service = Service.getInstance();

    @Nullable
    public static WsDirection calculateDirection(WsCoordinate source, WsCoordinate target) {
        if (source.getX() < target.getX())
            return WsDirection.RIGHT;
        else if (source.getX() > target.getX())
            return WsDirection.LEFT;
        else if (source.getY() < target.getY())
            return WsDirection.UP;
        else if (source.getY() > target.getY())
            return WsDirection.DOWN;
        else return null;
    }

    @Nullable
    public static WsDirection calculateDirection(int unitID, WsCoordinate target) {
        WsCoordinate source = service.builderUnits.get(unitID).getCord();
        return calculateDirection(source, target);
    }

    public static WsCoordinate simulateMove(WsBuilderunit builder, @Nullable WsDirection direction) {
        if (direction == null) {
            return builder.getCord();
        }
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

    public static Step checkMovement(GameMap map, WsCoordinate simulatedCoordinate) {
        if (simulatedCoordinate.getX() < service.initialGameState.getSize().getX() && simulatedCoordinate.getY() < service.initialGameState.getSize().getY()) {
            int tileType = map.getMapTile(simulatedCoordinate).getTileTypeIndex();
            Logger.log("Lépés ellenőrzés:::::::" + tileType);
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

    public static WsCoordinate coordinateInDirection(WsCoordinate oldCoordinate, @Nullable WsDirection direction) {
        if (direction == null) {
            return oldCoordinate;
        }
        switch (direction) {
            case LEFT:
                return new WsCoordinate(oldCoordinate.getX() - 1, oldCoordinate.getY());
            case RIGHT:
                return new WsCoordinate(oldCoordinate.getX() + 1, oldCoordinate.getY());
            case UP:
                return new WsCoordinate(oldCoordinate.getX(), oldCoordinate.getY() + 1);
            case DOWN:
                return new WsCoordinate(oldCoordinate.getX(), oldCoordinate.getY() - 1);
        }
        return oldCoordinate;
    }

    public static TileType stringToCellType(String object, boolean myTeam) {
        switch (object) {
            case "ROCK":
                return TileType.ROCK;
            case "SHUTTLE":
                if (myTeam) { // FIXME: Rossz értéket kapunk
                    return TileType.SHUTTLE;
                } else {
                    return TileType.ENEMY_SHUTTLE;
                }
            case "OBSIDIAN":
                return TileType.OBSIDIAN;
            case "TUNNEL":
                if (myTeam) {
                    return TileType.TUNNEL;
                } else {
                    return TileType.ENEMY_TUNNEL;
                }
            case "GRANITE":
                return TileType.GRANITE;
            case "BUILDER_UNIT":
                if (myTeam) {
                    return TileType.BUILDER;
                } else {
                    return TileType.ENEMY_BUILDER;
                }
            default:
                return TileType.UNKNOWN;
        }
    }

    public static void printMap(GameMap map) {
        System.out.print("    ");
        for (int x = 0; x < map.getXSize(); x++) {
            System.out.print((char) 27 + "[30m" + x % 10 + " ");
        }
        System.out.println();
        System.out.println((char) 27 + "[30m    -----------------------------------------");
        for (int y = map.getYSize() - 1; y >= 0; y--) {
            String rowString = "";
            System.out.print((char) 27 + "[30m" + (y <= 9 ? " " + (y) : y) + "| ");
            for (int x = 0; x < map.getXSize(); x++) {
                Tile tile = map.getMapTile(x, y);
                if (tile.getUnitId() >= 0) {
                    rowString += (char) 27 + "[32m" + Integer.toString(tile.getUnitId()) + " ";
                } else {
                    rowString += (char) 27 + "[30m" + getTileTypeChar(tile.getTileType()) + " ";
                }
            }
            System.out.println((char) 27 + "[30m" + rowString);
        }
        System.out.println((char) 27 + "[30m    -----------------------------------------");
        System.out.print((char) 27 + "[30m    ");
        for (int x = 0; x < map.getXSize(); x++) {
            System.out.print(x % 10 + " ");
        }
        System.out.println();
    }

    private static String getTileTypeChar(TileType tileType) {
        switch (tileType) {
            case UNKNOWN:
                return (char) 27 + "[30m ";
            case ROCK:
                return (char) 27 + "[30m▪";
            case GRANITE:
                return (char) 27 + "[30m●";
            case OBSIDIAN:
                return (char) 27 + "[30m#";
            case SHUTTLE:
                return (char) 27 + "[32m@";
            case ENEMY_SHUTTLE:
                return (char) 27 + "[31m!";
            case TUNNEL:
                return (char) 27 + "[32m+";
            case ENEMY_TUNNEL:
                return (char) 27 + "[31m-";
            case BUILDER:
                return (char) 27 + "[32m5";
            case ENEMY_BUILDER:
                return (char) 27 + "[31mB";
        }
        return (char) 27 + "[30m?";
    }

    public static void printResult(CommonResp result) {
        System.out.println(result.toString());
    }

    /**
     * Útovnaltervezés (A* algoritmus alapján) a startCoord-ból indulva az endCoord felé a map térképen, moveStrategy stratégiával
     * @param map A térkép, amin útvonalat keresünk
     * @param startCoord A koordináta, ahonnan indulva keressük az útvonalat
     * @param endCoord A cél koordináta
     * @param moveStrategy Megmondja, hogy melyik mezőkre léphetünk, illetve mennyibe kerül egy lépés
     * @return Az útvonalak koordinátájának listáját, amely tartalmazza startCoord-ot és endCoord-ot is, ha létezik ilyen útvonal;
     *      null, ha nem létezik útvonal
     */
    @Nullable
    public static ArrayList<WsCoordinate> planRoute(GameMap map, WsCoordinate startCoord, WsCoordinate endCoord, MoveStrategy moveStrategy) {
        Set<WsCoordinate> closedSet = new HashSet<>();
        Set<WsCoordinate> openSet = new HashSet<>();
        openSet.add(startCoord);
        WsCoordinate[][] cameFrom = new WsCoordinate[map.getXSize()][map.getYSize()];
        int[][] gScore = new int[map.getXSize()][map.getYSize()];
        int[][] fScore = new int[map.getXSize()][map.getYSize()];

        for (int x = 0; x < map.getXSize(); ++x) {
            for (int y = 0; y < map.getYSize(); ++y) {
                fScore[x][y] = INFINITY;
                gScore[x][y] = INFINITY;
            }
        }

        gScore[startCoord.getX()][startCoord.getY()] = 0;
        fScore[startCoord.getX()][startCoord.getY()] = distanceHeuristics(startCoord, endCoord);
        cameFrom[startCoord.getX()][startCoord.getY()] = null;

        while (!openSet.isEmpty()) {
            Iterator<WsCoordinate> openSetIterator = openSet.iterator();
            WsCoordinate minCoord = openSetIterator.next();
            while (openSetIterator.hasNext()) {
                WsCoordinate coord = openSetIterator.next();
                if (fScore[coord.getX()][coord.getY()] < fScore[minCoord.getX()][minCoord.getY()]) {
                    minCoord = coord;
                }
            }
            openSet.remove(minCoord);
            if (minCoord.equals(endCoord)) {
                // Beértünk a célba
                WsCoordinate actualCoord = minCoord;
                ArrayList<WsCoordinate> path = new ArrayList<>();
                while (cameFrom[actualCoord.getX()][actualCoord.getY()] != null) {
                    path.add(actualCoord);
                    actualCoord = cameFrom[actualCoord.getX()][actualCoord.getY()];
                }
                Collections.reverse(path);
                return path;
            } else {
                closedSet.add(minCoord);
                ArrayList<WsCoordinate> neighbours = getNeighbours(map, minCoord, moveStrategy);
                for (WsCoordinate neighbour : neighbours) {
                    if (closedSet.contains(neighbour)) {
                        continue;
                    }
                    int newGScore = gScore[minCoord.getX()][minCoord.getY()] + moveStrategy.getDistanceTo(map.getMapTile(neighbour));
                    if (!openSet.contains(neighbour)) {
                        openSet.add(neighbour);
                    } else if (newGScore >= gScore[neighbour.getX()][neighbour.getY()] + distanceHeuristics(neighbour, endCoord)) {
                        continue;
                    }
                    cameFrom[neighbour.getX()][neighbour.getY()] = minCoord;
                    gScore[neighbour.getX()][neighbour.getY()] = newGScore;
                    fScore[neighbour.getX()][neighbour.getY()] = gScore[neighbour.getX()][neighbour.getY()] + distanceHeuristics(neighbour, endCoord);
                }
            }
        }

        return null;
    }

    private static ArrayList<WsCoordinate> getNeighbours(GameMap map, WsCoordinate coord, MoveStrategy moveStrategy) {
        ArrayList<WsCoordinate> neighbours = new ArrayList<>();
        WsCoordinate newCoord = new WsCoordinate(coord.getX()-1, coord.getY());
        if (moveStrategy.canMoveTo(map.getMapTile(newCoord))) {
            neighbours.add(newCoord);
        }
        newCoord = new WsCoordinate(coord.getX()+1, coord.getY());
        if (moveStrategy.canMoveTo(map.getMapTile(newCoord))) {
            neighbours.add(newCoord);
        }
        newCoord = new WsCoordinate(coord.getX(), coord.getY()-1);
        if (moveStrategy.canMoveTo(map.getMapTile(newCoord))) {
            neighbours.add(newCoord);
        }
        newCoord = new WsCoordinate(coord.getX(), coord.getY()+1);
        if (moveStrategy.canMoveTo(map.getMapTile(newCoord))) {
            neighbours.add(newCoord);
        }
        return neighbours;
    }

    private static int distanceHeuristics(WsCoordinate startCoord, WsCoordinate endCoord) {
        return Math.abs(startCoord.getX() - endCoord.getX()) + Math.abs(startCoord.getY() - endCoord.getY());
    }
}
