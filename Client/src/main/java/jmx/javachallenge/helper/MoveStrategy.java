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
    boolean canMoveTo(Tile tile);
}
