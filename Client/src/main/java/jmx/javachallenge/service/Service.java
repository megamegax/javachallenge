package jmx.javachallenge.service;

import eu.loxon.centralcontrol.*;
import jmx.javachallenge.builder.ExplorerStrategy;
import jmx.javachallenge.builder.JMXBuilder;
import jmx.javachallenge.helper.TileType;
import jmx.javachallenge.helper.Util;
import jmx.javachallenge.logger.Logger;

import java.util.HashMap;
import java.util.List;

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
    private WsCoordinate spaceShuttleCoord;
    public HashMap<Integer, JMXBuilder> builderUnits = new HashMap<>();
    public int turnLeft = 51;
    public StartGameResponse initialGameState;
    public JMXBuilder currentBuilder;
    private WsCoordinate spaceShuttleExitPos;
    private ActionCostResponse actionCosts;
    private GameMap currentMap;

    private Service() {
        CentralControlServiceService centralControlServiceService = new CentralControlServiceService();
        api = centralControlServiceService.getCentralControlPort();
    }

    public static Service getInstance() {
        if (service == null) {
            service = new Service();
        }
        return service;
    }

    public void setStrategies() {
        builderUnits.get(0).setStrategy(new ExplorerStrategy(0));
        builderUnits.get(1).setStrategy(new ExplorerStrategy(1));
        builderUnits.get(2).setStrategy(new ExplorerStrategy(2));
        builderUnits.get(3).setStrategy(new ExplorerStrategy(3));
    }

    public void startGame() {
        StartGameRequest req = new StartGameRequest();
        StartGameResponse res = api.startGame(req);
        Logger.log("Request sent: " + req.toString());
        Logger.log("Answer: " + res.toString());
        initialGameState = res;
        for (WsBuilderunit builderunit : initialGameState.getUnits()) {
            JMXBuilder jmxBuilder = new JMXBuilder(builderunit);
            builderUnits.put(jmxBuilder.getUnitid(), jmxBuilder);
        }
        processResult(res.getResult());
        currentMap = new GameMap(res.getSize().getX(), res.getSize().getY());
    }

    private void processResult(CommonResp result) {
        // TODO: tároljuk le az infókat ezekből az adatokból, minden dolog után friss infónk legyen mindenről
        serviceState = result;
        actionPointsForTurn = result.getActionPointsLeft();
        currentBuilder = selectBuilder(result.getBuilderUnit());
        if (turnLeft > result.getTurnsLeft()) {
            this.newTurnFound(50 - turnLeft);
        }
        turnLeft = result.getTurnsLeft();
        Util.printResult(result);
    }

    public boolean isMyTurn() {
        IsMyTurnRequest req = new IsMyTurnRequest();
        IsMyTurnResponse res = api.isMyTurn(req);
        Logger.log("Request sent: " + req.toString());
        Logger.log("Answer: " + res.toString());
        processResult(res.getResult());
        return res.isIsYourTurn();
    }

    public void saveActionCost() {
        ActionCostRequest req = new ActionCostRequest();
        ActionCostResponse res = api.getActionCost(req);
        Logger.log("Request sent: " + req.toString());
        Logger.log("Answer: " + res.toString());
        actionCosts = res;
        processResult(res.getResult());
    }

    public void saveSpaceShuttlePos() {
        if (spaceShuttleCoord == null) {
            GetSpaceShuttlePosRequest req = new GetSpaceShuttlePosRequest();
            GetSpaceShuttlePosResponse res = api.getSpaceShuttlePos(req);
            Logger.log("Request sent: " + req.toString());
            Logger.log("Answer: " + res.toString());
            processResult(res.getResult());
            spaceShuttleCoord = res.getCord();

            currentMap.getMapTile(spaceShuttleCoord).setTileType(TileType.SHUTTLE);
            Util.printMap(currentMap);
        }
    }

    public void saveSpaceShuttlePosExit() {
        if (spaceShuttleExitPos == null) {
            GetSpaceShuttleExitPosRequest req = new GetSpaceShuttleExitPosRequest();
            GetSpaceShuttleExitPosResponse res = api.getSpaceShuttleExitPos(req);
            Logger.log("Request sent: " + req.toString());
            Logger.log("Answer: " + res.toString());
            processResult(res.getResult());
            spaceShuttleExitPos = res.getCord();
        }
    }

    public boolean watch(int unitID) {
        if (actionPointsForTurn - actionCosts.getWatch() >= 0) {
            WatchRequest req = new WatchRequest(unitID);
            WatchResponse res = api.watch(req);
            Logger.log("Request sent: " + req.toString());
            Logger.log("Answer: " + res.toString());
            processResult(res.getResult());
            if (res.getResult().getType().equals(ResultType.DONE)) {
                for (Scouting scout : res.getScout()) {
                    currentMap.getMapTile(scout.getCord()).setTileType(Util.stringToCellType(scout.getObject().name(), MY_TEAM_NAME.equalsIgnoreCase(scout.getTeam()), scout.getCord()));
                }
                // Saját koordináta meghatározása a szomszédos mezők koordinátáiból
                builderUnits.get(unitID).setCord(new WsCoordinate(res.getScout().get(0).getCord().getX(), res.getScout().get(2).getCord().getY()));

                Util.printMap(currentMap);

                return true;
            } else {
                return false;
            }
        } else {
            Logger.log("HIBA: Nincs elég pont watcholni");
            return false;
        }
    }

    public JMXBuilder selectBuilder(int unitID) {
        return builderUnits.get(unitID);
    }

    public boolean moveUnit(int unitID, WsDirection direction) {
        if (actionPointsForTurn - actionCosts.getMove() >= 0) {
            MoveBuilderUnitRequest req = new MoveBuilderUnitRequest();
            req.setUnit(unitID);
            req.setDirection(direction);
            MoveBuilderUnitResponse res = api.moveBuilderUnit(req);
            Logger.log("Request sent: " + req.toString());
            Logger.log("Answer: " + res.toString());
            processResult(res.getResult());
            if (res.getResult().getType().equals(ResultType.DONE)) {
                JMXBuilder builder = builderUnits.get(unitID);
                WsCoordinate oldCoordinate = builder.getCord();
                WsCoordinate newCoordinate = Util.coordinateInDirection(oldCoordinate, direction);
                builder.setCord(newCoordinate);
                Util.printMap(currentMap);
                return true;
            } else {
                return false;
            }
        } else {
            Logger.log("HIBA: Nincs elég pont mozogni");
            return false;
        }

    }

    public boolean radar(int unitID, List<WsCoordinate> coordinates) {
        if (actionPointsForTurn - actionCosts.getRadar() * coordinates.size() > 0) {
            RadarRequest req = new RadarRequest();
            req.setUnit(unitID);
            req.getCord().addAll(coordinates);
            RadarResponse res = api.radar(req);
            Logger.log("Request sent: " + req.toString());
            Logger.log("Answer: " + res.toString());
            processResult(res.getResult());
            if (res.getResult().getType().equals(ResultType.DONE)) {
                for (Scouting scout : res.getScout()) {
                    currentMap.getMapTile(scout.getCord()).setTileType(Util.stringToCellType(scout.getObject().name(), MY_TEAM_NAME.equalsIgnoreCase(scout.getTeam()), scout.getCord()));
                }
                Util.printMap(currentMap);
                return true;
            } else {
                return false;
            }
        } else {
            Logger.log("HIBA: Nincs elég pont radarozni");
            return false;
        }
    }

    public boolean structureTunnel(int unitID, WsDirection direction) {
        if (actionPointsForTurn - actionCosts.getDrill() >= 0) {

            StructureTunnelRequest req = new StructureTunnelRequest();
            req.setUnit(unitID);
            req.setDirection(direction);
            StructureTunnelResponse res = api.structureTunnel(req);
            Logger.log("Request sent: " + req.toString());
            Logger.log("Answer: " + res.toString());
            processResult(res.getResult());
            if (res.getResult().getType().equals(ResultType.DONE)) {
                currentMap.getMapTile(Util.coordinateInDirection(selectBuilder(unitID).getCord(), direction)).setTileType(TileType.TUNNEL);
                Util.printMap(currentMap);
                return true;
            } else {
                Logger.log("HIBA: Nem sikerült alagutat építeni");
                return false;
            }
        } else {
            Logger.log("HIBA: Nincs elég pont építeni");
            return false;
        }
    }

    public boolean explode(int unitID, WsDirection direction) {
        Logger.log("EXPLOOOOOODE");
        return false;
    }


    public void newTurnFound(int i) {
        Logger.log("ÚJ KÖR: " + i);
        // builderUnits.get(serviceState.getBuilderUnit()).getCord();
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }

    public WsCoordinate getSpaceShuttleCoord() {
        return spaceShuttleCoord;
    }

    public ActionCostResponse getActionCosts() {
        return actionCosts;
    }

    public WsCoordinate getSpaceShuttleExitPos() {
        return spaceShuttleExitPos;
    }
}
