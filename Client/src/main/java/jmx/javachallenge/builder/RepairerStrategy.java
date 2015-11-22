package jmx.javachallenge.builder;

import eu.loxon.centralcontrol.WsCoordinate;
import jmx.javachallenge.helper.MoveStrategy;
import jmx.javachallenge.helper.Tile;
import jmx.javachallenge.helper.TileType;
import jmx.javachallenge.helper.Util;
import jmx.javachallenge.logger.Logger;

import java.util.ArrayList;

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
public class RepairerStrategy implements Strategy {
    private final int unitID;
    private final JMXBuilder builderUnit;
    private WsCoordinate destination;

    public void setDestination(WsCoordinate destination) {
        this.destination = destination;
    }

    public RepairerStrategy(int unitID) {
        this.unitID = unitID;
        this.builderUnit = service.builderUnits.get(unitID);
        this.destination = Util.getRandomCoordinate();
    }

    @Override
    public WsCoordinate nextCoordinate() {
        if (builderUnit.getCord().equals(service.getSpaceShuttleCoord())) {
            return service.getSpaceShuttleExitPos();
        } else {
            if (destination == null) {
                Logger.log("HIBA: Nincs megadva cél!");
                return builderUnit.getCord();
            }
            if (destination.equals(builderUnit.getCord())) {
                // Célban vagyunk
                Logger.log("Megérkeztünk, új koordinátát keresünk");
                this.destination = Util.getRandomCoordinate();
            }
            ArrayList<WsCoordinate> path = Util.planRoute(service.getCurrentMap(), builderUnit.getCord(), destination, new MoveStrategy() {
                @Override
                public int getDistanceTo(Tile tile) {
                    return getCostOfMoveToTile(tile);
                }
            });
            if (path != null) {
                if (path.size() > 1) {
                    return path.get(1);
                } else {
                    // TODO
                    Logger.log("HIBA: Meg vagyunk érkezve, várakozunk");
                    return path.get(0);
                }
            } else {
                // TODO
                Logger.log("HIBA: Nem tudunk odaérni a kiválasztott ponthoz, várunk egy kicsit és keresünk újat");
                this.destination = Util.getRandomCoordinate();
                return builderUnit.getCord();
            }
        }
    }

    private int getCostOfMoveToTile(Tile tile){
        switch (tile.getTileType()) {
            case UNKNOWN:
                return 50;
            case SHUTTLE:
                return 10000;
            case ROCK:
                return service.getActionCosts().getDrill() + service.getActionCosts().getMove();
            case OBSIDIAN:
                return 10000;
            case TUNNEL:
                return service.getActionCosts().getMove()+service.getActionCosts().getDrill();
            case BUILDER:
                return service.getActionCosts().getMove() + 60;
            case GRANITE:
                return service.getActionCosts().getExplode() + service.getActionCosts().getDrill() + service.getActionCosts().getMove()+50;
            case ENEMY_TUNNEL:
                return 1;
            case ENEMY_SHUTTLE:
                return 10000;
            case ENEMY_BUILDER:
                return 500;
        }
        return 1;
    }

}