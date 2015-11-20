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
    private List<WsCoordinate> coordinates;
    private WsCoordinate exitPoint;
    //generátor ami mindig az egyre távolabbi szomszédokat adja vissza
    //először az 1 távolságra lévőket, aztán a 2, 3, 4, stb.
    private final Supplier<List<WsCoordinate>> generator = new Supplier<List<WsCoordinate>>() {
        private WsCoordinate center = service.initialPos.getCord();

        @Override
        public List<WsCoordinate> get() {
            System.out.println("GETBE JUT A GENERÁTOR");
            WsCoordinate currentCoordinate;
            exitPoint = service.initialExitPos.getCord();
            if (service.builderUnits.get(unitID) == null) {
                currentCoordinate = service.initialPos.getCord();
            } else {
                if (service.builderUnits.get(unitID).getCord() != null)
                    currentCoordinate = service.builderUnits.get(unitID).getCord();
                else
                    currentCoordinate = service.initialPos.getCord();
            }
            List<WsCoordinate> coordinates = new ArrayList<>();

            if (currentCoordinate == service.initialPos.getCord()) {
                Logger.log("!! még a kompban");
                coordinates.add(exitPoint);
                coordinates.add(exitPoint);

                return coordinates;
                // coordinates.add(exitPoint);
            }
            int maxX = service.initialGameState.getSize().getX();
            int maxY = service.initialGameState.getSize().getY();
            Random r = new Random();
            int x = r.nextInt(maxX) + 1;
            int y = r.nextInt(maxY) + 1;
            int tx = currentCoordinate.getX();
            if (x == 17 || x == 16) {
                x = 1;
            }
            int ty = currentCoordinate.getY();
            for (int i = 0; i < (x + y) * 2; i++) {
                if (i % 2 != 0) {
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
            Logger.log(unitID + " -« útvonal:" + coordinates);
            return coordinates;
        }


    };

    public ExplorerStrategy(int unitID) {
        this.unitID = unitID;
        this.coordinates = generator.get();
    }

    public void clear() {
        coordinates.clear();
        coordinates.addAll(generator.get());
    }

    @Override
    public WsCoordinate nextCoordinate() {

        if (coordinates.size() <= 1) {//ha már csak 1 elem maradna, akkor feltöltjük a listát az eggyel távolabbi szomszédokkal
            coordinates.addAll(generator.get());
        }

        return coordinates.get(0);

    }

    @Override
    public boolean done() {
        coordinates.remove((0));
        return true;
    }


}