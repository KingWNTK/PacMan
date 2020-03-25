package edu.rice.comp504.pacman.model.wall;

import edu.rice.comp504.pacman.model.MovingObject;
import edu.rice.comp504.pacman.model.Typeable;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The wall object represents the walls on the map.
 */
public class Wall implements PropertyChangeListener, Typeable {
    private transient Point beginLoc;
    private transient Point endLoc;
    private transient boolean passable;
    private String color;
    private String type;
    private transient Point teleport;
    private transient String initialColor;
    private String p;

    /**
     * The constructor.
     *
     * @param beginLoc The begin location of the wall.
     * @param endLoc   The end location of the wall.
     * @param passable If the wall is passable.
     * @param color    The color of the wall.
     */
    public Wall(Point beginLoc, Point endLoc, boolean passable, String color) {
        this.beginLoc = beginLoc;
        this.endLoc = endLoc;
        this.passable = passable;
        this.type = "wall";
        this.color = color;
        this.initialColor = color;
        this.teleport = null;
        this.p = "" + beginLoc.x / 20 + "-" + beginLoc.y / 20 + "-" + endLoc.x / 20 + "-" + endLoc.y / 20;
    }

    @Override
    public String getType() {
        return type;
    }

    /**
     * Get begin location.
     * @return begin location.
     */
    public Point getBeginLoc() {
        return beginLoc;
    }

    /**
     * Get end location.
     * @return end location.
     */
    public Point getEndLoc() {
        return endLoc;
    }

    /**
     * Set Passable value.
     */
    public void setPassable(boolean passable) {
        this.passable = passable;
    }

    /**
     * Get if passable or not.
     * @return if is passable.
     */
    public boolean isPassable() {
        return passable;
    }

    /**
     * Set teleport to connected wall.
     * @param connectedWall connection wall
     */
    public void setTeleport(Point connectedWall) {
        this.teleport = connectedWall;
    }

    /**
     * Get teleport location.
     * @return teleport location.
     */
    public Point getTeleport() {
        return teleport;
    }

    /**
     * Check if two one dimensional segments are intersected.
     *
     * @param a Segment a.
     * @param b Segment b.
     * @return If they are intersected.
     */
    private boolean isSegmentIntersected(Point a, Point b) {
        return a.x < b.x ? a.y > b.x : b.y > a.x;
    }

    /**
     * Check if the wall is collided with a moving object.
     *
     * @param object The moving object
     * @return If they are collided
     */
    public boolean isCollidedWith(MovingObject object) {
        if (((Typeable) object).getType().equals("ghost") && isPassable()) {
            return false;
        }
        boolean ret = true;
        Point objLoc = object.getLoc();
        int r = object.getRadius();
        if (getBeginLoc().x == getEndLoc().x) {
            //The wall is vertical.
            ret &= Math.abs(objLoc.x - beginLoc.x) < r;
            ret &= isSegmentIntersected(new Point(objLoc.y - r, objLoc.y + r), new Point(Math.min(beginLoc.y, endLoc.y), Math.max(beginLoc.y, endLoc.y)));
        } else {
            //The wall is horizontal.
            ret &= Math.abs(objLoc.y - beginLoc.y) < r;
            ret &= isSegmentIntersected(new Point(objLoc.x - r, objLoc.x + r), new Point(Math.min(beginLoc.x, endLoc.x), Math.max(beginLoc.x, endLoc.x)));
        }

        return ret;
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    /**
     * Set color to initial color.
     */
    public void resetColor() {
        this.color = initialColor;
    }

    /**
     * Set color to a given color.
     * @param color the color to be set
     */
    public void setColor(String color) {
        this.color = color;
    }


}
