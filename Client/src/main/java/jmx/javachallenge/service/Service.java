package jmx.javachallenge.service;

import eu.loxon.centralcontrol.*;

import java.util.OptionalDouble;
import java.util.stream.Stream;

/**
 * Created by megam on 2015. 11. 12..
 */
public class Service {
    static {
        java.net.Authenticator.setDefault(new java.net.Authenticator() {
            @Override
            protected java.net.PasswordAuthentication getPasswordAuthentication() {
                return new java.net.PasswordAuthentication("jmx", "XWHD7855".toCharArray());
            }
        });
    }

    public static CentralControl api = null;

    public Service() {
        CentralControlServiceService service = new CentralControlServiceService();
        api = service.getCentralControlPort();
        StartGameResponse res = api.startGame(new StartGameRequest());
        int actionPointsLeft = res.getResult().getActionPointsLeft();
        int initialBuilderUnit = res.getResult().getBuilderUnit();
        String code = res.getResult().getCode();
        int initialExposes = res.getResult().getExplosivesLeft();
        String message = res.getResult().getMessage();
        int initX = res.getSize().getX();
        int initY = res.getSize().getY();

        System.out.println("action points = " + actionPointsLeft + ", builder unit = " + initialBuilderUnit + ", code" + code + ", exposes = " + initialExposes + ", message = " + message + ", x:" + initX + ", y:" + initY);

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


    public void getActionCost() {
        ActionCostResponse res = api.getActionCost(new ActionCostRequest());
        System.out.println("drill: " + res.getDrill() + ", explode: " + res.getExplode() + ", move: " + res.getMove() + ", radar: " + res.getRadar() + ", watch: " + res.getWatch());
        printMessage(res.getResult());
    }

    public void getSpaceShuttlePos() {
        GetSpaceShuttlePosResponse res = api.getSpaceShuttlePos(new GetSpaceShuttlePosRequest());
        System.out.println("action points = " + res.getResult().getActionPointsLeft());
        System.out.println("spaceshuttle coords: x:" + res.getCord().getX() + ", y:" + res.getCord().getY());
        printMessage(res.getResult());
    }

    public void printMessage(CommonResp res) {
        System.out.println(res.getMessage());
        System.out.println(res.getType().value());
    }

    public void getSpaceShuttlePosExit() {
        GetSpaceShuttleExitPosResponse res = api.getSpaceShuttleExitPos(new GetSpaceShuttleExitPosRequest());
        System.out.println("action points = " + res.getResult().getActionPointsLeft());
        System.out.println("spaceshuttle coords: x:" + res.getCord().getX() + ", y:" + res.getCord().getY());
        printMessage(res.getResult());
    }
}
