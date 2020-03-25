package edu.rice.comp504.pacman.model.strategy.ghost;

import edu.rice.comp504.pacman.model.GameContext;
import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;
import edu.rice.comp504.pacman.model.strategy.IUpdateStrategy;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The movement ghost should perform before they are born.
 */
public class BeforeBirthStrategy implements IUpdateStrategy {
    private static BeforeBirthStrategy only = null;

    /**
     * update the status of the ghost.
     *
     * @param context A ghost
     * @param man     man
     */
    @Override
    public void update(Ghost context, Man man) {
        context.addliveClock();
        if (context.getLiveClock() == context.getBirthTime()) {
            this.changeDir(context);
        }
        this.move(context);
    }

    /**
     * get instance of BeforeBirthStrategy.
     *
     * @return BeforeBirthStrategy
     */
    public static BeforeBirthStrategy getStrategy() {
        if (only == null) {
            only = new BeforeBirthStrategy();
        }
        return only;
    }

    @Override
    public String getType() {
        return "BeforeBirthStrategy";
    }

    /**
     * change the direction of the context.
     *
     * @param context ghost
     */
    public void changeDir(Ghost context) {
        context.setIsOut(true);
        if (context.getColor().equals("blue")) {
            context.setDir(new Point(1, 0));
        } else if (context.getColor().equals("pink")) {
            context.setDir(new Point(-1, 0));
        } else if (context.getColor().equals("red")) {
            context.setDir(new Point(0, 1));
        }
    }

    /**
     * move the ghost.
     *
     * @param context ghost
     */
    public void move(Ghost context) {
        int step = context.getVel();
        Point upDir = new Point(0, -1);

        GameContext gameContext = context.getGameContext();
        while (step > 0) {
            if (context.getLoc().y == 180 && context.getDir().equals(upDir)) {
                if ((gameContext.isStayAwayActive() && context.getStayAway()) ||
                        gameContext.isStayAwayActive() && gameContext.getStayAwayRoundNo() != context.getDieRound()) {
                    context.setUpdateStrategy(StayAwayStrategy.getStrategy(gameContext));

                } else {
                    context.setStayAway(false);
                    context.setUpdateStrategy(context.getNextStrategy());
                }
                context.setIsOut(true);
                break;
            }

            if (context.getLoc().x == 620 && !context.getDir().equals(upDir) && context.getIsOut()) {
                context.setDir(upDir);
                continue;
            }

            if (context.getLoc().x % context.getRadius() != 0 | context.getLoc().y % context.getRadius() != 0) {
                context.moveOneStep();
                step = step - 1;
                continue;
            }

            if (!context.getIsOut()) {
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

