package hu.jmx.javachallenge.builder;

import eu.loxon.centralcontrol.WsCoordinate;
import hu.jmx.javachallenge.helper.MoveStrategy;
import hu.jmx.javachallenge.helper.Tile;
import hu.jmx.javachallenge.helper.Util;
import hu.jmx.javachallenge.logger.Logger;

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
    private final JMXBuilder builderUnit;
    private WsCoordinate destination;

    public void setDestination(WsCoordinate destination) {
        this.destination = destination;
    }

    public RepairerStrategy(JMXBuilder builder) {
        this.builderUnit = builder;
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
                public boolean canMoveTo(Tile tile) {
                    switch (tile.getTileType()) {
                        case UNKNOWN:
                            return true;
                        case SHUTTLE:
                            return false;
                        case ROCK:
                            return true;
                        case OBSIDIAN:
                            return false;
                        case TUNNEL:
                            return true;
                        case BUILDER:
                            return false;
                        case GRANITE:
                            return true;
                        case ENEMY_TUNNEL:
                            return true;
                        case ENEMY_SHUTTLE:
                            return false;
                        case ENEMY_BUILDER:
                            return false;
                    }
                    return false;
                }

                @Override
                public int getDistanceTo(Tile tile) {
                    return getCostOfMoveToTile(tile);
                }
            });
            if (path != null && path.size() != 0) {
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
                return service.getActionCosts().getDrill() + service.getActionCosts().getMove();
            case SHUTTLE:
                return 10000;
            case ROCK:
                return service.getActionCosts().getDrill() + service.getActionCosts().getMove();
            case OBSIDIAN:
                return 10000;
            case TUNNEL:
                return service.getActionCosts().getMove()+service.getActionCosts().getDrill()+20;
            case BUILDER:
                return service.getActionCosts().getMove() + 200;
            case GRANITE:
                return service.getActionCosts().getExplode() + service.getActionCosts().getDrill() + service.getActionCosts().getMove()+10;
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