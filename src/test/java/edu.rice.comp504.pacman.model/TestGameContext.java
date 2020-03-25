package edu.rice.comp504.pacman.model;

import com.google.gson.JsonParser;
import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;
import edu.rice.comp504.pacman.model.strategy.ghost.*;
import junit.framework.TestCase;
import org.junit.After;

import java.awt.*;

public class TestGameContext extends TestCase {

    private String testMap = "{\"wall\":[{\"start\":[500,280],\"end\":[700,280],\"color\":\"red\"},{\"start\":[500,320],\"end\":[700,320],\"color\":\"red\"},{\"start\":[500,280],\"end\":[500,320],\"color\":\"red\"},{\"start\":[700,280],\"end\":[700,320],\"color\":\"red\"},{\"start\":[0,0],\"end\":[1180,0],\"color\":\"red\"},{\"start\":[0,720],\"end\":[1180,720],\"color\":\"red\"},{\"start\":[0,0],\"end\":[0,740],\"color\":\"red\"},{\"start\":[1160,0],\"end\":[1160,740],\"color\":\"red\"}],\"pac\":[{\"start\":[520,300],\"end\":[560,300]},{\"start\":[660,300],\"end\":[680,300]}],\"bigPac\":[{\"loc\":[680,300]}]}";

    public void testGameContext() {
        DispatchAdapter da = new DispatchAdapter(new JsonParser().parse(testMap).getAsJsonObject());
        da.init();
        GameContext gameContext = da.getGameContext();

        assertEquals(0, AfterDieStrategy.getStrategy().getTimer());
        assertEquals(0, AmbushStrategy.getStrategy().getTimer());

        assertEquals(0, BeforeBirthStrategy.getStrategy().getTimer());

        assertEquals(0, ChaseManStrategy.getStrategy().getTimer());
        assertEquals(0, IntersectStrategy.getStrategy().getTimer());

        assertEquals(0, RandomWalkStrategy.getStrategy().getTimer());
        assertEquals(0, StayAwayStrategy.getStrategy(gameContext).getTimer());


        da.update(39);
        for(int i = 0; i < 20; i++) {
            da.update(0);
        }

        Man man = gameContext.getMan();
        Ghost ghost = gameContext.getGhosts().get(3);

        assertEquals("energizer should work", StayAwayStrategy.getStrategy(gameContext).getType(), ghost.getUpdateStrategy().getType());

        ghost.setLoc((Point)man.getLoc().clone());

        da.update(0);

        assertEquals("man can eat stay away ghost", AfterDieStrategy.getStrategy().getType(), ghost.getUpdateStrategy().getType());

        assertEquals("score increases as expected", 260, man.getScore());

        for(int i = 0; i < 20; i++) {
            da.update(37);
        }
        assertEquals("can go to next level when all pacs are eaten", 2, man.getLevel());
    }
}
