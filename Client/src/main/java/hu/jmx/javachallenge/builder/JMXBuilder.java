package hu.jmx.javachallenge.builder;

import javax.annotation.Nonnull;
import eu.loxon.centralcontrol.WsBuilderunit;
import eu.loxon.centralcontrol.WsCoordinate;
import eu.loxon.centralcontrol.WsDirection;
import hu.jmx.javachallenge.helper.Step;
import hu.jmx.javachallenge.helper.Tile;
import hu.jmx.javachallenge.helper.TileType;
import hu.jmx.javachallenge.helper.Util;
import hu.jmx.javachallenge.logger.Logger;
import hu.jmx.javachallenge.service.GameMap;
import hu.jmx.javachallenge.service.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by megam on 2015. 11. 18..
 */
public class JMXBuilder extends WsBuilderunit {
    Random random = new Random();
    private Service service;
    private Strategy strategy;

    public JMXBuilder(WsBuilderunit builderUnit) {
        super();
        this.service = Service.getInstance();
        this.unitid = builderUnit.getUnitid();
        this.cord = builderUnit.getCord();
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Egy játékos teljes körét lejátssza, elkölti az összes akciópontját, egyszer hívódik meg körönként, max akcióponttal
     * @param map
     */
    public void step(GameMap map) {
        Logger.log(unitid + "'s building turn");

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

        long startTime = System.currentTimeMillis();
        int previousActionPoints = 10000;
        while (previousActionPoints > service.getRemainingActionPoints()) {
            previousActionPoints = service.getRemainingActionPoints();
            service.watch(unitid);
            WsCoordinate coordinate = strategy.nextCoordinate();
            if (doMove(map, Util.calculateDirection(unitid, coordinate))) {
               // service.builderUnits.get(unitid).strategy.done();
            }
            service.watch(unitid);
        }
        List<WsCoordinate> radarPoints = findRadarPoints(service.getRemainingActionPoints() / service.getActionCosts().getRadar());
        if (radarPoints.size() != 0) {
            service.radar(unitid, radarPoints);
        }
        System.out.println("Turn length: " + (System.currentTimeMillis() - startTime));
    }

    private List<WsCoordinate> findRadarPoints(int points) {
        if (points == 0) {
            return new ArrayList<>();
        }
        ArrayList<WsCoordinate> possibleTiles = new ArrayList<>();
        for (int i = -3; i <= 3; ++i) {
            for (int j = -1; j <= 3; ++j) {
                if (Math.abs(i) <= 1 && j == 0 || Math.abs(j) <= 1 && i == 0) {
                    // Ezt a watch is tudja
                    continue;
                }
                WsCoordinate coordinate = new WsCoordinate(getCord().getX()+i, getCord().getY()+i);
                Tile tile = service.getCurrentMap().getMapTile(coordinate);
                switch (tile.getTileType()) {
                    case UNKNOWN:
                        possibleTiles.add(coordinate);
                        break;
                    case SHUTTLE:
                        break;
                    case ROCK:
                        possibleTiles.add(coordinate);
                        break;
                    case OBSIDIAN:
                        break;
                    case TUNNEL:
                        possibleTiles.add(coordinate);
                        break;
                    case BUILDER:
                        break;
                    case GRANITE:
                        possibleTiles.add(coordinate);
                        break;
                    case ENEMY_TUNNEL:
                        break;
                    case ENEMY_SHUTTLE:
                        break;
                    case ENEMY_BUILDER:
                        possibleTiles.add(coordinate);
                        break;
                }
            }
        }
        Collections.shuffle(possibleTiles);
        if (possibleTiles.size() < points) {
            return possibleTiles;
        } else {
            return possibleTiles.subList(0, points);
        }
    }

    private boolean doMove(GameMap map, WsDirection direction) {
        WsCoordinate simulatedCoordinate = Util.simulateMove(service.builderUnits.get(unitid), direction);
        Step step = Util.checkMovement(map, simulatedCoordinate);
        Logger.log(step);
        switch (step) {
            case BUILD:
                if (service.structureTunnel(unitid, direction)) {
                    return true;
                }
                return false;

            case MOVE:
                if (service.moveUnit(unitid, direction)) {
                    return true;
                }
                return false;

            case WATCH:
                return service.watch(unitid);

            case EXPLODE:
                return service.explode(unitid, direction);

            case STAY:
                //if (doMove(moveRandomly())) {
                // return true;//service.builderUnits.get(unitID).strategy.done();
                //}
              //  strategy.clear();
                Logger.log("STAY: most: " + getCord().toString() + "; " + strategy.nextCoordinate());
                return false;

            case NO_POINTS:
                Logger.log("Elfogytak az elkölthető pontok");
                return false;
        }
        return false;
    }

    private WsDirection moveRandomly() {
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

    @Override
    public void setCord(@Nonnull WsCoordinate coordinate) {
        if (getCord() != null && !getCord().equals(service.getSpaceShuttleCoord())) {
            Tile oldTile = service.getCurrentMap().getMapTile(getCord());
            oldTile.setBuilder(-1);
            oldTile.setTileType(TileType.TUNNEL);
        }
        super.setCord(coordinate);
        if (!getCord().equals(service.getSpaceShuttleCoord())) {
            Tile newTile = service.getCurrentMap().getMapTile(getCord());
            newTile.setBuilder(unitid);
            newTile.setTileType(TileType.BUILDER);
        }
    }
}
