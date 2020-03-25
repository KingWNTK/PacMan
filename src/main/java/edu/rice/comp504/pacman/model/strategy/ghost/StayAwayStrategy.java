package edu.rice.comp504.pacman.model.strategy.ghost;


import edu.rice.comp504.pacman.model.GameContext;
import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;
import edu.rice.comp504.pacman.model.strategy.IUpdateStrategy;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * The ghost will travel away the ghost,
 * And at the same time, the color will be dark blue.
 * Here i use the Manhattan Distance to calculate
 */

public class StayAwayStrategy implements IUpdateStrategy {
    private transient GameContext gameContext;

    /**
     * update the status of the ghost.
     *
     * @param context A ghost
     * @param man     man
     */
    @Override
    public void update(Ghost context, Man man) {
        if (gameContext.isStayAwayActive()) {
            gameContext.increaseStayAwayTimer();
            int timer = gameContext.getStayAwayTimer();

            if (timer == gameContext.getStayAwayTime()) {
                this.initializeGhost(gameContext);
                gameContext.init();

            } else if (timer == gameContext.getStayAwayFlashingTime()) {
                this.setFlash(gameContext);

            } else {
                this.move(context);

            }
        }
    }

    /**
     * set the flash of the moving object.
     *
     * @param gameContext moving object
     */
    public void setFlash(GameContext gameContext) {
        for (int i = 0; i < gameContext.getGhosts().size(); i++) {
            // 1. if the ghost is now stay away
            // 2. if the ghost is not dead
            // then set it flashing
            if (gameContext.getGhosts().get(i).getStayAway() && !gameContext.getGhosts().get(i).getIsDead()) {
                gameContext.getGhosts().get(i).setStayAway(false);
                gameContext.getGhosts().get(i).setFlashing(true);
            }
        }
    }

    /**
     * initialize the moving object.
     *
     * @param gameContext moving object
     */
    public void initializeGhost(GameContext gameContext) {
        for (int i = 0; i < gameContext.getGhosts().size(); i++) {
            gameContext.getGhosts().get(i).setStayAway(false);
            gameContext.getGhosts().get(i).setFlashing(false);
            if (!gameContext.getGhosts().get(i).getUpdateStrategy().getType().equals("BeforeBirthStrategy") &&
                    !gameContext.getGhosts().get(i).getUpdateStrategy().getType().equals("AfterDieStrategy")) {
                gameContext.getGhosts().get(i).setUpdateStrategy(gameContext.getGhosts().get(i).getNextStrategy());
            }
        }
    }

    /**
     * return StayAwayStrategy.
     *
     * @param gameContext moving object
     * @return StayAwayStrategy
     */
    public static StayAwayStrategy getStrategy(GameContext gameContext) {
        return new StayAwayStrategy(gameContext);
    }

    private StayAwayStrategy(GameContext gameContext) {
        this.gameContext = gameContext;
        gameContext.setStayAwayActive(true);
    }


    @Override
    public String getType() {
        return "StayAwayStrategy";
    }

    /**
     * change direction.
     *
     * @param context ghost
     */
    public void changeDir(Ghost context) {
        Point dir = context.getDir();
        Point chooseDir = null;
        int curManhattanDis = Math.abs(context.getLoc().x - gameContext.getMan().getLoc().x) + Math.abs(context.getLoc().y - gameContext.getMan().getLoc().y);
        for (int i = 0; i < gameContext.getDirs().length - 1; i++) {
            int newX = context.getLoc().x / context.getRadius() + gameContext.getDirs()[i];
            int newY = context.getLoc().y / context.getRadius() + gameContext.getDirs()[i + 1];
            Point newLoc = new Point(newX, newY);
            int newDis = Math.abs(context.getLoc().x + gameContext.getDirs()[i] - gameContext.getMan().getLoc().x) + Math.abs(context.getLoc().y + gameContext.getDirs()[i + 1] - gameContext.getMan().getLoc().y);
            if (context.getPathArr()[newLoc.x][newLoc.y] == 1 && newDis >= curManhattanDis) {
                curManhattanDis = newDis;
                chooseDir = new Point(gameContext.getDirs()[i], gameContext.getDirs()[i + 1]);
            }
        }
        if (chooseDir == null) {
            context.setDir(dir);
        } else {
            context.setDir(chooseDir);
        }
    }

    /**
     * move the object.
     *
     * @param context moving object
     */
    public void move(Ghost context) {
        int step = context.getVel();
        int curManhattanDis = Math.abs(context.getLoc().x - gameContext.getMan().getLoc().x) + Math.abs(context.getLoc().y - gameContext.getMan().getLoc().y);

        while (step > 0) {
            if (context.getLoc().x % context.getRadius() != 0 | context.getLoc().y % context.getRadius() != 0) {
                context.moveOneStep();
                step = step - 1;
                continue;
            }

            if (curManhattanDis <= gameContext.getStayAwayManhattan()) {
                this.changeDir(context);
            } else {
                List<Point> possDir = new ArrayList<>();
                for (int i = 0; i < gameContext.getDirs().length - 1; i++) {
                    Point newLoc = new Point(context.getLoc().x / context.getRadius() + gameContext.getDirs()[i], context.getLoc().y / context.getRadius() + gameContext.getDirs()[i + 1]);
                    boolean isOpposite = (gameContext.getDirs()[i] == -context.getDir().x && gameContext.getDirs()[i + 1] == -context.getDir().y);
                    if (context.getPathArr()[newLoc.x][newLoc.y] == 1 && !isOpposite) {
                        possDir.add(new Point(gameContext.getDirs()[i], gameContext.getDirs()[i + 1]));
                    }
                }

                if (possDir.size() == 0) {
                    // no way to go, go back to the original way
                    context.setDir(new Point(-context.getDir().x, -context.getDir().y));
                } else if (possDir.size() == 1) {
                    context.setDir(possDir.get(0));
                } else {
                    // there are more than one directions that you can choose for next step
                    // you need to choose an element randomly
                    int randomIndex = (int) (Math.random() * possDir.size());
                    context.setDir(possDir.get(randomIndex));
                }
            }

            context.moveOneStep();
            step = step - 1;
        }
    }

    @Override
    public void setNextStrategy(Ghost context, IUpdateStrategy nextStrategy) {

    }

    @Override
    public int getTimer() {
        return 0;
    }
}
