import eu.loxon.centralcontrol.ActionCostResponse;
import eu.loxon.centralcontrol.GetSpaceShuttleExitPosResponse;
import eu.loxon.centralcontrol.GetSpaceShuttlePosResponse;
import eu.loxon.centralcontrol.StartGameResponse;
import jmx.javachallenge.service.Service;

/**
 * Created by Marton on 2015. 11. 12..
 */
public class Client {


    private static Service service;
    private static GetSpaceShuttlePosResponse initialPos;
    private static GetSpaceShuttleExitPosResponse initialExitPos;
    private static ActionCostResponse initialActionCost;
    private static StartGameResponse initialGameState;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello JMX");
        service = new Service();
        initialPos = service.getSpaceShuttlePos();
        initialExitPos = service.getSpaceShuttlePosExit();
        initialActionCost = service.getActionCost();
        initialGameState = service.startGame();
        for (int i = 0; i < 70; i++) {
            Thread.sleep(301);
            if (service.isMyTurn()) {
                doJob(i);
            }
        }
        service.getStats();
    }

    private static void doJob(int i) {
        System.out.println("kör:" + (i + 1));

    }
}

