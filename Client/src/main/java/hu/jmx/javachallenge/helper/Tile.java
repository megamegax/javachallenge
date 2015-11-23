package hu.jmx.javachallenge.helper;

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

    public int getTileTypeIndex() {
        return tileType.getValue();
    }

    public TileType getTileType() {
        return tileType;
    }

    public int getUnitId() {
        return unitID;
    }
}
