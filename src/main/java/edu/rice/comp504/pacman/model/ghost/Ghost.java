package edu.rice.comp504.pacman.model.ghost;

import edu.rice.comp504.pacman.model.DispatchAdapter;
import edu.rice.comp504.pacman.model.GameContext;
import edu.rice.comp504.pacman.model.MovingObject;
import edu.rice.comp504.pacman.model.Typeable;
import edu.rice.comp504.pacman.model.cmd.IPacManCmd;
import edu.rice.comp504.pacman.model.strategy.ICollisionStrategy;
import edu.rice.comp504.pacman.model.strategy.IUpdateStrategy;
import edu.rice.comp504.pacman.model.strategy.ghost.*;
import edu.rice.comp504.pacman.model.wall.Wall;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * AGhost is the abstract class for all the ghosts.
 */

public class Ghost extends MovingObject implements PropertyChangeListener, Typeable {
    public transient int[][] pathArr;

    private String type;
    private int level;
    private String color;
    private transient IUpdateStrategy updateStrategy;
    private transient IUpdateStrategy nextStrategy;
    private int birthTime;
    private boolean isStayAway;  // use this strategy, if the session does not expire
    private boolean isFlashing;  // if the session is flashing
    private transient boolean isOut;
    private boolean isDead;      // if ghost is eaten by the man, then set it as dead
    private transient int liveClock;
    private transient int dieRound;
    private int dieScore;        // if it equals to zero, the ghost is not eaten
    private transient int initialVel;
    private transient int dieVel;
    private transient GameContext gameContext;


    /**
     * The constructor.
     *
     * @param loc      Initial location.
     * @param vel      Initial velocity.
     * @param dir      Initial dir.
     * @param radius   Initial radius.
     * @param color    The ghost color.
     * @param strategy The initial strategy.
     */
    public Ghost(Point loc, int vel, Point dir, int radius, String color, IUpdateStrategy strategy, int[][] pathArr) {
        super(loc, vel, dir, radius);
        this.updateStrategy = strategy;
        this.type = "ghost";
        this.level = 1;
        this.color = color;
        this.liveClock = 0;
        this.isOut = false;
        this.isStayAway = false;
        this.initialVel = vel;
        this.pathArr = pathArr;
        setDieVel();

        if (this.color.equals("blue")) {
            this.birthTime = 20;
            this.nextStrategy = AmbushStrategy.getStrategy();
        } else if (this.color.equals("pink")) {
            this.birthTime = 40;
            this.nextStrategy = ChaseManStrategy.getStrategy();
        } else if (this.color.equals("red")) {
            this.birthTime = 60;
            this.nextStrategy = IntersectStrategy.getStrategy();
        } else {
            this.birthTime = 0;
            this.nextStrategy = RandomWalkStrategy.getStrategy();
        }
    }

    /**
     * Get ghost's next strategy.
     *
     * @return The ghost's next strategy
     */
    public IUpdateStrategy getNextStrategy() {
        return nextStrategy;
    }

    @Override
    public String getType() {
        return type;
    }

    /**
     * Set the current update strategy.
     *
     * @param updateStrategy The ghost's update strategy
     */
    public void setUpdateStrategy(IUpdateStrategy updateStrategy) {
        this.updateStrategy = updateStrategy;
    }

    /**
     * Get the current update strategy.
     *
     * @return The ghost's update strategy
     */
    public IUpdateStrategy getUpdateStrategy() {
        return updateStrategy;
    }

    /**
     * Get the level.
     *
     * @return The ghost's level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Set the level.
     *
     * @param level The ghost's level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Get the game context.
     *
     * @return The game context
     */
    public GameContext getGameContext() {
        return gameContext;
    }

    /**
     * Set the game context.
     *
     * @param gameContext The game context
     */
    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    /**
     * Check if the ghost is collided with another moving object.
     *
     * @param object The other moving object.
     * @return If they are collided.
     */
    public boolean isCollidedWith(MovingObject object) {
        return Math.pow(getLoc().x - object.getLoc().x, 2) + Math.pow(getLoc().y - object.getLoc().y, 2) < Math.pow(getRadius() + object.getRadius(), 2);
    }

    /**
     * Get the birth time.
     *
     * @return The birth time
     */
    public int getBirthTime() {
        return this.birthTime;
    }

    /**
     * Implements the property change listener function.
     *
     * @param evt the event.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        ((IPacManCmd) (evt.getNewValue())).execute(this);
    }

    /**
     * Get the color.
     *
     * @return The ghost's color
     */
    public String getColor() {
        return color;
    }

    /**
     * Get the live clock.
     *
     * @return live clock
     */
    public int getLiveClock() {
        return liveClock;
    }

    /**
     * Increase the live clock by 1.
     */
    public void addliveClock() {
        liveClock = liveClock + 1;
    }

    /**
     * Initiate the live lock to 0.
     */
    public void initliveClock() {
        liveClock = 0;
    }

    /**
     * Get the out flag.
     *
     * @return The out flag
     */
    public boolean getIsOut() {
        return isOut;
    }

    /**
     * Set the out flag.
     *
     * @param flag The out flag
     */
    public void setIsOut(boolean flag) {
        isOut = flag;
    }

    /**
     * Set the stay away flag.
     *
     * @param stayAway The stay away flag
     */
    public void setStayAway(boolean stayAway) {
        isStayAway = stayAway;
    }

    /**
     * Get the stay away flag.
     *
     * @return The stay away flag
     */
    public boolean getStayAway() {
        return isStayAway;
    }

    /**
     * Get the flashing flag.
     *
     * @return The flashing flags
     */
    public boolean getFlashing() {
        return isFlashing;
    }

    /**
     * Set the flashing flag
     *
     * @param flashing The flashing flag.
     */
    public void setFlashing(boolean flashing) {
        isFlashing = flashing;
    }

//    public void setNextStrategy(IUpdateStrategy nextStrategy) {
//        this.nextStrategy = nextStrategy;
//    }

    /**
     * Set the die round.
     *
     * @param round The die round
     */
    public void setDieRound(int round) {
        this.dieRound = round;
    }

    /**
     * Get the die round.
     *
     * @return The die round
     */
    public int getDieRound() {
        return this.dieRound;
    }

    /**
     * Set the dead flag.
     *
     * @param isDead The dead flag
     */
    public void setIsDead(boolean isDead) {
        this.isDead = isDead;
    }

    /**
     * Get the dead flag.
     *
     * @return The dead flag
     */
    public boolean getIsDead() {
        return this.isDead;
    }

    /**
     * Make the ghost rebirth after being eaten by Pacman.
     */
    public void reBirth() {
        Point initialPosition = this.getInitialPosition();
        Point initialDirection = this.getInitialDirection();
        this.setLoc(new Point(initialPosition.x, initialPosition.y));
        this.setDir(new Point(initialDirection.x, initialDirection.y));
        this.setUpdateStrategy(BeforeBirthStrategy.getStrategy());
        this.initliveClock();
        this.setIsOut(false);
        this.setStayAway(false);
        this.setIsDead(false);
        this.setFlashing(false);
        this.setDieVel();
        this.setVel(initialVel);
    }


    public void setInitialVel(int vel) {
        this.initialVel = vel;
    }

    /**
     * Get the die score.
     *
     * @return The die score
     */
    public int getDieScore() {
        return dieScore;
    }


    /**
     * Set the die score.
     *
     * @param newDieScore The die score
     */
    public void setDieScore(int newDieScore) {
        dieScore = newDieScore;
    }

    /**
     * Get the die level.
     *
     * @return The die level
     */
    public int getDieVel() {
        return dieVel;
    }

    /**
     * Make the die level increase by 50.
     */
    public void setDieVel() {
        dieVel = this.getVel() + 50;
    }

    /**
     * Get the preprocessed paths array.
     *
     * @return The paths array
     */
    public int[][] getPathArr() {
        return pathArr;
    }

    /**
     * Match the point to the grid.
     *
     * @param p          The point
     * @param gridPoints The grid points
     * @return The result
     */
    private Point matchToGrid(Point p, Set<Point> gridPoints) {
        Point res = null;
        double minDis = 1 << 30;
        for (Point g : gridPoints) {
            if (p.distance(g) < minDis) {
                minDis = p.distance(g);
                res = g;
            }
        }
        return res;
    }

    /**
     * Make the ghost move towards the assigned position.
     *
     * @param target The target position
     */
    public void goTo(Point target) {
        HashMap<Point, HashMap<Point, Point>> allPath = gameContext.getAllPath();

        int initialVel = getVel();
        int used = 0;

        while (used < initialVel) {
            int delta = Math.min(15, initialVel - used);
            setVel(delta);

            Point tar = matchToGrid(target, allPath.keySet());
            Point cur = matchToGrid(getLoc(), allPath.keySet());
            HashMap<Point, Point> path = allPath.get(cur);

            ArrayList<Point> curRoute = new ArrayList<>();

            curRoute.add(tar);
            Point nxt = path.get(tar);
            while (nxt != null) {
                curRoute.add(nxt);
                nxt = path.get(nxt);
            }

            curRoute.remove(curRoute.size() - 1);

            if (curRoute.size() == 0) {
                return;
            }

            Point nxtPos = curRoute.get(curRoute.size() - 1);
            Point dir = new Point((nxtPos.x - cur.x) / 20, (nxtPos.y - cur.y) / 20);
            curRoute.remove(curRoute.size() - 1);

            int dis = Math.max(nxtPos.x - getLoc().x, nxtPos.y - getLoc().y);

            while (curRoute.size() > 0 && dir.equals(getDir()) && dis < getVel()) {
                cur = nxtPos;
                nxtPos = curRoute.get(curRoute.size() - 1);
                dir = new Point((nxtPos.x - cur.x) / 20, (nxtPos.y - cur.y) / 20);
                dis += 20;
                curRoute.remove(curRoute.size() - 1);
            }

            move(dir, gameContext.getVerticalWalls(), gameContext.getHorizontalWalls());
            used += delta;
        }

        setVel(initialVel);


    }


    @Override
    public void moveOneStep() {
//        super.moveOneStep();
        int s = getVel();
        setVel(1);
        move(getDir(), gameContext.getVerticalWalls(), gameContext.getHorizontalWalls());
        setVel(s);
    }

    /**
     * Make the ghosts move in a random way.
     * The function is similar to the function in the random strategy.
     */
    public void randomMove() {
        int step = this.getVel();
        GameContext gameContext = this.getGameContext();
        while (step > 0) {
            if (this.getLoc().x % this.getRadius() != 0 | this.getLoc().y % this.getRadius() != 0) {
                this.moveOneStep();
                step = step - 1;
                continue;
            }

            List<Point> possDir = new ArrayList<>();
            for (int i = 0; i < gameContext.getDirs().length - 1; i++) {
                Point newLoc = new Point(this.getLoc().x / this.getRadius() + gameContext.getDirs()[i], this.getLoc().y / this.getRadius() + gameContext.getDirs()[i + 1]);
                boolean isOpposite = (gameContext.getDirs()[i] == -this.getDir().x && gameContext.getDirs()[i + 1] == -this.getDir().y);
                if (this.getPathArr()[newLoc.x][newLoc.y] == 1 && !isOpposite) {
                    possDir.add(new Point(gameContext.getDirs()[i], gameContext.getDirs()[i + 1]));
                }
            }

            if (possDir.size() == 0) {
                // no way to go, go back to the original way
                this.setDir(new Point(-this.getDir().x, -this.getDir().y));
            } else if (possDir.size() == 1) {
                this.setDir(possDir.get(0));
            } else {
                // there are more than one directions that you can choose for next step
                // you need to choose an element randomly
                int randomIndex = (int) (Math.random() * possDir.size());
                this.setDir(possDir.get(randomIndex));
            }

            this.moveOneStep();
            step = step - 1;
        }
    }
}
