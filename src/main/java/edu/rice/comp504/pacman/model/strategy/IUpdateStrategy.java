package edu.rice.comp504.pacman.model.strategy;


import edu.rice.comp504.pacman.model.Typeable;
import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;

/**
 * The IUpdateStrategy is an interface designed for update.
 */
public interface IUpdateStrategy extends Typeable {

    /**
     * Update the location and state for a ghost and a pacman.
     * @param context   A ghost
     * @param man       A pacman
     */
    void update(Ghost context, Man man);

    /**
     * Change the ghost's strategy.
     * @param context       The ghost
     * @param nextStrategy  The next strategy
     */
    void setNextStrategy(Ghost context, IUpdateStrategy nextStrategy);

    /**
     * Get the timer.
     * @return The timer
     */
    int getTimer();
}
