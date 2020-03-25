package edu.rice.comp504.pacman.model;

import edu.rice.comp504.pacman.model.wall.Wall;
import edu.rice.comp504.pacman.model.strategy.IUpdateStrategy;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class is supposed to be extended by ghosts and man, it manages all the moving information.
 */
public abstract class MovingObject {
    private Point loc;
    private int vel;
    private Point dir;
    private int radius;
    private IUpdateStrategy strategy;
    private final Point initialPosition;
    private final Point initialDirection;

    /**
     * The constructor of moving object.
     *
     * @param loc    The initial location.
     * @param vel    The initial velocity.
     * @param dir    The initial direction.
     * @param radius The initial radius.
     */
    public MovingObject(Point loc, int vel, Point dir, int radius) {
        this.loc = loc;
        this.vel = vel;
        this.dir = dir;
        this.radius = radius;
        this.initialPosition = new Point(loc.x, loc.y);
        this.initialDirection = new Point(dir.x, dir.y);
    }

    /**
     * This function returns the location of the moving object.
     */
    public Point getLoc() {
        return loc;
    }

    /**
     * This function sets the location of the moving object.
     *
     * @param loc The location to be set.
     */
    public void setLoc(Point loc) {
        this.loc = loc;
    }

    /**
     * This function returns the velocity of the moving object.
     */
    public int getVel() {
        return vel;
    }

    /**
     * This function sets the velocity of the moving object.
     *
     * @param vel The velocity to be set.
     */
    public void setVel(int vel) {
        this.vel = vel;
    }

    /**
     * This function returns the direction of the moving object.
     */
    public Point getDir() {
        return dir;
    }

    /**
     * This function sets the direction of the moving object.
     *
     * @param dir The direction(Point object) to be set.
     */
    public void setDir(Point dir) {
        this.dir = dir;
    }

    /**
     * This function returns the radius of the moving object.
     */
    public int getRadius() {
        return radius;
    }

    /**
     * This function sets the radius of the moving object.
     *
     * @param radius The radius to be set.
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * This function returns the strategy of the moving object.
     */
    public IUpdateStrategy getStrategy() {
        return strategy;
    }

    /**
     * This function sets the strategy of the moving object.
     *
     * @param strategy The strategy to be set.
     */
    public void setStrategy(IUpdateStrategy strategy) {
        this.strategy = strategy;
    }


    /**
     * This function will make the moving object go n steps in its direction, it will stop when it cannot go ahead any more.
     *
     * @param steps           The steps the moving object plan to go.
     * @param verticalWalls   The vertical walls.
     * @param horizontalWalls The horizontal walls.
     */
    private void goToNextLoc(int steps, ArrayList<Wall> verticalWalls, ArrayList<Wall> horizontalWalls) {
        if (steps <= 0) {
            return;
        }
        loc.x += dir.x;
        loc.y += dir.y;
        if (isCollisionExist(verticalWalls, horizontalWalls)) {
            loc.x -= dir.x;
            loc.y -= dir.y;
            return;
        }
        goToNextLoc(steps - 1, verticalWalls, horizontalWalls);
    }

    /**
     * Use binary search to detect moving object's collision with walls.
     *
     * @param verticalWalls   The vertical walls.
     * @param horizontalWalls The horizontal walls.
     * @return If collision exists.
     */
    private boolean isCollisionExist(ArrayList<Wall> verticalWalls, ArrayList<Wall> horizontalWalls) {
        int pv = Collections.binarySearch(
                verticalWalls, new Wall(new Point(loc.x - radius - 1, 0), new Point(loc.x - radius - 1, 0), false, null), (a, b) -> a.getBeginLoc().x - b.getBeginLoc().x
        );
        pv = pv < 0 ? -(pv + 1) : pv;
        pv = pv > 0 ? pv - 1 : 0;
        int ph = Collections.binarySearch(horizontalWalls, new Wall(new Point(0, loc.y - radius - 1), new Point(0, loc.y - radius - 1), false, null), (a, b) -> a.getBeginLoc().y - b.getBeginLoc().y
        );
        ph = ph < 0 ? -(ph + 1) : ph;
        ph = ph > 0 ? ph - 1 : 0;

        while (pv < verticalWalls.size()) {
            if (verticalWalls.get(pv).getBeginLoc().x >= loc.x + radius) {
                break;
            }
            if (verticalWalls.get(pv).isCollidedWith(this)) {
                Point teleport = verticalWalls.get(pv).getTeleport();
                if (teleport != null) {
                    loc.x += teleport.x;
                    loc.y += teleport.y;
                } else {
                    return true;
                }
            }
            pv++;
        }
        while (ph < horizontalWalls.size()) {
            if (horizontalWalls.get(ph).getBeginLoc().y >= loc.y + radius) {
                break;
            }
            if (horizontalWalls.get(ph).isCollidedWith(this)) {
                Point teleport = horizontalWalls.get(ph).getTeleport();
                if (teleport != null) {
                    loc.x += teleport.x;
                    loc.y += teleport.y;
                } else {
                    return true;
                }
            }
            ph++;
        }
        return false;
    }

    /**
     * This function will try to change the direction of the object.
     * If the object can change its direction based on its own velocity, then return the steps it should go before it change its direction.
     * Note that this function does not update the object's data.
     *
     * @param newDir          The new direction for the object.
     * @param verticalWalls   The vertical walls.
     * @param horizontalWalls The horizontal walls.
     * @return The steps the object should go before it can change its direction.
     */
    private int tryChangeDir(Point newDir, ArrayList<Wall> verticalWalls, ArrayList<Wall> horizontalWalls) {
        //Try all the possible steps the object should go before direction change.
        for (int steps = 0; steps <= vel; steps++) {
            loc.x += this.dir.x * steps;
            loc.y += this.dir.y * steps;
            boolean canGo = !isCollisionExist(verticalWalls, horizontalWalls);
            boolean canChange = true;
            if (canGo) {
                loc.x += newDir.x;
                loc.y += newDir.y;
                canChange = !isCollisionExist(verticalWalls, horizontalWalls);
                loc.x -= newDir.x;
                loc.y -= newDir.y;
            }

            loc.x -= this.dir.x * steps;
            loc.y -= this.dir.y * steps;
            if (!canGo) {
                return -1;
            } else if (canChange) {
                return steps;
            }

        }
        return -1;
    }

    /**
     * This function will move the object according to the command made by user.
     * If the object is able to change its direction in this frame,
     * then it will go d steps based on original direction, and vel - d steps based on new Direction.
     * If the object is not able to change its direction, it will try to go vel steps until it hits a wall.
     *
     * @param newDir          The new direction received from user's command in front-end
     * @param verticalWalls   The vertical walls.
     * @param horizontalWalls The horizontal walls.
     */
    public void move(Point newDir, ArrayList<Wall> verticalWalls, ArrayList<Wall> horizontalWalls) {
        int d = tryChangeDir(newDir, verticalWalls, horizontalWalls);
        if (d >= 0) {
            loc.x += d * this.dir.x;
            loc.y += d * this.dir.y;
            this.dir = newDir;
            goToNextLoc(vel - d, verticalWalls, horizontalWalls);
        } else {
            goToNextLoc(vel, verticalWalls, horizontalWalls);
        }
    }

    /**
     * Moves the object one step to its direction.
     */
    public void moveOneStep() {
        setLoc(new Point(getLoc().x + getDir().x, getLoc().y + getDir().y));
    }

    /**
     * Check if it is collided with another moving object.
     * @param other the other moving object.
     * @return A boolean indicating whether they have collided.
     */

    public boolean isCollison(MovingObject other) {
        if (this.getLoc().x == other.getLoc().x) {
            if (Math.abs(this.getLoc().y - other.getLoc().y) <= getRadius() + other.getRadius()) {
                return true;
            }
        } else if (this.getLoc().y == other.getLoc().y) {
            if (Math.abs(this.getLoc().y - other.getLoc().y) <= getRadius() + other.getRadius()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the initial position of the moving object.
     * @return the object's initial position
     */
    public Point getInitialPosition() {
        return initialPosition;
    }

    /**
     * Get the initial direction of the moving object.
     * @return the object's initial direction
     */
    public Point getInitialDirection() {
        return initialDirection;
    }
}
