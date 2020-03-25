package edu.rice.comp504.pacman.model;

import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;
import edu.rice.comp504.pacman.model.pac.Pac;
import edu.rice.comp504.pacman.model.strategy.collision.ScoreStrategy;
import edu.rice.comp504.pacman.model.wall.Wall;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The game context contains the game information.
 */
public class GameContext {
    private static final int WALLLEVELTIME = 20;
    private DispatchAdapter da;
    private Man man;
    private ArrayList<Ghost> ghosts;
    private ArrayList<Pac> pacs;
    private ArrayList<Wall> verticalWalls;
    private ArrayList<Wall> horizontalWalls;
    private HashMap<Point, HashMap<Point, Point>> allPath;
    private static final int[] dirs = {0, 1, 0, -1, 0}; // [0,1],[1,0],[0,-1],[-1,0]
    private final int darkBlueTime = 180;
    private final int flashingTime = 260;
    private final int initialScore = 200;
    private final int manhattan = 100;
    private final int ghostInitialVel = 10;
    private final int velAdd = 3;
    private int wallTimer = 0;
    private final String[] fruitName = {"banana", "durian", "pineapple", "strawberry", "watermelon"};

    //0: counting down to generate fruit, 1: counting down to disappear
    private int fruitStatus;
    private int fruitCD;
    private Pac fruit;


    private int stayAwayTimer;
    private int stayAwayTime;
    private int stayAwayFlashingTime;
    private boolean stayAwayActive;
    private int stayAwayGhostScore;
    private final int stayAwayManhattan = 200;
    private int stayAwayRoundNo;

    /**
     * GameContext constructor.
     *
     * @param da              The dispatch adapter
     * @param man             The pac-man
     * @param ghosts          The ghosts
     * @param pacs            The pacs
     * @param verticalWalls   The vertical walls
     * @param horizontalWalls The horizontal walls
     * @param allPath         All of the preprocessed path.
     */
    public GameContext(DispatchAdapter da, Man man, ArrayList<Ghost> ghosts, ArrayList<Pac> pacs, ArrayList<Wall> verticalWalls, ArrayList<Wall> horizontalWalls, HashMap<Point, HashMap<Point, Point>> allPath) {
        this.da = da;
        this.man = man;
        this.ghosts = ghosts;
        this.pacs = pacs;
        this.verticalWalls = verticalWalls;
        this.horizontalWalls = horizontalWalls;
        this.allPath = allPath;
        init();
        initFruitContext();
    }

    /**
     * Initialize the starting context.
     */
    public void init() {
        resetStayAwayContext();
        initWall();
//        stayAwayManhattan = 100;
    }

    /**
     * Initialize the walls.
     */
    public void initWall() {
        wallTimer = 0;
    }

    /**
     * Update the fruit status.
     */
    public void update() {
        fruitCD--;
        if (fruitCD == 0) {
            int s = fruitStatus;
            da.removeListener(fruit);
            initFruitContext();
            if (s == 0) {
                fruitStatus = 1;
                da.addListener(fruit);
            }
        }
    }

    /**
     * Get the added velocity.
     *
     * @return added velocity
     */
    public int getVelAdd() {
        return velAdd;
    }

    /**
     * Get the Dispatch Adapter.
     *
     * @return the Dispatch Adapter
     */
    public DispatchAdapter getDa() {
        return da;
    }

    /**
     * Get the Man object.
     *
     * @return the man object
     */
    public Man getMan() {
        return man;
    }

    /**
     * Get an array of ghosts.
     *
     * @return an ArrayList containing the ghosts
     */
    public ArrayList<Ghost> getGhosts() {
        return ghosts;
    }

    /**
     * Get an array of Pacs.
     *
     * @return an ArrayList containing the pacs
     */
    public ArrayList<Pac> getPacs() {
        return pacs;
    }

    /**
     * Get an array of vertical walls.
     *
     * @return an ArrayList containing the vertical walls
     */
    public ArrayList<Wall> getVerticalWalls() {
        return verticalWalls;
    }

    /**
     * Get an array of horizontal walls.
     *
     * @return an ArrayList containing the horizontal walls
     */
    public ArrayList<Wall> getHorizontalWalls() {
        return horizontalWalls;
    }

    /**
     * Get all the paths.
     *
     * @return an HashMap containing all the paths
     */
    public HashMap<Point, HashMap<Point, Point>> getAllPath() {
        return allPath;
    }

    /**
     * Get an array containing directions.
     *
     * @return an array containing the directions
     */
    public static int[] getDirs() {
        return dirs;
    }

    /**
     * Get the ghost's initial velocity.
     *
     * @return int initial velocity
     */
    public int getGhostInitialVel() {
        return ghostInitialVel;
    }

    /**
     * Set level statistics.
     */
    public void setLevel(int level) {
        stayAwayTime = Math.max(flashingTime - level * 20, 80);
        stayAwayFlashingTime = Math.max(darkBlueTime - level * 20, 60);
    }

    /**
     * Reset level statistics.
     */
    public void resetStayAwayContext() {
        stayAwayTimer = 0;
        stayAwayActive = false;
        stayAwayGhostScore = 200;
    }

    /**
     * Initialize the fruit statistics.
     */
    public void initFruitContext() {
        fruitStatus = 0;
        fruitCD = 50;
        fruit = new Pac(new Point(man.getInitialPosition().x, man.getInitialPosition().y), ScoreStrategy.getStrategy());
        fruit.setScore(100);
        fruit.setType("fruit");
        setRandomFruit();
    }

    /**
     * Set fruit randomly.
     */
    public void setRandomFruit() {
        double index = Math.random() * 5;
        fruit.setFruitName(fruitName[(int) index]);
    }

    /**
     * Get stay away time.
     *
     * @return stay away time
     */
    public int getStayAwayTime() {
        return stayAwayTime;
    }

    /**
     * Get stay away flashing time.
     *
     * @return stay away flashing time
     */
    public int getStayAwayFlashingTime() {
        return stayAwayFlashingTime;
    }

    /**
     * Get stay away timer.
     *
     * @return stay away timer
     */
    public int getStayAwayTimer() {
        return stayAwayTimer;
    }

    /**
     * increase stay away timer.
     */
    public void increaseStayAwayTimer() {
        this.stayAwayTimer++;
    }

    /**
     * get whether StayAway is active.
     *
     * @return whether StayAway is active
     */
    public boolean isStayAwayActive() {
        return stayAwayActive;
    }

    /**
     * Set StayAway activity.
     */
    public void setStayAwayActive(boolean stayAwayActive) {
        this.stayAwayActive = stayAwayActive;
    }

    /**
     * Get StayAway Manhattan distance.
     *
     * @return StayAway Manhattan distance
     */
    public int getStayAwayManhattan() {
        return stayAwayManhattan;
    }

    /**
     * Get StayAway Round Number.
     *
     * @return StayAway Round Number
     */
    public int getStayAwayRoundNo() {
        return stayAwayRoundNo;
    }

    /**
     * Increase StayAway Round Number.
     */
    public void increaseStayAwayRoundNo() {
        this.stayAwayRoundNo++;
    }

    /**
     * Get StayAway ghost score.
     *
     * @return StayAway Ghost Score
     */
    public int getStayAwayGhostScore() {
        return stayAwayGhostScore;
    }

    /**
     * Double the StayAway ghost score.
     */
    public void doubleStayAwayGhostScore() {
        this.stayAwayGhostScore = 2 * this.stayAwayGhostScore;
    }

    /**
     * Get Wall level time.
     *
     * @return Wall level time
     */
    public static int getWallleveltime() {
        return WALLLEVELTIME;
    }

    /**
     * Get Wall timer.
     *
     * @return Wall timer
     */
    public int getWallTimer() {
        return wallTimer;
    }

    /**
     * Increase the Wall timer.
     */
    public void addWallTimer() {
        wallTimer++;
    }

    /**
     * Reset the Wall timer.
     */
    public void resetWallTimer() {
        wallTimer = 0;
    }
}

