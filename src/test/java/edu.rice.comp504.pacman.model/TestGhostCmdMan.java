package edu.rice.comp504.pacman.model;

import edu.rice.comp504.pacman.model.cmd.InteractCmd;
import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;
import edu.rice.comp504.pacman.model.strategy.ghost.AmbushStrategy;
import edu.rice.comp504.pacman.model.strategy.ghost.BeforeBirthStrategy;
import edu.rice.comp504.pacman.model.wall.Wall;
import junit.framework.TestCase;

import java.awt.*;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class TestGhostCmdMan extends TestCase{

    /**
     * Test Most Ghost Functions.
     */
    public void testGhost() {
        DispatchAdapter da = new DispatchAdapter(0);
        da.init();
        GameContext gc = da.getGameContext();
        ArrayList<Ghost> ghs = gc.getGhosts();

        assertEquals("number of ghosts", 4, ghs.size());

        for (Ghost a : ghs) {
            if (a.getColor().equals("blue")) {
                assertEquals("birth time", a.getBirthTime(), 20);
                a.setUpdateStrategy(BeforeBirthStrategy.getStrategy());
                assertEquals("strategy", a.getNextStrategy().getType(), new AmbushStrategy().getType());
                assertEquals("strategy", a.getUpdateStrategy().getType(), new BeforeBirthStrategy().getType());
                assertEquals("Type", a.getType(), "ghost");
                a.setLevel(1);
                assertEquals("Level", a.getLevel(), 1);
                assertEquals("Context", a.getGameContext(), gc);
                assertFalse("Collide?", a.isCollidedWith(gc.getMan()));
                assertEquals("LiveClock", a.getLiveClock(), 0);
                a.addliveClock();
                assertEquals("Add Clock", a.getLiveClock(), 1);
                a.initliveClock();
                assertEquals("LiveClock", a.getLiveClock(), 0);
                a.setIsOut(true);
                assertTrue("isOut", a.getIsOut());
                a.setIsOut(false);
                a.getDieVel();
                assertFalse("Stayaway", a.getStayAway());
                assertFalse("Flashing", a.getFlashing());
                a.reBirth();
                a.setDieRound(2);
                assertEquals("Dieround", a.getDieRound(), 2);
                a.setDieRound(0);
                assertFalse("die?", a.getIsDead());
                a.setStayAway(true);
            } else if (a.getColor().equals("pink")) {
                assertEquals("birth time", a.getBirthTime(), 40);
            }
        }

        for (int i = 0; i < 10; i++)
            da.update(39);
        for (int i = 0; i < 1000; i++) {
            da.update(40);
        }

        assertEquals("number of ghosts", 4, ghs.size());
    }

    /**
     * Test eat a ghost.
     */
    public void testCmdEat() {
        DispatchAdapter da = new DispatchAdapter(0);
        da.init();
        GameContext gameContext = da.getGameContext();
        ArrayList<Ghost> ghs = gameContext.getGhosts();
        ArrayList<Wall> wh = gameContext.getHorizontalWalls();
        ArrayList<Wall> wv = gameContext.getVerticalWalls();
        InteractCmd ic = InteractCmd.getCmd(new Point(1, 0), gameContext.getMan(),
                ghs, new PropertyChangeSupport(gameContext.getMan()));
        ic.goToNearest(ghs.get(0));
        ic.setDie(ghs.get(0));
        assertTrue("die", ghs.get(0).getIsDead());
        ghs.get(0).setStayAway(true);
        gameContext.setStayAwayActive(true);
        gameContext.getMan().setLoc(ghs.get(0).getLoc());
        ghs.get(0).setIsDead(false);
        int beforeS = gameContext.getMan().getScore();
        ic.execute(ghs.get(0));
        int afterS = gameContext.getMan().getScore();

        /**
         * Try if the score of ghost is added.
         */
        assertTrue("Test eat", beforeS < afterS);
    }

    /**
     * Test eaten by ghost.
     */
    public void testCmdEaten() {
        DispatchAdapter da = new DispatchAdapter(0);
        da.init();
        GameContext gameContext = da.getGameContext();
        ArrayList<Ghost> ghs = gameContext.getGhosts();
        ArrayList<Wall> wh = gameContext.getHorizontalWalls();
        ArrayList<Wall> wv = gameContext.getVerticalWalls();
        InteractCmd ic = InteractCmd.getCmd(new Point(1, 0), gameContext.getMan(),
                ghs, new PropertyChangeSupport(gameContext.getMan()));
        ic.goToNearest(ghs.get(0));
        ic.setDie(ghs.get(0));
        assertTrue("die", ghs.get(0).getIsDead());
        ghs.get(0).setStayAway(true);
        gameContext.setStayAwayActive(false);
        gameContext.getMan().setLoc(ghs.get(0).getLoc());
        ghs.get(0).setIsDead(false);
        int beforeS = gameContext.getMan().getLives();
        ic.execute(ghs.get(0));
        int afterS = gameContext.getMan().getLives();

        /**
         * Try if life is reduced.
         */
        assertTrue("Test eat", beforeS > afterS);
    }

    /**
     * Test remaining functions for man.
     */
    public void testMan() {
        DispatchAdapter da = new DispatchAdapter(0);
        da.init();
        GameContext gameContext = da.getGameContext();
        Man m = gameContext.getMan();
        m.initEatenNum();
        assertEquals("Eaten", 0, m.getEatenNum());

        m.setLevel(5);
        m.addLevel();
        assertEquals("Level", 6, m.getLevel());
    }
}
