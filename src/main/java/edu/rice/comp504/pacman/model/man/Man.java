package edu.rice.comp504.pacman.model.man;

import edu.rice.comp504.pacman.model.GameContext;
import edu.rice.comp504.pacman.model.MovingObject;
import edu.rice.comp504.pacman.model.Typeable;
import edu.rice.comp504.pacman.model.cmd.IPacManCmd;
import edu.rice.comp504.pacman.model.wall.Wall;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * The class for pac man.
 */
public class Man extends MovingObject implements PropertyChangeListener, Typeable {
    private int lives;
    private int score;
    private int level;
    private String type;
    private transient GameContext gameContext;
    private int eatenNum;
    private boolean nextLevel;

    /**
     * The constructor.
     *
     * @param loc    Initial location.
     * @param vel    Initial velocity.
     * @param dir    Initial direction.
     * @param radius Initial radius.
     * @param lives  The lives remaining.
     */
    public Man(Point loc, int vel, Point dir, int radius, int lives) {
        super(loc, vel, dir, radius);
        this.lives = lives;
        this.type = "man";
        this.score = 0;
        this.level = 1;
    }

    @Override
    public String getType() {
        return type;
    }

    /**
     * set the lives of the object.
     *
     * @param lives lives of the object
     */
    public void setLives(int lives) {
        this.lives = lives;
    }

    /**
     * get the lives of the object.
     *
     * @return lives
     */
    public int getLives() {
        return lives;
    }

    /**
     * reduce the lives of the object.
     */
    public void reduceLive() {
        lives = lives - 1;
    }

    /**
     * set the score of the object.
     *
     * @param score score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * get the score of the object.
     *
     * @return score
     */
    public int getScore() {
        return score;
    }

    /**
     * set level of current game.
     *
     * @param level level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * get level of the game.
     *
     * @return level
     */
    public int getLevel() {
        return level;
    }

    /**
     * add the new score.
     *
     * @param newScore new score
     */
    public void addScore(int newScore) {
        score = newScore + score;
    }

    /**
     * set the game context.
     *
     * @param gameContext game context
     */
    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    /**
     * get the game context.
     *
     * @return game context
     */
    public GameContext getGameContext() {
        return gameContext;
    }

    /**
     * Update man's position when an update event is fired.
     *
     * @param evt The event.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        ((IPacManCmd) (evt.getNewValue())).execute(this);
    }

    /**
     * add eaten number of the object.
     */
    public void addEatenNum() {
        eatenNum++;
    }

    /**
     * initialize the eaten number of the object.
     */
    public void initEatenNum() {
        eatenNum = 0;
    }

    /**
     * get the eaten number of the object.
     *
     * @return The number of eaten pacs
     */
    public int getEatenNum() {
        return eatenNum;
    }

    /**
     * add the level of the game.
     */
    public void addLevel() {
        level++;
    }

    /**
     * get the next level of the game.
     *
     * @return next level
     */
    public boolean getNextLevel() {
        return nextLevel;
    }

    /**
     * set the next level.
     *
     * @param flag next level
     */
    public void setNextLevel(boolean flag) {
        nextLevel = flag;
    }

    /**
     * reset the location and direction of the object.
     */
    public void resetMan() {
        this.setLoc(new Point(getInitialPosition().x, getInitialPosition().y));
        this.setDir(new Point(0, -1));
    }
}
