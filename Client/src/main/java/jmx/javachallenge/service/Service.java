package jmx.javachallenge.service;

import eu.loxon.centralcontrol.*;
import jmx.javachallenge.helper.CellType;
import jmx.javachallenge.helper.Step;
import jmx.javachallenge.helper.Util;

import java.awt.*;
import java.util.ArrayList;
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
    public int[][] map = new int[19][19]; // -1:unknown;0:shuttle;1:rock;2:obsidian;

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

        for (int x = 0; x < initialGameState.getSize().getX(); x++) {
            for (int y = 0; y < initialGameState.getSize().getY(); y++) {
                map[x][y] = -1;
            }
        }
        return res;
    }

    public boolean isMyTurn() {
        IsMyTurnResponse res = api.isMyTurn(new IsMyTurnRequest());
        serviceState = res.getResult();
        System.out.println("isMyTurn:" + res.toString());

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
            map[res.getCord().getX()][Util.convertCoordinateToMapCoordinate(res.getCord().getY())] = CellType.SHUTTLE.getValue();

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
                    map[scout.getCord().getX()][Util.convertCoordinateToMapCoordinate(scout.getCord().getY())] = Util.stringToCellType(scout.getObject().name()).getValue();
                }
                Util.printMap();
                // System.out.println(res.toString());
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

    public Step moveUnit(int unitID, WsDirection direction) {
        int tempPoints = actionPointsForTurn;
        tempPoints -= initialActionCost.getMove();
        Step answer = Step.NO_POINTS;
        if (tempPoints > 0) {
            MoveBuilderUnitRequest req = new MoveBuilderUnitRequest();
            req.setUnit(unitID);
            req.setDirection(direction);
            answer = Util.checkMovement(Util.simulateMove(selectBuilder(unitID), direction));
            if (answer == Step.MOVE) {
                MoveBuilderUnitResponse res = api.moveBuilderUnit(req);
                Util.wait(10);
                if (res.getResult().getType().equals(ResultType.DONE)) {
                    serviceState = res.getResult();
                    Util.updateCoords(res.getResult().getBuilderUnit(), direction);
                    // System.out.println(res.toString());

                    actionPointsForTurn = tempPoints;
                    return answer;
                } else {
                    //  System.out.println(res.getResult());
                    return answer;
                }
            } else return answer;
        } else
            return answer;
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
                    map[scout.getCord().getX()][Util.convertCoordinateToMapCoordinate(scout.getCord().getY())] = Util.stringToCellType(scout.getObject().name()).getValue();
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

    public Step structureTunnel(int unitID, WsDirection direction) {
        int tempPoints = actionPointsForTurn;
        tempPoints -= initialActionCost.getDrill();
        Step answer = Step.NO_POINTS;
        if (tempPoints > 0) {
            WsCoordinate simulatedCoordinate = Util.simulateMove(builderUnits.get(unitID), direction);
            answer = Util.checkMovement(simulatedCoordinate);
            if (answer == Step.BUILD) {
                StructureTunnelRequest req = new StructureTunnelRequest();
                req.setUnit(unitID);
                req.setDirection(direction);
                StructureTunnelResponse res = api.structureTunnel(req);
                Util.wait(10);
                if (res.getResult().getType().equals(ResultType.DONE)) {
                    map[selectBuilder(unitID).getCord().getX()][Util.convertCoordinateToMapCoordinate(selectBuilder(unitID).getCord().getY())] = 3;
                    System.out.println(res.toString());
                    serviceState = res.getResult();
                    actionPointsForTurn = tempPoints;
                    return Util.checkMovement(simulatedCoordinate);
                } else {
                    System.out.println(res.getResult());
                    return answer;
                }
            } else
                return answer;
        } else
            return answer;
    }

    public void explode(int unitID, WsDirection direction) {

    }


    public void startTurn(int i) {
        System.out.println("kör:" + i);
        builderUnits.get(serviceState.getBuilderUnit()).getCord();

    }
}
