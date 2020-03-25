package edu.rice.comp504.pacman.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;
import edu.rice.comp504.pacman.model.strategy.ghost.RandomWalkStrategy;
import junit.framework.TestCase;

import java.awt.*;

public class TestMovingObject extends TestCase {
    private String testMap = "{\"wall\":[{\"start\":[420,280],\"end\":[900,280],\"color\":\"red\"},{\"start\":[420,320],\"end\":[860,320],\"color\":\"red\"},{\"start\":[860,480],\"end\":[900,480],\"color\":\"red\"},{\"start\":[420,280],\"end\":[420,320],\"color\":\"red\"},{\"start\":[860,320],\"end\":[860,480],\"color\":\"red\"},{\"start\":[900,280],\"end\":[900,480],\"color\":\"red\"},{\"start\":[0,0],\"end\":[1180,0],\"color\":\"red\"},{\"start\":[0,720],\"end\":[1180,720],\"color\":\"red\"},{\"start\":[0,0],\"end\":[0,740],\"color\":\"red\"},{\"start\":[1160,0],\"end\":[1160,740],\"color\":\"red\"}],\"pac\":[{\"start\":[440,300],\"end\":[880,300]},{\"start\":[880,300],\"end\":[880,460]}],\"bigPac\":[{\"loc\":[880,460]}]}";
    public void testMovingObject() {
        DispatchAdapter da = new DispatchAdapter(0);
        da.init();
        GameContext gameContext = da.getGameContext();
        Man man = gameContext.getMan();
        Point loc = (Point)man.getLoc().clone();
        Point dir = (Point)man.getDir().clone();
        int locX = man.getLoc().x;
        man.move(new Point(1, 0), gameContext.getVerticalWalls(), gameContext.getHorizontalWalls());
        assertEquals("should be able to move", locX + man.getVel(), man.getLoc().x);

        locX = man.getLoc().x;
        man.move(new Point(0, 1), gameContext.getVerticalWalls(), gameContext.getHorizontalWalls());
        assertEquals("should not change direction when there is wall", locX + man.getVel(), man.getLoc().x);

        assertEquals("should keep initial location", loc, man.getInitialPosition());

        assertEquals("should keep initial direction", dir, man.getInitialDirection());

        man.setLoc((Point)man.getInitialPosition().clone());
        assertEquals("should set man's location to initial", man.getInitialPosition(), man.getLoc());

        Ghost ghost = gameContext.getGhosts().get(0);

        ghost.setStrategy(RandomWalkStrategy.getStrategy());
        assertEquals("should set ghost strategy", RandomWalkStrategy.getStrategy().getType(), ghost.getStrategy().getType());

        man.moveOneStep();
        assertEquals("should be able to move one step", man.getInitialPosition().x + 1, man.getLoc().x);

        man.setLoc(new Point(100, 100));
        ghost.setLoc(new Point(100, 200));
        assertFalse("should judge moving objects collision", man.isCollison(ghost));
        man.setRadius(10);
        assertEquals("should set moving object radius", 10, man.getRadius());
    }

    public void testChangeDir() {
        JsonObject jo = new JsonParser().parse(testMap).getAsJsonObject();
        DispatchAdapter da = new DispatchAdapter(jo);
        da.init();
        GameContext gameContext = da.getGameContext();
        for(int i = 0; i < 20; i++) {
            da.update(39);
        }
        Point loc = (Point)gameContext.getMan().getLoc().clone();
        da.update(39);
        assertEquals("should stop when hit the wall", loc, da.getGameContext().getMan().getLoc());

        da.update(40);
        assertEquals("should change direction when possible", loc.y + gameContext.getMan().getVel(), da.getGameContext().getMan().getLoc().y);
    }

}
