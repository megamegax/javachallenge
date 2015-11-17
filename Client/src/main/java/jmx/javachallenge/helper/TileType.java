package jmx.javachallenge.helper;

/**
 * Created by megam on 2015. 11. 14..
 */
public enum TileType {
    UNKNOWN(-1), SHUTTLE(0), ROCK(1), OBSIDIAN(2), TUNNEL(3),BUILDER_UNIT(4),GRANITE(5);
    private final int type;

    TileType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
