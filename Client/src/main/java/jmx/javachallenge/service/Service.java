package jmx.javachallenge.service;

import eu.loxon.centralcontrol.*;

import java.util.HashMap;
import java.util.OptionalDouble;
import java.util.stream.Stream;

/**
 * Created by MegaX on 2015. 11. 12..
 */
public class Service {
    public static CentralControl api = null;
    public static CommonResp serviceState;

    static {
        java.net.Authenticator.setDefault(new java.net.Authenticator() {
            @Override
            protected java.net.PasswordAuthentication getPasswordAuthentication() {
                return new java.net.PasswordAuthentication("jmx", "XWHD7855".toCharArray());
            }
        });
    }

    HashMap<Integer, WsBuilderunit> unitState = new HashMap<>();

    public Service() {
        CentralControlServiceService service = new CentralControlServiceService();
        api = service.getCentralControlPort();
        unitState.put(0, new WsBuilderunit());
        unitState.put(1, new WsBuilderunit());
        unitState.put(2, new WsBuilderunit());
        unitState.put(3, new WsBuilderunit());
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
            System.out.println(average.getAsDouble());
        }
    }

    public StartGameResponse startGame() {
        StartGameResponse res = api.startGame(new StartGameRequest());

        System.out.println(res.toString());
        return res;
    }

    public boolean isMyTurn() {
        IsMyTurnResponse res = api.isMyTurn(new IsMyTurnRequest());
        serviceState = res.getResult();

        //     printMessage(res.getResult());
        return res.isIsYourTurn();
    }

    public ActionCostResponse getActionCost() {
        ActionCostResponse res = api.getActionCost(new ActionCostRequest());
        printMessage(res.getResult());
        serviceState = res.getResult();
        System.out.println(res.toString());

        return res;
    }

    public GetSpaceShuttlePosResponse getSpaceShuttlePos() {
        GetSpaceShuttlePosResponse res = api.getSpaceShuttlePos(new GetSpaceShuttlePosRequest());
        printMessage(res.getResult());
        unitState.get(0).setCord(res.getCord());
        unitState.get(1).setCord(res.getCord());
        unitState.get(2).setCord(res.getCord());
        unitState.get(3).setCord(res.getCord());
        serviceState = res.getResult();
        System.out.println(res.toString());
        return res;
    }

    public void printMessage(CommonResp res) {
        // System.out.println(res.getMessage());
        // System.out.println(res.getType().value());
    }

    public GetSpaceShuttleExitPosResponse getSpaceShuttlePosExit() {
        GetSpaceShuttleExitPosResponse res = api.getSpaceShuttleExitPos(new GetSpaceShuttleExitPosRequest());
        printMessage(res.getResult());
        System.out.println(res.toString());

        return res;
    }

    public WatchResponse getStats() {
        WatchResponse res = api.watch(new WatchRequest());
        System.out.println(res.toString());
        return res;
    }

    public void moveUnit(int unitID, WsDirection direction) {
        MoveBuilderUnitRequest req = new MoveBuilderUnitRequest();
        req.setUnit(unitID);
        req.setDirection(direction);

        MoveBuilderUnitResponse res = api.moveBuilderUnit(req);
        serviceState = res.getResult();

        System.out.println(res.toString());
    }

    public void radar(int unitID) {
        RadarRequest req = new RadarRequest();
        req.setUnit(unitID);
        req.getCord().add(unitState.get(unitID).getCord());
        RadarResponse res = api.radar(req);
        System.out.println(res.toString());
        serviceState = res.getResult();

    }
}
