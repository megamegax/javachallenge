package jmx.javachallenge.service;

import eu.loxon.centralcontrol.*;
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
    public WsCoordinate currentCoordinates;
    public HashMap<Integer, WsBuilderunit> builderUnits = new HashMap<>();
    public int turnLeft = 70;
    private GetSpaceShuttleExitPosResponse initialExitPos;
    private ActionCostResponse initialActionCost;
    private StartGameResponse initialGameState;

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
        return res;
    }

    public boolean isMyTurn() {
        IsMyTurnResponse res = api.isMyTurn(new IsMyTurnRequest());
        serviceState = res.getResult();
        if (turnLeft > res.getResult().getTurnsLeft()) {
            turnLeft = res.getResult().getTurnsLeft();
            System.out.println("ismyturn:" + res.toString());
            actionPointsForTurn = res.getResult().getActionPointsLeft();
            return res.isIsYourTurn();
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
            System.out.println(res.toString());
            return res;
        } else return initialPos;
    }

    public void printMessage(CommonResp res) {
        actionPointsForTurn = res.getActionPointsLeft();
        // System.out.println(res.getMessage());

        // System.out.println(res.getType().value());
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

    public boolean watch() {
        actionPointsForTurn -= initialActionCost.getWatch();
        if (actionPointsForTurn > 0) {
            WatchResponse res = api.watch(new WatchRequest());
            System.out.println(res.toString());
            return true;
        } else
            return false;

    }

    public boolean moveUnit(WsDirection direction) {
        actionPointsForTurn -= initialActionCost.getMove();
        if (actionPointsForTurn > 0) {
            MoveBuilderUnitRequest req = new MoveBuilderUnitRequest();
            req.setUnit(serviceState.getBuilderUnit());
            req.setDirection(direction);

            MoveBuilderUnitResponse res = api.moveBuilderUnit(req);
            serviceState = res.getResult();
            Util.updateCoords(res.getResult().getBuilderUnit(), direction);
            currentCoordinates = builderUnits.get(serviceState.getBuilderUnit()).getCord();
            System.out.println(res.toString());
            return true;
        } else
            return false;
    }

    public boolean radar(List<WsCoordinate> coordinates) {
        actionPointsForTurn -= initialActionCost.getRadar();
        if (actionPointsForTurn > 0) {
            RadarRequest req = new RadarRequest();
            req.setUnit(serviceState.getBuilderUnit());
            req.getCord().addAll(coordinates);
            RadarResponse res = api.radar(req);
            System.out.println(res.toString());
            serviceState = res.getResult();
            return true;
        } else
            return false;
    }

    public boolean structureTunnel(WsDirection direction) {
        actionPointsForTurn -= initialActionCost.getDrill();
        if (actionPointsForTurn > 0) {
            StructureTunnelRequest req = new StructureTunnelRequest();
            req.setUnit(serviceState.getBuilderUnit());
            req.setDirection(direction);
            StructureTunnelResponse res = api.structureTunnel(req);
            System.out.println(res.toString());
            serviceState = res.getResult();
            return true;
        } else
            return false;
    }

    public void startTurn(int i) {
        System.out.println("kör:" + i);
        currentCoordinates = builderUnits.get(serviceState.getBuilderUnit()).getCord();

    }
}
