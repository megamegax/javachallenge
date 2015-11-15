package jmx.javachallenge.service;

import eu.loxon.centralcontrol.*;
import jmx.javachallenge.helper.CellType;
import jmx.javachallenge.helper.Tile;
import jmx.javachallenge.helper.Step;
import jmx.javachallenge.helper.Util;

import java.util.HashMap;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Stream;

/**
 * Created by MegaX on 2015. 11. 12..
 */
public class Service {
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
    public HashMap<Integer, WsBuilderunit> builderUnits = new HashMap<>();
    public Tile[][] map; // -1:unknown;0:shuttle;1:rock;2:obsidian;

    public int turnLeft = 71;
    private GetSpaceShuttleExitPosResponse initialExitPos;
    private ActionCostResponse initialActionCost;
    public StartGameResponse initialGameState;
    public int selectedBuilder;

    private Service() {
        CentralControlServiceService service = new CentralControlServiceService();
        api = service.getCentralControlPort();
        builderUnits.put(0, new WsBuilderunit());
        builderUnits.put(1, new WsBuilderunit());
        builderUnits.put(2, new WsBuilderunit());
        builderUnits.put(3, new WsBuilderunit());

        /** jociFaktor(); **/
        Service.service = this;
    }

    public static Service getInstance() {
        if (service == null) {
            return new Service();
        }
        return service;
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
            System.out.println(average.getAsDouble());
        }
    }

    public StartGameResponse startGame() {
        StartGameResponse res = api.startGame(new StartGameRequest());
        initialGameState = res;
        System.out.println(res.toString());
        map = new Tile[res.getSize().getY()][res.getSize().getX()];
        for (int y = 0; y < initialGameState.getSize().getY(); y++) {
            for (int x = 0; x < initialGameState.getSize().getX(); x++) {
                map[y][x] = new Tile();
            }
        }
        return res;
    }

    public boolean isMyTurn() {
        IsMyTurnResponse res = api.isMyTurn(new IsMyTurnRequest());
        serviceState = res.getResult();

        if (res.isIsYourTurn()) {
            selectedBuilder = res.getResult().getBuilderUnit();
            actionPointsForTurn = res.getResult().getActionPointsLeft();
            if (turnLeft > res.getResult().getTurnsLeft()) {
                turnLeft = res.getResult().getTurnsLeft();
                this.startTurn(70 - turnLeft);
            }
            return true;
        }
        //     printMessage(res.getResult());
        return false;
    }

    public ActionCostResponse getActionCost() {
        ActionCostResponse res = api.getActionCost(new ActionCostRequest());
        initialActionCost = res;
        printMessage(res.getResult());
        serviceState = res.getResult();
        System.out.println(res.toString());

        return res;
    }

    public GetSpaceShuttlePosResponse getSpaceShuttlePos() {
        if (initialPos == null) {
            GetSpaceShuttlePosResponse res = api.getSpaceShuttlePos(new GetSpaceShuttlePosRequest());
            printMessage(res.getResult());
            initialPos = res;
            builderUnits.get(0).setCord(res.getCord());
            builderUnits.get(1).setCord(res.getCord());
            builderUnits.get(2).setCord(res.getCord());
            builderUnits.get(3).setCord(res.getCord());
            serviceState = res.getResult();
            map[Util.convertCoordinateToMapCoordinate(res.getCord().getY())][res.getCord().getX()].setCellType(CellType.SHUTTLE);
            Util.printMap();
            return res;
        } else return initialPos;
    }

    public void printMessage(CommonResp res) {
        actionPointsForTurn = res.getActionPointsLeft();
    }

    public GetSpaceShuttleExitPosResponse getSpaceShuttlePosExit() {
        if (initialExitPos == null) {
            GetSpaceShuttleExitPosResponse res = api.getSpaceShuttleExitPos(new GetSpaceShuttleExitPosRequest());
            printMessage(res.getResult());
            initialExitPos = res;
            System.out.println(res.toString());
            return res;
        } else return initialExitPos;
    }

    public boolean watch(int unitID) {
        int tempPoints = actionPointsForTurn;
        tempPoints -= initialActionCost.getWatch();
        if (tempPoints > 0) {
            WatchResponse res = api.watch(new WatchRequest(unitID));
            Util.wait(10);
            if (res.getResult().getType().equals(ResultType.DONE)) {
                for (Scouting scout : res.getScout()) {
                    map[Util.convertCoordinateToMapCoordinate(scout.getCord().getY())][scout.getCord().getX()].setCellType(Util.stringToCellType(scout.getObject().name()));
                }
                System.out.println(res.getScout().get(0).getCord());
                System.out.println(res.getScout().get(1).getCord());
                System.out.println(res.getScout().get(2).getCord());
                System.out.println(res.getScout().get(3).getCord());
                builderUnits.get(unitID).setCord(new WsCoordinate(res.getScout().get(0).getCord().getX(), res.getScout().get(2).getCord().getY()));
                Util.printMap();
                actionPointsForTurn = tempPoints;
                return true;
            } else {
                System.out.println(res.getResult());

                return false;
            }
        } else
            return false;
    }

    public WsBuilderunit selectBuilder(int unitID) {
        return builderUnits.get(unitID);
    }

    public boolean moveUnit(int unitID, WsDirection direction) {
        int tempPoints = actionPointsForTurn;
        tempPoints -= initialActionCost.getMove();
        if (tempPoints > 0) {
            MoveBuilderUnitRequest req = new MoveBuilderUnitRequest();
            req.setUnit(unitID);
            req.setDirection(direction);
            MoveBuilderUnitResponse res = api.moveBuilderUnit(req);
            Util.wait(10);
            System.out.println(res);
            if (res.getResult().getType().equals(ResultType.DONE)) {
                serviceState = res.getResult();
                WsCoordinate oldCoordinate = builderUnits.get(unitID).getCord();
                map[Util.convertCoordinateToMapCoordinate(oldCoordinate.getY())][oldCoordinate.getX()].setBuilder(-1);
                WsCoordinate coordinate = Util.updateCoords(unitID, direction);
                map[Util.convertCoordinateToMapCoordinate(coordinate.getY())][coordinate.getX()].setBuilder(unitID);
                Util.printMap();

                actionPointsForTurn = tempPoints;
                return true;
            } else {
                System.out.println(res.getResult());
                return false;
            }
        } else {
            System.out.println("nincs elég pont mozogni");
            return false;
        }

    }

    public boolean radar(int unitID, List<WsCoordinate> coordinates) {
        int tempPoints = actionPointsForTurn;
        tempPoints -= initialActionCost.getRadar() * coordinates.size();
        if (tempPoints > 0) {
            RadarRequest req = new RadarRequest();
            req.setUnit(unitID);
            req.getCord().addAll(coordinates);
            RadarResponse res = api.radar(req);
            if (res.getResult().getType().equals(ResultType.DONE)) {
                System.out.println(res.toString());
                for (Scouting scout : res.getScout()) {
                    map[Util.convertCoordinateToMapCoordinate(scout.getCord().getY())][scout.getCord().getX()].setCellType(Util.stringToCellType(scout.getObject().name()));
                }
                Util.printMap();
                actionPointsForTurn = tempPoints;
                serviceState = res.getResult();
                return true;
            } else {
                System.out.println(res.getResult());

                return false;
            }
        } else
            return false;
    }

    public boolean structureTunnel(int unitID, WsDirection direction) {
        int tempPoints = actionPointsForTurn;
        tempPoints -= initialActionCost.getDrill();
        if (tempPoints >= 0) {

            StructureTunnelRequest req = new StructureTunnelRequest();
            req.setUnit(unitID);
            req.setDirection(direction);
            StructureTunnelResponse res = api.structureTunnel(req);
            Util.wait(10);
            if (res.getResult().getType().equals(ResultType.DONE)) {
                map[Util.convertCoordinateToMapCoordinate(selectBuilder(unitID).getCord().getY())][selectBuilder(unitID).getCord().getX()].setCellType(CellType.TUNNEL);
                serviceState = res.getResult();
                actionPointsForTurn = tempPoints;
                Util.printMap();
                return true;
            } else {
                System.out.println(res.getResult());
                return false;
            }

        } else {
            System.out.println("nincs elég pont építeni");
            return false;
        }
    }

    public boolean explode(int unitID, WsDirection direction) {
        System.out.println("EXPLOOOOOODE");
        return false;
    }


    public void startTurn(int i) {
        System.out.println("kör:" + i);
        builderUnits.get(serviceState.getBuilderUnit()).getCord();

    }
}
