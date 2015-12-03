package hu.jmx.javachallenge.builder;

import eu.loxon.centralcontrol.WsCoordinate;
import hu.jmx.javachallenge.helper.MoveStrategy;
import hu.jmx.javachallenge.helper.Tile;
import hu.jmx.javachallenge.helper.Util;
import hu.jmx.javachallenge.logger.Logger;

import java.util.ArrayList;

/**
 * Created by joci on 11/19/15.
 * Ez a strategy a középpont körül kezd el körkörösen építkezni, ilyen koordinátákat ad vissza a buildernek.
 */
public class DefensiveStrategy implements Strategy {
    //generátor ami mindig az egyre távolabbi szomszédokat adja vissza
    //először az 1 távolságra lévőket, aztán a 2, 3, 4, stb.
    private final JMXBuilder builderUnit;
    private WsCoordinate destination;

    public DefensiveStrategy(JMXBuilder builder, WsCoordinate destination) {
        this.builderUnit = builder;
        this.destination = destination;
    }

    @Override
    public WsCoordinate nextCoordinate() {
        if (service.turnLeft < 10) {
            destination = service.getSpaceShuttleCoord();
        }

        if (builderUnit.getCord().equals(service.getSpaceShuttleCoord())) {
            return service.getSpaceShuttleExitPos();
        } else {
            // Megnézzük, mekkora sugárig van felfedezve a dolog
            ArrayList<WsCoordinate> tilesToVisit = new ArrayList<>();
            for (int radius = 0; radius < service.getCurrentMap().getXSize(); ++radius) {
                for (int x = -radius; x <= radius; ++x) {
                    for (int y = -radius; y <= radius; ++y) {
                        WsCoordinate coordinate = new WsCoordinate(destination.getX() + x, destination.getY() + y);
                        Tile tile = service.getCurrentMap().getMapTile(coordinate);
                        switch (tile.getTileType()) {
                            case UNKNOWN:
                                tilesToVisit.add(coordinate);
                                break;
                            case SHUTTLE:
                                break;
                            case ROCK:
                                tilesToVisit.add(coordinate);
                                break;
                            case OBSIDIAN:
                                break;
                            case TUNNEL:
                                break;
                            case BUILDER:
                                break;
                            case GRANITE:
                                tilesToVisit.add(coordinate);
                                break;
                            case ENEMY_TUNNEL:
                                tilesToVisit.add(coordinate);
                                break;
                            case ENEMY_SHUTTLE:
                                break;
                            case ENEMY_BUILDER:
                                break;
                        }
                    }
                }
                if (tilesToVisit.size() > 0) {
                    ArrayList<WsCoordinate> minPath = null;
                    for (WsCoordinate coordinate : tilesToVisit) {
                        ArrayList<WsCoordinate> path = Util.planRoute(service.getCurrentMap(), builderUnit.getCord(), coordinate, new MoveStrategy() {
                            @Override
                            public boolean canMoveTo(Tile tile) {
                                switch (tile.getTileType()) {
                                    case UNKNOWN:
                                        return true;
                                    case SHUTTLE:
                                        return false;
                                    case ROCK:
                                        return true;
                                    case OBSIDIAN:
                                        return false;
                                    case TUNNEL:
                                        return true;
                                    case BUILDER:
                                        return false;
                                    case GRANITE:
                                        return true;
                                    case ENEMY_TUNNEL:
                                        return true;
                                    case ENEMY_SHUTTLE:
                                        return false;
                                    case ENEMY_BUILDER:
                                        return false;
                                }
                                return false;
                            }

                            @Override
                            public int getDistanceTo(Tile tile) {
                                return Util.getCostOfMoveToTile(tile);
                            }
                        });
                        if (minPath == null || (path != null && minPath.size() > path.size())) {
                            minPath = path;
                        }
                    }
                    if (minPath != null) {
                        Logger.log("Route plan: " + minPath.toString());
                        return minPath.get(0);
                    }
                }
            }
            return builderUnit.getCord();
        }
    }
}
