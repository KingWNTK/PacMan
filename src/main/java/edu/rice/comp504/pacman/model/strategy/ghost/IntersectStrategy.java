package edu.rice.comp504.pacman.model.strategy.ghost;

import edu.rice.comp504.pacman.model.GameContext;
import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;
import edu.rice.comp504.pacman.model.strategy.IUpdateStrategy;

import java.awt.*;

/**
 * IntersectStrategy will let ghost go between itself and pac-man.
 */
public class IntersectStrategy implements IUpdateStrategy {
    private static IntersectStrategy only = null;

    /**
     * get instance of the IntersectStrategy.
     *
     * @return IntersectStrategy
     */
    public static IntersectStrategy getStrategy() {
        if (only == null) {
            only = new IntersectStrategy();
        }
        return only;
    }

    @Override
    public String getType() {
        return "Intersect strategy";
    }

    @Override
    public void update(Ghost context, Man man) {
        context.goTo(new Point((int)(context.getLoc().x + man.getLoc().x) / 2, (int)(context.getLoc().y + man.getLoc().y) / 2));
    }

    @Override
    public void setNextStrategy(Ghost context, IUpdateStrategy nextStrategy) {

    }

    @Override
    public int getTimer() {
        return 0;
    }
}
