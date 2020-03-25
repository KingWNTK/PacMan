package edu.rice.comp504.pacman.model.strategy.ghost;

import edu.rice.comp504.pacman.model.DispatchAdapter;
import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;
import edu.rice.comp504.pacman.model.strategy.IUpdateStrategy;
import edu.rice.comp504.pacman.model.wall.Wall;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * If Pac-Man collides with a dark blue or flashing ghost,
 * the ghosts become two eyes and travel quickly to the square box in the middle of the screen.
 * A non dark blue,
 */

public class AfterDieStrategy implements IUpdateStrategy {
    private static AfterDieStrategy only = null;

    /**
     * Get instance of strategy.
     *
     * @return get AfterDieStrategy
     */
    public static AfterDieStrategy getStrategy() {
        if (only == null) {
            only = new AfterDieStrategy();
        }
        return only;
    }

    /**
     * Update the status the the ghost.
     *
     * @param context A ghost
     * @param man     man
     */
    @Override
    public void update(Ghost context, Man man) {
        if (Math.abs(context.getLoc().x - context.getInitialPosition().x) <= 10 && Math.abs(context.getLoc().y - context.getInitialPosition().y) <= 10) {
            context.reBirth();
        } else {
            int v = context.getVel();
            context.setVel(context.getDieVel());
            context.goTo(context.getInitialPosition());
            context.setVel(v);
        }
    }

    @Override
    public String getType() {
        return "AfterDieStrategy";
    }

    @Override
    public void setNextStrategy(Ghost context, IUpdateStrategy nextStrategy) {

    }

    @Override
    public int getTimer() {
        return 0;
    }
}
