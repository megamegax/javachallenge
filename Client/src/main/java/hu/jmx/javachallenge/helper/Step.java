package hu.jmx.javachallenge.helper;

/**
 * Created by megam on 2015. 11. 15..
 */
public enum Step {
    MOVE, BUILD, EXPLODE, STAY, WATCH,NO_POINTS;

    public static Step getStep(TileType tileType){
        switch (tileType) {
            case UNKNOWN:
                return Step.WATCH;
            case SHUTTLE:
                return Step.STAY;
            case ROCK:
                return Step.BUILD;
            case OBSIDIAN:
                return Step.STAY;
            case TUNNEL:
                return Step.MOVE;
            case BUILDER:
                return Step.STAY;
            case GRANITE:
                return Step.EXPLODE;
            case ENEMY_TUNNEL:
                return Step.EXPLODE;
            case ENEMY_SHUTTLE:
                return Step.STAY;
            case ENEMY_BUILDER:
                return Step.STAY;
            default:
                return Step.STAY;
        }
    }

}
