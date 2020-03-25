package edu.rice.comp504.pacman.model.strategy.ghost;

import edu.rice.comp504.pacman.model.GameContext;
import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;
import edu.rice.comp504.pacman.model.strategy.IUpdateStrategy;


/**
 * For this strategy, ghost will random walk at the beginning
 * And will chase after the man, until the manhaton distance reaches to a specific value.
 */
public class AmbushStrategy implements IUpdateStrategy {

    private static AmbushStrategy only = null;

    /**
     * get instance of AmbushStrategy.
     *
     * @return AmbushStrategy
     */
    public static AmbushStrategy getStrategy() {
        if (only == null) {
            only = new AmbushStrategy();
        }
        return only;
    }

    /**
     * update the status of the ghost.
     *
     * @param context A ghost
     * @param man     man
     */
    @Override
    public void update(Ghost context, Man man) {
        this.move(context);
    }

    @Override
    public String getType() {
        return "AmbushStrategy";
    }

    /**
     * move the location of the ghost.
     *
     * @param context ghost
     */
    public void move(Ghost context) {
        // calculate the distance first
        GameContext gameContext = context.getGameContext();
        int curManhattanDis = Math.abs(context.getLoc().x - gameContext.getMan().getLoc().x) + Math.abs(context.getLoc().y - gameContext.getMan().getLoc().y);

        if (curManhattanDis >= 200) {
            context.randomMove();
        } else {
            context.goTo(context.getGameContext().getMan().getLoc());
        }

    }

    @Override
    public void setNextStrategy(Ghost context, IUpdateStrategy nextStrategy) {

    }

    @Override
    public int getTimer() {
        return 0;
    }
}
