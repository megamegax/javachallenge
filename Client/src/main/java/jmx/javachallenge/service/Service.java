package jmx.javachallenge.service;

import eu.loxon.centralcontrol.*;

import java.util.OptionalDouble;
import java.util.stream.Stream;

/**
 * Created by MegaX on 2015. 11. 12..
 */
public class Service {
    public static CentralControl api = null;

    static {
        java.net.Authenticator.setDefault(new java.net.Authenticator() {
            @Override
            protected java.net.PasswordAuthentication getPasswordAuthentication() {
                return new java.net.PasswordAuthentication("jmx", "XWHD7855".toCharArray());
            }
        });
    }

    public Service() {
        CentralControlServiceService service = new CentralControlServiceService();
        api = service.getCentralControlPort();

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
        //     printMessage(res.getResult());
        return res.isIsYourTurn();
    }

    public ActionCostResponse getActionCost() {
        ActionCostResponse res = api.getActionCost(new ActionCostRequest());
        printMessage(res.getResult());
        System.out.println(res.toString());

        return res;
    }

    public GetSpaceShuttlePosResponse getSpaceShuttlePos() {
        GetSpaceShuttlePosResponse res = api.getSpaceShuttlePos(new GetSpaceShuttlePosRequest());
        printMessage(res.getResult());
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
}
