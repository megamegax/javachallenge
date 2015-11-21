package jmx.javachallenge.builder;

import eu.loxon.centralcontrol.WsCoordinate;
import jmx.javachallenge.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Created by joci on 11/19/15.
 * A defenzív algoritmussal szemben itt távoli pontok lesznek kijelölve,
 * azokhoz közelít a builder, ha eléri, csinál valamit,
 * esetleg spéci null értéket ad vissza, jelezve a buildernek,
 * hogy váltson másik stratégiára (defenzív?),
 * vagy az explorer kijelölhet egy újabb távoli pontot (pókháló jellegű bejárás)
 */
//TODO statikus listában tárolni a többi explorer strategy célpontját
//és az új célpontokat úgy kéne kikalkulálni, hogy az előző pontokhoz
// képest a távolságuk maximális legyen
public class ExplorerStrategy implements Strategy {
    private final int unitID;
    private int current = 0;
    private List<WsCoordinate> coordinates;
    private WsCoordinate exitPoint;
    //generátor ami mindig az egyre távolabbi szomszédokat adja vissza
    //először az 1 távolságra lévőket, aztán a 2, 3, 4, stb.
    private final Supplier<List<WsCoordinate>> generator = new Supplier<List<WsCoordinate>>() {
        private WsCoordinate center = service.getSpaceShuttleCoord();

        @Override
        public List<WsCoordinate> get() {
            System.out.println("GETBE JUT A GENERÁTOR");
            List<WsCoordinate> coordinates = new ArrayList<>();
            WsCoordinate currentCoordinate = service.builderUnits.get(unitID).getCord();
            if (isUnitInSpaceComp(unitID)) {
                exitPoint = service.getSpaceShuttleExitPos();
                Logger.log("!! még a kompban");
                System.out.println(exitPoint);
                coordinates.add(exitPoint);
                coordinates.add(exitPoint);

                return coordinates;
                // coordinates.add(exitPoint);

            } else {


                int maxX = service.initialGameState.getSize().getX();
                int maxY = service.initialGameState.getSize().getY();
                Random r = new Random();
                int x = r.nextInt(maxX) + 1;
                int y = r.nextInt(maxY) + 1;
                for (int i = 0; i < 5; i++) {
                    x = r.nextInt(maxX) + 1;
                    y = r.nextInt(maxY) + 1;
                }
                System.out.println("---------");
                System.out.println(x);
                System.out.println(y);
                int tx = currentCoordinate.getX();
                int ty = currentCoordinate.getY();
                for (int i = 0; i < (x + y) * 2; i++) {
                    if (i % 2 == 0) {
                        if (tx <= x) {
                            coordinates.add(new WsCoordinate(tx, ty));
                            if (x < tx)
                                tx--;
                            else
                                tx++;
                        }
                    } else {
                        if (ty <= y) {
                            coordinates.add(new WsCoordinate(tx, ty));
                            if (y < ty)
                                ty--;
                            else
                                ty++;
                        }
                    }
                }
                Logger.log(unitID + " -» útvonal:" + coordinates);
            }
            return coordinates;
        }


    };

    public ExplorerStrategy(int unitID) {
        this.unitID = unitID;
        this.coordinates = generator.get();
    }

    public void clear() {
        coordinates.clear();
        current = 0;
        coordinates.addAll(generator.get());
    }

    @Override
    public WsCoordinate nextCoordinate() {
        if (coordinates.isEmpty()) {
            clear();
        }
        WsCoordinate c = coordinates.get(current);
        if (current + 1 >= coordinates.size()) {//ha már csak 1 elem maradna, akkor feltöltjük a listát az eggyel távolabbi szomszédokkal
            clear();// coordinates.addAll(generator.get());
        }

        return c;

    }

    private boolean isUnitInSpaceComp(int unitID) {
        return service.builderUnits.get(unitID).getCord().equals(service.getSpaceShuttleCoord());
    }

    @Override
    public boolean done() {
        current++;
        //coordinates.remove((0));
        return true;
    }


}