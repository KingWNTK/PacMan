package edu.rice.comp504.pacman.model.cmd;

import edu.rice.comp504.pacman.model.GameContext;
import edu.rice.comp504.pacman.model.Typeable;
import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;
import edu.rice.comp504.pacman.model.pac.Pac;
import edu.rice.comp504.pacman.model.strategy.ghost.AfterDieStrategy;

import java.awt.*;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

/**
 * This is the interaction command. It handles the interactions between a Pacman and ghosts.
 */
public class InteractCmd implements IPacManCmd {
    private Man man;
    private ArrayList<Ghost> ghosts;
    private PropertyChangeSupport pcs;
    private Point newManDir;

    /**
     * Factory function.
     *
     * @param newDir New direction
     * @param man    The Pacman
     * @param ghosts The ghosts array
     * @param pcs    The property change listener
     * @return The Interact Command
     */
    public static InteractCmd getCmd(Point newDir, Man man, ArrayList<Ghost> ghosts, PropertyChangeSupport pcs) {
        return new InteractCmd(newDir, man, ghosts, pcs);
    }

    /**
     * The constructor.
     *
     * @param newDir New direction
     * @param man    The Pacman
     * @param ghosts The ghosts array
     * @param pcs    The property change listener
     */
    private InteractCmd(Point newDir, Man man, ArrayList<Ghost> ghosts, PropertyChangeSupport pcs) {
        this.man = man;
        this.ghosts = ghosts;
        this.pcs = pcs;
        this.newManDir = newDir;
    }

    /**
     * Initialize the ghosts.
     */
    public void initializeGhost() {
        for (int i = 0; i < ghosts.size(); i++) {
            ghosts.get(i).reBirth();
        }
    }

    /**
     * Initialize the Pacman.
     */
    public void initializeMan() {
        man.reduceLive();
        man.resetMan();
    }

    /**
     * Make the chosen ghost die.
     *
     * @param context The ghost
     */
    public void setDie(Ghost context) {
        this.goToNearest(context);
        context.setUpdateStrategy(AfterDieStrategy.getStrategy());
        context.setIsDead(true);
        context.setStayAway(false);
        context.setFlashing(false);
    }

    /**
     * Make the ghost go to the next possible point.
     *
     * @param ghost The ghost
     */
    public void goToNearest(Ghost ghost) {
        Point dir = new Point(-ghost.getDir().x, -ghost.getDir().y);
        while (ghost.getLoc().x % 20 != 0 || ghost.getLoc().y % 20 != 0) {
            ghost.setLoc(new Point(ghost.getLoc().x + dir.x, ghost.getLoc().y + dir.y));
        }
    }

    @Override
    public void execute(Typeable context) {
        if (context.getType().equals("pac") || context.getType().equals("fruit")) {
            ((Pac) context).getCollisionStrategy().interact(context, man, ghosts);
        } else if (context.getType().equals("ghost")) {
            Boolean isCollision = ((Ghost) context).isCollidedWith(man);
            GameContext gameContext = ((Ghost) context).getGameContext();
            if (isCollision) {
                Boolean isStayAway = gameContext.isStayAwayActive() &&
                        (((Ghost) context).getStayAway() || ((Ghost) context).getFlashing())
                        && !((Ghost) context).getIsDead();
                if (isStayAway) {
                    // 1. if the ghost is flashing or darkblue
                    // 2. if the stayaway session is active now
                    // 3. if the ghost is not dead
                    this.goToNearest((Ghost) context);
                    this.setDie((Ghost) context);
                    // then the man will get the score
                    man.addScore(gameContext.getStayAwayGhostScore());
                    ((Ghost) context).setDieScore(gameContext.getStayAwayGhostScore());
                    gameContext.doubleStayAwayGhostScore();
                } else if (!((Ghost) context).getIsDead()) {
                    // 1. if met with a normal ghost
                    // 2. if the ghost is not on the death way
                    initializeGhost();
                    initializeMan();
                    gameContext.init();
                }
            }
        }
    }
}
