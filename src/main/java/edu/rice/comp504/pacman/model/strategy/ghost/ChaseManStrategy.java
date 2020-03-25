package edu.rice.comp504.pacman.model.strategy.ghost;


import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;
import edu.rice.comp504.pacman.model.strategy.IUpdateStrategy;

/**
 * This strategy is the initial strategy for ghost 3 and ghost 4
 * They will chase after the man, using a DFS strategy.
 */

public class ChaseManStrategy implements IUpdateStrategy {
    public static ChaseManStrategy only = null;

    /**
     * Get instance of AmbushStrategy.
     *
     * @return AmbushStrategy
     */
    public static ChaseManStrategy getStrategy() {
        if (only == null) {
            only = new ChaseManStrategy();
        }
        return only;
    }

    /**
     * Update the status of the ghost.
     *
     * @param context A ghost
     * @param man     man
     */
    @Override
    public void update(Ghost context, Man man) {
        context.goTo(man.getLoc());
    }

    @Override
    public String getType() {
        return "ChaseManStrategy";
    }

    @Override
    public void setNextStrategy(Ghost context, IUpdateStrategy nextStrategy) {

    }

    @Override
    public int getTimer() {
        return 0;
    }
}
