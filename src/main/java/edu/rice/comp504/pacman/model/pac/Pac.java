package edu.rice.comp504.pacman.model.pac;

import edu.rice.comp504.pacman.model.GameContext;
import edu.rice.comp504.pacman.model.MovingObject;
import edu.rice.comp504.pacman.model.Typeable;
import edu.rice.comp504.pacman.model.cmd.IPacManCmd;
import edu.rice.comp504.pacman.model.strategy.ICollisionStrategy;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The class for pac dots.
 */
public class Pac implements PropertyChangeListener, Typeable {
    private transient Point loc;
    private transient boolean eaten;
    private transient ICollisionStrategy collisionStrategy;
    private transient int radius;
    private String type;
    private transient int score;
    private transient GameContext gameContext;
    private String p;
    private String fruitName;

    /**
     * The constructor.
     *
     * @param loc               The initial location.
     * @param collisionStrategy The initial collision strategy.
     */
    public Pac(Point loc, ICollisionStrategy collisionStrategy) {
        this.loc = loc;
        this.type = "pac";
        this.radius = 3;
        this.score = 10;
        this.collisionStrategy = collisionStrategy;
        this.p = "" + loc.x / 20 + "-" + loc.y / 20 + "-" + radius;
    }

    /**
     * The constructor.
     *
     * @param loc               The initial location.
     * @param radius            The radius.
     * @param collisionStrategy The initial collision strategy.
     */
    public Pac(Point loc, int radius, ICollisionStrategy collisionStrategy) {
        this.loc = loc;
        this.type = "pac";
        this.radius = radius;
        this.score = 10;
        this.collisionStrategy = collisionStrategy;
        this.p = "" + loc.x / 20 + "-" + loc.y / 20 + "-" + radius;
    }

    /**
     * Get Pac's type.
     *
     * @return Pac's type.
     */
    @Override
    public String getType() {
        return type;
    }

    public void setFruitName(String name) {
        fruitName = name;
    }

    /**
     * Set the Pac's type.
     */
    public void setType(String type) {
        this.type = type;
    }

    public void setLoc(Point loc) {
        this.loc = loc;
    }

    /**
     * Get Location of the pac.
     *
     * @return Pac's location.
     */
    public Point getLoc() {
        return loc;
    }

    /**
     * Set the pac to eaten.
     */
    public void setEaten(boolean eaten) {
        this.eaten = eaten;
    }

    /**
     * Check whether a pac is eaten.
     *
     * @return whether a pac is eaten.
     */
    public boolean isEaten() {
        return eaten;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * Get radius of the pac.
     *
     * @return Pac's radius.
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Get the score.
     *
     * @return The score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Set the score.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Get current collision strategy.
     *
     * @return Collision Strategy.
     */
    public ICollisionStrategy getCollisionStrategy() {
        return collisionStrategy;
    }

    public void setCollisionStrategy(ICollisionStrategy collisionStrategy) {
        this.collisionStrategy = collisionStrategy;
    }

    /**
     * Set the game's context.
     */
    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    /**
     * Get game context.
     *
     * @return Game context.
     */
    public GameContext getGameContext() {
        return gameContext;
    }

    /**
     * Check if the pac is collided with some moving object.
     *
     * @param object The moving object.
     * @return If they are collided.
     */
    public boolean isCollidedWith(MovingObject object) {
        return (!this.isEaten() && Math.pow(loc.x - object.getLoc().x, 2) + Math.pow(loc.y - object.getLoc().y, 2) <= Math.pow(object.getRadius() - 2, 2));
    }

    /**
     * The property change handler.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        ((IPacManCmd) (evt.getNewValue())).execute(this);
    }
}
