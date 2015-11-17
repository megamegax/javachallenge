package jmx.javachallenge.helper;

/**
 * Created by megam on 2015. 11. 15..
 */
public class Tile {
    int unitID;
    TileType tileType;

    public Tile() {
        this.unitID = -1;
        tileType = TileType.UNKNOWN;
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
    }

    public void setBuilder(int unitID) {
        this.unitID = unitID;
    }

    public String toString() {
        return String.valueOf((tileType.getValue() >= 0 ? " " + tileType.getValue() : tileType.getValue())) + (unitID == -1 ? "[ ] " : "[" + unitID + "] ");
    }

    public int getTileType() {
        return tileType.getValue();
    }
}
