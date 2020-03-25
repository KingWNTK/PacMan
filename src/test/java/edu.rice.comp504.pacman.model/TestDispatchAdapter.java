package edu.rice.comp504.pacman.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import junit.framework.TestCase;

import java.beans.PropertyChangeListener;

public class TestDispatchAdapter extends TestCase {
    private String testMap = "{\"wall\":[{\"start\":[420,280],\"end\":[900,280],\"color\":\"red\"},{\"start\":[420,320],\"end\":[860,320],\"color\":\"red\"},{\"start\":[860,480],\"end\":[900,480],\"color\":\"red\"},{\"start\":[420,280],\"end\":[420,320],\"color\":\"red\"},{\"start\":[860,320],\"end\":[860,480],\"color\":\"red\"},{\"start\":[900,280],\"end\":[900,480],\"color\":\"red\"},{\"start\":[0,0],\"end\":[1180,0],\"color\":\"red\"},{\"start\":[0,720],\"end\":[1180,720],\"color\":\"red\"},{\"start\":[0,0],\"end\":[0,740],\"color\":\"red\"},{\"start\":[1160,0],\"end\":[1160,740],\"color\":\"red\"}],\"pac\":[{\"start\":[440,300],\"end\":[880,300]},{\"start\":[880,300],\"end\":[880,460]}],\"bigPac\":[{\"loc\":[880,460]}]}";

    public void testLoadingMap() {
        DispatchAdapter da = new DispatchAdapter(0);
        da.init();
        PropertyChangeListener[] pcls = da.update(0);
        assertTrue("Map should be loaded", pcls.length > 0);

        GameContext gameContext = da.getGameContext();

        assertEquals("should be four ghosts", 4,  gameContext.getGhosts().size());
        assertNotNull("should has a pacman", gameContext.getMan());
    }

    public void testEatPac() {
        DispatchAdapter da = new DispatchAdapter(0);
        da.init();
        PropertyChangeListener[] pcls = da.update(0);
        int cnt = pcls.length;
        pcls = da.update(37);
        assertTrue("should eat pac", cnt > pcls.length);
    }

    public void testAddAndRemoveListener() {
        DispatchAdapter da = new DispatchAdapter(0);
        da.init();
        PropertyChangeListener[] pcls = da.update(0);
        int cnt = pcls.length;
        PropertyChangeListener pcl = pcls[0];
        da.removeListener(pcl);
        pcls = da.update(0);
        assertEquals("should remove listener", cnt - 1, pcls.length);
        da.addListener(pcl);
        pcls = da.update(0);
        assertEquals("should add listener", cnt, pcls.length);
    }

    public void testLoadCustomizedMap() {
        JsonObject jo = new JsonParser().parse(testMap).getAsJsonObject();
        DispatchAdapter da = new DispatchAdapter(jo);
        da.init();
        GameContext gameContext = da.getGameContext();
        assertTrue("test map should be loaded", gameContext.getPacs().size() > 0 && gameContext.getGhosts().size() == 4);
    }
}
