package hu.jmx.javachallenge.helper;

/**
 * Created by megam on 2015. 11. 15..
 */
public enum Step {
    MOVE, BUILD, EXPLODE, STAY, WATCH,NO_POINTS;

    public static Step getStep(int tileType){
        switch (tileType) {
            case -1: //UNKNOWN
                return Step.WATCH;
            case 0: //SPACE SHUTTLE
                return Step.STAY;

            case 1: //ROCK
                return Step.BUILD;
            case 2: //OBSIDIAN
                return Step.STAY;
            case 3: //TUNNEL
                return Step.MOVE;
            case 4: //ANOTHER BUILDER
                return Step.STAY;
            case 5: //GRANITE
                return Step.EXPLODE;
            case 10: //ENEMY SPACE SHUTTLE
                return Step.STAY;
            case 13: //ENEMY TUNNEL
                return Step.EXPLODE;
            case 14: //ENEMY BUILDER
                return Step.STAY;
            default:
                return Step.STAY;
        }
    }

}
