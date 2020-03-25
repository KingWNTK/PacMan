package edu.rice.comp504.pacman.model.strategy.collision;

import edu.rice.comp504.pacman.model.DispatchAdapter;
import edu.rice.comp504.pacman.model.GameContext;
import edu.rice.comp504.pacman.model.Typeable;
import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;
import edu.rice.comp504.pacman.model.pac.Pac;
import edu.rice.comp504.pacman.model.strategy.ICollisionStrategy;
import edu.rice.comp504.pacman.model.strategy.ghost.StayAwayStrategy;

import java.awt.*;
import java.util.ArrayList;

/**
 * The strategy to calculate the score for pac man.
 */
public class ScoreStrategy implements ICollisionStrategy {
    private static ScoreStrategy only = null;

    /**
     * The singleton constructor.
     *
     * @return The only singleton.
     */
    public static ScoreStrategy getStrategy() {
        if (only == null) {
            only = new ScoreStrategy();
        }
        return only;
    }

    /**
     * Set the walls to white.
     *
     * @param gameContext The game context
     */
    public void setWallWhite(GameContext gameContext) {
        for (int i = 0; i < gameContext.getHorizontalWalls().size(); i++) {
            gameContext.getHorizontalWalls().get(i).setColor("white");
        }

        for (int j = 0; j < gameContext.getVerticalWalls().size(); j++) {
            gameContext.getVerticalWalls().get(j).setColor("white");
        }

        gameContext.resetWallTimer();
    }

    /**
     * Set the ghost to stay away.
     *
     * @param gameContext The game context
     */
    public void turnStayAway(GameContext gameContext) {
        for (int i = 0; i < gameContext.getGhosts().size(); i++) {

            if (gameContext.getGhosts().get(i).getUpdateStrategy().getType().equals("BeforeBirthStrategy")) {
                if (gameContext.getGhosts().get(i).getDieRound() != gameContext.getStayAwayRoundNo()) {
                    gameContext.getGhosts().get(i).setStayAway(true);
                    gameContext.getGhosts().get(i).setDieRound(gameContext.getStayAwayRoundNo());
                    gameContext.getGhosts().get(i).setFlashing(false);
                }
            } else if (!gameContext.getGhosts().get(i).getUpdateStrategy().getType().equals("BeforeBirthStrategy") &&
                    !gameContext.getGhosts().get(i).getUpdateStrategy().getType().equals("AfterDieStrategy")) {
                gameContext.getGhosts().get(i).setUpdateStrategy(StayAwayStrategy.getStrategy(gameContext));
                gameContext.getGhosts().get(i).setDieRound(gameContext.getStayAwayRoundNo());
                gameContext.getGhosts().get(i).setStayAway(true);
                gameContext.getGhosts().get(i).setFlashing(false);
            }
        }
    }

    /**
     * Reset the pacs.
     *
     * @param gameContext The game context
     */
    public void resetPacs(GameContext gameContext) {
        for (int i = 0; i < gameContext.getPacs().size(); i++) {
            gameContext.getDa().removeListener(gameContext.getPacs().get(i));
            gameContext.getPacs().get(i).setEaten(false);
        }
        for (int i = 0; i < gameContext.getPacs().size(); i++) {
            gameContext.getDa().addListener(gameContext.getPacs().get(i));
        }
    }

    /**
     * Set ghost velocity according to current level.
     *
     * @param level       Current level
     * @param gameContext The game context
     */
    public void ghostToNextLevel(int level, GameContext gameContext) {
        for (int i = 0; i < gameContext.getGhosts().size(); i++) {
            gameContext.getGhosts().get(i).setVel(gameContext.getGhostInitialVel() + level * gameContext.getVelAdd());
            gameContext.getGhosts().get(i).setInitialVel(gameContext.getGhostInitialVel() + level * gameContext.getVelAdd());
        }
    }

    @Override
    public void interact(Typeable context, Man man, ArrayList<Ghost> ghosts) {
        if (context.getType().equals("pac")) {
            Pac pac = (Pac) context;
            if (pac.isCollidedWith(man)) {
                man.setScore(man.getScore() + pac.getScore());
                pac.setEaten(true);
                GameContext gameContext = pac.getGameContext();
                man.addEatenNum();

                if (man.getEatenNum() == man.getGameContext().getPacs().size()) {
                    // Eat all the pacs!! move to next level !
                    man.addLevel();
                    initGhosts(gameContext);
                    resetPacs(gameContext);
                    man.setLoc(new Point(man.getInitialPosition().x, man.getInitialPosition().y));
                    man.setNextLevel(true);
                    man.initEatenNum();
                    gameContext.init();
                    ghostToNextLevel(man.getLevel(), gameContext);
                    gameContext.setLevel(man.getLevel());
                    setWallWhite(gameContext);
                    return;
                }

                if (pac.getRadius() == 10 && !pac.getType().equals("fruit")) {
                    // each time when the big dot met with the ghost
                    // the set the staytimer
                    gameContext.init();
                    gameContext.setStayAwayActive(true);
                    gameContext.increaseStayAwayRoundNo();
                    turnStayAway(gameContext);
                }
            }
        }
        if (context.getType().equals("fruit")) {
            Pac fruit = (Pac) context;
            if (fruit.isCollidedWith(man)) {
                man.setScore(man.getScore() + fruit.getScore());
                man.getGameContext().getDa().removeListener(fruit);
                man.getGameContext().initFruitContext();
            }
        }

    }

    /**
     * Rebirth all the ghost.
     *
     * @param gameContext The game context
     */
    public void initGhosts(GameContext gameContext) {
        for (int i = 0; i < gameContext.getGhosts().size(); i++) {
            gameContext.getGhosts().get(i).reBirth();
        }
    }

    @Override
    public String getType() {
        return "ScoreStrategy";
    }
}
