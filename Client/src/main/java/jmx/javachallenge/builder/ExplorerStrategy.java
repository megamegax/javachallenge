package jmx.javachallenge.builder;

import eu.loxon.centralcontrol.WsCoordinate;

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
    private WsCoordinate exitPoint = service.initialExitPos.getCord();
    //generátor ami mindig az egyre távolabbi szomszédokat adja vissza
    //először az 1 távolságra lévőket, aztán a 2, 3, 4, stb.
    private final Supplier<List<WsCoordinate>> generator = new Supplier<List<WsCoordinate>>() {
        private WsCoordinate center = service.initialPos.getCord();

        @Override
        public List<WsCoordinate> get() {
            WsCoordinate currentCoordinate;
            if (service.builderUnits.get(unitID) == null) {
                currentCoordinate = service.initialPos.getCord();
            } else {
                currentCoordinate = service.builderUnits.get(unitID).getCord();
            }
            List<WsCoordinate> coordinates = new ArrayList<>();

            if (currentCoordinate == exitPoint) {
                //coordinates.add(exitPoint);
            }
            int maxX = service.initialGameState.getSize().getX();
            int maxY = service.initialGameState.getSize().getY();
            Random r = new Random();
            int x = r.nextInt(maxX) + 1;
            int y = r.nextInt(maxY) + 1;
            int tx = currentCoordinate.getX();
            int ty = currentCoordinate.getY();
            for (int i = 0; i < (x + y) * 2; i++) {
                if (i % 2 == 0) {
                    if (tx <= x) {
                        coordinates.add(new WsCoordinate(tx, ty));
                        tx++;
                    }
                } else {
                    if (ty <= y) {
                        coordinates.add(new WsCoordinate(tx, ty));
                        ty++;
                    }
                }
            }
           /* for(int i = 0;i < y; i++){
                for (int j = 0;j<x;j++){
                    coordinates.add(new WsCoordinate(j,i));
                }
            }*/
            return coordinates;
        }


    };
    private WsCoordinate previous;

    public ExplorerStrategy(int unitID) {
        this.unitID = unitID;
        this.coordinates = generator.get();
    }

    @Override
    public WsCoordinate nextCoordinate() {
        if (coordinates.contains(exitPoint)) {//első lépés
            coordinates.remove(exitPoint);
            previous = exitPoint;
            return exitPoint;
        } else {
            //megkeressük az előző koordináta egyik szomszédját
            WsCoordinate next =
                    coordinates
                            .stream()
                            .filter(this::isNeighbor)
                            .findFirst()
                            .get();
            coordinates.remove(next);
            if (coordinates.size() == 1) {//ha már csak 1 elem maradna, akkor feltöltjük a listát az eggyel távolabbi szomszédokkal
                coordinates.addAll(generator.get());
            }
            previous = next;
            return next;
        }
    }

    private boolean isNeighbor(WsCoordinate c) {
        if (previous == null) {
            previous = coordinates.get(1);
        }
        int x1 = previous.getX();
        int y1 = previous.getY();
        int x2 = c.getX();
        int y2 = c.getY();
        //megegyezik az egyik koordinátájuk és a másik különbsége 1, azaz szomszédok
        return (x1 - x2 == 0 && Math.abs(y1 - y2) == 1) || (y1 - y2 == 0 && Math.abs(x1 - x2) == 1);
    }
}