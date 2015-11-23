package hu.jmx.javachallenge.helper;

/**
 * Created by megam on 2015. 11. 14..
 */
public enum TileType {
    UNKNOWN(-1), SHUTTLE(0), ROCK(1), OBSIDIAN(2), TUNNEL(3),BUILDER(4),GRANITE(5),ENEMY_TUNNEL(13),ENEMY_SHUTTLE(10),ENEMY_BUILDER(14);
    private final int type;

    TileType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
