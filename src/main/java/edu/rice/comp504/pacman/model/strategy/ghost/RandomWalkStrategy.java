package edu.rice.comp504.pacman.model.strategy.ghost;


import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;
import edu.rice.comp504.pacman.model.strategy.IUpdateStrategy;


/**
 * This strategy is the initial strategy for ghost 1 and ghost 2,
 * they will not chase after the ghost, and set a random walk.
 */


public class RandomWalkStrategy implements IUpdateStrategy {
    private static RandomWalkStrategy only = null;

    /**
     * get instance of the RandomWalkStrategy.
     *
     * @return RandomWalkStrategy
     */
    public static RandomWalkStrategy getStrategy() {
        if (only == null) {
            only = new RandomWalkStrategy();
        }
        return only;
    }


    @Override
    public void setNextStrategy(Ghost context, IUpdateStrategy nextStrategy) {

    }

    @Override
    public int getTimer() {
        return 0;
    }

    /**
     * update the status of the ghost.
     *
     * @param context A ghost
     * @param man     man
     */
    @Override
    public void update(Ghost context, Man man) {
        context.randomMove();
    }


    @Override
    public String getType() {
        return "RandomWalkStrategy";
    }

}
