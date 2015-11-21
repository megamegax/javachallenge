package jmx.javachallenge.helper;

/**
 * Created by Márton on 2015. 11. 21..
 */
public interface MoveStrategy {
    /**
     * Megmondja, hogy mekkora súllyal számoljuk a mezőre lépést.
     * @param tile Annak a mezőnek a típusa, ahonnan indulunk
     * @return Az útvonalkereséskor használt súly (aka távolság). Minden mező esetében >=1 (ez tekinthető az üres-üres
     * átlépésnek), különben nem működik az útvonalkeresés!
     */
    int getDistanceTo(Tile tile);

    /**
     * Megmondja, hogy léphetünk-e egy adott mezőre. (Pl. ha az ellenség fejével akarunk útvonalat tervezni, akkor
     * az ellenség alagútjaira léphetünk csap
     * @param tile A mező típusa
     * @return Ráléphetünk-e
     */
    default boolean canMoveTo(Tile tile) {
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
                return false;
            case ENEMY_SHUTTLE:
                return false;
            case ENEMY_BUILDER:
                return false;
        }
        return false;
    }
}
