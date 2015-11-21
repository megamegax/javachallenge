package jmx.javachallenge.service;

import eu.loxon.centralcontrol.*;
import jmx.javachallenge.builder.ExplorerStrategy;
import jmx.javachallenge.builder.JMXBuilder;
import jmx.javachallenge.helper.Tile;
import jmx.javachallenge.helper.TileType;
import jmx.javachallenge.helper.Util;
import jmx.javachallenge.logger.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Stream;

/**
 * Created by MegaX on 2015. 11. 12..
 */
public class Service {
    public static final String MY_TEAM_NAME = "jmx";
    private static Service service = null;

    static {
        java.net.Authenticator.setDefault(new java.net.Authenticator() {
            @Override
            protected java.net.PasswordAuthentication getPasswordAuthentication() {
                return new java.net.PasswordAuthentication("jmx", "XWHD7855".toCharArray());
            }
        });
    }

    public CentralControl api = null;
    public CommonResp serviceState;
    public int actionPointsForTurn = 14;
    public GetSpaceShuttlePosResponse initialPos;
    public HashMap<Integer, JMXBuilder> builderUnits = new HashMap<>();
    public int turnLeft = 51;
    public StartGameResponse initialGameState;
    public JMXBuilder selectedBuilder;
    public GetSpaceShuttleExitPosResponse initialExitPos;
    private ActionCostResponse initialActionCost;
    private GameMap currentMap;

    private Service() {
        Logger.log("service constructor");
        CentralControlServiceService centralControlServiceService = new CentralControlServiceService();
        api = centralControlServiceService.getCentralControlPort();
    }

    public static Service getInstance() {
        if (service == null) {
            service = new Service();
        }
        return service;
    }

    public void init() {
        builderUnits.put(0, new JMXBuilder(0));
        builderUnits.put(1, new JMXBuilder(1));
        builderUnits.put(2, new JMXBuilder(2));
        builderUnits.put(3, new JMXBuilder(3));
        builderUnits.get(0).setCord(initialPos.getCord());
        builderUnits.get(1).setCord(initialPos.getCord());
        builderUnits.get(2).setCord(initialPos.getCord());
        builderUnits.get(3).setCord(initialPos.getCord());

        builderUnits.get(0).setStrategy(new ExplorerStrategy(0));
        builderUnits.get(1).setStrategy(new ExplorerStrategy(1));
        builderUnits.get(2).setStrategy(new ExplorerStrategy(2));
        builderUnits.get(3).setStrategy(new ExplorerStrategy(3));

        /** jociFaktor(); **/
    }

    private void jociFaktor() {
        OptionalDouble average = Stream.iterate(0, e -> e + 1)
                .limit(40)
                .mapToLong(e -> {
                    long before = System.currentTimeMillis();
                    api.startGame(new StartGameRequest());
                    return System.currentTimeMillis() - before;
                })
                .average();
        if (average.isPresent()) {
            Logger.log(average.getAsDouble());
        }
    }

    public StartGameResponse startGame() {
        StartGameResponse res = api.startGame(new StartGameRequest());
        Logger.log("StartGame: " + res.toString());
        initialGameState = res;
        processResult(res.getResult());
        currentMap = new GameMap(res.getSize().getX(), res.getSize().getY());
        return res;
    }

    private void processResult(CommonResp result) {
        // TODO: tároljuk le az infókat ezekből az adatokból, minden dolog után friss infónk legyen mindenről
        serviceState = result;
        actionPointsForTurn = result.getActionPointsLeft();
        selectedBuilder = selectBuilder(result.getBuilderUnit());
        if (turnLeft > result.getTurnsLeft()) {
            this.newTurnFound(50 - turnLeft);
        }
        turnLeft = result.getTurnsLeft();
        Util.printResult(result);
    }

    public boolean isMyTurn() {
        IsMyTurnResponse res = api.isMyTurn(new IsMyTurnRequest());
        processResult(res.getResult());
        Logger.log(res.isIsYourTurn() + " " + res.getResult().getBuilderUnit());
        return res.isIsYourTurn();
    }

    public ActionCostResponse getActionCost() {
        ActionCostResponse res = api.getActionCost(new ActionCostRequest());
        initialActionCost = res;
        Logger.log(initialActionCost.toString());
        processResult(res.getResult());
        return res;
    }

    public GetSpaceShuttlePosResponse getSpaceShuttlePos() {
        if (initialPos == null) {
            GetSpaceShuttlePosResponse res = api.getSpaceShuttlePos(new GetSpaceShuttlePosRequest());
            processResult(res.getResult());
            initialPos = res;

            currentMap.getMapTile(res.getCord().getX(), res.getCord().getY()).setTileType(TileType.SHUTTLE);
            Util.printMap(currentMap);
            return res;
        } else return initialPos;
    }

    public GetSpaceShuttleExitPosResponse getSpaceShuttlePosExit() {
        if (initialExitPos == null) {
            GetSpaceShuttleExitPosResponse res = api.getSpaceShuttleExitPos(new GetSpaceShuttleExitPosRequest());
            processResult(res.getResult());
            initialExitPos = res;
            Logger.log(res.toString());
            return res;
        } else return initialExitPos;
    }

    public boolean watch(int unitID) {
        if (actionPointsForTurn - initialActionCost.getWatch() >= 0) {
            WatchResponse res = api.watch(new WatchRequest(unitID));
            processResult(res.getResult());
            if (res.getResult().getType().equals(ResultType.DONE)) {
                Logger.log(res.toString());
                for (Scouting scout : res.getScout()) {
                    currentMap.getMapTile(scout.getCord().getX(), scout.getCord().getY()).setTileType(Util.stringToCellType(scout.getObject().name(), MY_TEAM_NAME.equalsIgnoreCase(scout.getTeam())));
                }
                builderUnits.get(unitID).setCord(new WsCoordinate(res.getScout().get(0).getCord().getX(), res.getScout().get(2).getCord().getY()));
                if (!builderUnits.get(unitID).getCord().equals(service.initialPos.getCord())) {
                    currentMap.getMapTile(builderUnits.get(unitID).getCord().getX(), builderUnits.get(unitID).getCord().getY()).setBuilder(unitID);
                }
                Util.printMap(currentMap);

                return true;
            } else {
                return false;
            }
        } else
            return false;
    }

    public JMXBuilder selectBuilder(int unitID) {
        return builderUnits.get(unitID);
    }

    public boolean moveUnit(int unitID, WsDirection direction) {
        if (actionPointsForTurn - initialActionCost.getMove() >= 0) {
            MoveBuilderUnitRequest req = new MoveBuilderUnitRequest();
            req.setUnit(unitID);
            req.setDirection(direction);
            MoveBuilderUnitResponse res = api.moveBuilderUnit(req);
            processResult(res.getResult());
            Logger.log("próbálom : mozgatni");
            if (res.getResult().getType().equals(ResultType.DONE)) {
                Logger.log(res);
                WsCoordinate oldCoordinate = builderUnits.get(unitID).getCord();
                currentMap.getMapTile(oldCoordinate.getX(), oldCoordinate.getY()).setBuilder(-1);
                WsCoordinate coordinate = Util.updateCoords(unitID, direction);
                currentMap.getMapTile(coordinate.getX(), coordinate.getY()).setBuilder(unitID);
                Util.printMap(currentMap);

                return true;
            } else {
                return false;
            }
        } else {
            Logger.log("nincs elég pont mozogni");
            return false;
        }

    }

    public boolean radar(int unitID, List<WsCoordinate> coordinates) {
        if (actionPointsForTurn - initialActionCost.getRadar() * coordinates.size() > 0) {
            RadarRequest req = new RadarRequest();
            req.setUnit(unitID);
            req.getCord().addAll(coordinates);
            RadarResponse res = api.radar(req);
            processResult(res.getResult());
            if (res.getResult().getType().equals(ResultType.DONE)) {
                Logger.log(res.toString());
                for (Scouting scout : res.getScout()) {
                    currentMap.getMapTile(scout.getCord().getX(), scout.getCord().getY()).setTileType(Util.stringToCellType(scout.getObject().name(), scout.getTeam().equalsIgnoreCase(MY_TEAM_NAME)));
                }
                Util.printMap(currentMap);
                return true;
            } else {
                return false;
            }
        } else
            return false;
    }

    public boolean structureTunnel(int unitID, WsDirection direction) {
        if (actionPointsForTurn - initialActionCost.getDrill() >= 0) {

            StructureTunnelRequest req = new StructureTunnelRequest();
            req.setUnit(unitID);
            req.setDirection(direction);
            StructureTunnelResponse res = api.structureTunnel(req);
            processResult(res.getResult());
            Logger.log("próbálom : építeni --»" + unitID + "--»»" + direction);
            if (res.getResult().getType().equals(ResultType.DONE)) {
                Logger.log("build - done");
                currentMap.getMapTile(selectBuilder(unitID).getCord().getX(), selectBuilder(unitID).getCord().getY()).setTileType(TileType.TUNNEL);
                Util.printMap(currentMap);
                return true;
            } else {
                return false;
            }
        } else {
            Logger.log(actionPointsForTurn + " - nincs elég pont építeni");
            return false;
        }
    }

    public boolean explode(int unitID, WsDirection direction) {
        Logger.log("EXPLOOOOOODE");
        return false;
    }


    public void newTurnFound(int i) {
        Logger.log("Kör: " + i);
        // builderUnits.get(serviceState.getBuilderUnit()).getCord();
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }
}
