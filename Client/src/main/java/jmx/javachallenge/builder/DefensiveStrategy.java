package jmx.javachallenge.builder;

import eu.loxon.centralcontrol.WsCoordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by joci on 11/19/15.
 * Ez a strategy a középpont körül kezd el körkörösen építkezni, ilyen koordinátákat ad vissza a buildernek.
 */
public class DefensiveStrategy implements Strategy {
    //generátor ami mindig az egyre távolabbi szomszédokat adja vissza
    //először az 1 távolságra lévőket, aztán a 2, 3, 4, stb.
    private final Supplier<List<WsCoordinate>> generator = new Supplier<List<WsCoordinate>>() {
        private int radius = 1;
        private WsCoordinate center = service.initialPos.getCord();

        @Override
        public List<WsCoordinate> get() {
            List<WsCoordinate> coordinates = new ArrayList<>();
            for (int i = -radius; i <= radius; ++i) {
                for (int j = -radius; j <= radius; ++j) {
                    if (onBorder(i, j)) {
                        coordinates.add(new WsCoordinate(center.getX() - i, center.getY() - j));
                    }
                }
            }
            radius++;
            return coordinates;
        }

        private boolean onBorder(int i, int j) {
            return i == -radius || i == radius || j == -radius || j == radius;
        }
    };
    private List<WsCoordinate> coordinates;
    private WsCoordinate exitPoint = service.initialExitPos.getCord();
    private WsCoordinate previous;

    public DefensiveStrategy() {
        this.coordinates = generator.get();
    }

    @Override
    public WsCoordinate nextCoordinate() {
        if (coordinates.contains(exitPoint)) {//első lépés
            //coordinates.remove(exitPoint);
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
            //coordinates.remove(next);
            if(coordinates.size()==1){//ha már csak 1 elem maradna, akkor feltöltjük a listát az eggyel távolabbi szomszédokkal
                coordinates.addAll(generator.get());
            }
            previous = next;
            return next;
        }
    }

    @Override
    public boolean done() {
        coordinates.remove((previous));
        return true;
    }

    @Override
    public void clear() {
        coordinates.clear();
        coordinates.addAll(generator.get());
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
