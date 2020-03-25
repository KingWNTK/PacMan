package edu.rice.comp504.pacman.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.rice.comp504.pacman.model.cmd.InteractCmd;
import edu.rice.comp504.pacman.model.cmd.MoveCmd;
import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;
import edu.rice.comp504.pacman.model.pac.Pac;
import edu.rice.comp504.pacman.model.strategy.ghost.ChaseManStrategy;
import edu.rice.comp504.pacman.model.wall.Wall;
import edu.rice.comp504.pacman.model.strategy.collision.ScoreStrategy;
import edu.rice.comp504.pacman.model.strategy.ghost.BeforeBirthStrategy;
import edu.rice.comp504.pacman.model.strategy.ghost.RandomWalkStrategy;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * This adapter interfaces with the view and the controller.
 */
public class DispatchAdapter {

    private static final int XMAX = 58;
    private static final int YMAX = 36;
    private static final int EACHPIX = 20;
    private static final int NUMBEROFDOTS = 30;

    private HashMap<Point, HashMap<Point, Point>> allPath;
    private PropertyChangeSupport pcs;
    private ArrayList<Wall> verticalWalls;
    private ArrayList<Wall> horizontalWalls;
    private ArrayList<Pac> pacs;
    private ArrayList<Ghost> ghosts;
    private Man man;
    private int initialVel;
    private int[][] pathArr;
    private GameContext gameContext;
    private JsonObject mapJson;
    private String[] mapUrls = {
        "https://jsondragon.s3-us-west-1.amazonaws.com/wall2.json",
        "https://jsondragon.s3-us-west-1.amazonaws.com/cxk.json"
    };
//    private int wallTimer;

    /**
     * Get the number of dots.
     *
     * @return the number of dots.
     */
    public static int getNumberofdots() {
        return NUMBEROFDOTS;
    }

    public DispatchAdapter(JsonObject mapJson) {
        this.mapJson = mapJson;
    }

    /**
     * Dispatch adapter constructor.
     *
     * @param mapId The mapId
     */
    public DispatchAdapter(int mapId) {
        try {
//            File file = new File(getClass().getClassLoader().getResource("public/jsons/wall2.json").getFile());
//            parseMapJson(new JsonParser().parse(new FileReader(file)).getAsJsonObject());
            mapJson = readJsonFromUrl(mapUrls[mapId]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse map given in json format.
     *
     * @param jsonObject The map itself.
     */
    private void parseMapJson(JsonObject jsonObject) {
        if (jsonObject.get("wall") != null) {
            JsonArray jsonWalls = jsonObject.get("wall").getAsJsonArray();
            for (JsonElement jsonWall : jsonWalls) {
                JsonArray jsonStart = jsonWall.getAsJsonObject().get("start").getAsJsonArray();
                JsonArray jsonEnd = jsonWall.getAsJsonObject().get("end").getAsJsonArray();
                String jsonColor = jsonWall.getAsJsonObject().get("color").getAsString();


                Wall newWall = new Wall(
                        new Point(jsonStart.get(0).getAsInt(), jsonStart.get(1).getAsInt()),
                        new Point(jsonEnd.get(0).getAsInt(), jsonEnd.get(1).getAsInt()),
                        false, jsonColor
                );
                if (jsonStart.get(0).equals(jsonEnd.get(0))) {
                    verticalWalls.add(newWall);
                } else {
                    horizontalWalls.add(newWall);
                }

                JsonElement tmp = jsonWall.getAsJsonObject().get("teleport");
                if (tmp != null) {
                    JsonArray jsonTeleport = tmp.getAsJsonArray();
                    newWall.setTeleport(new Point(jsonTeleport.get(0).getAsInt(), jsonTeleport.get(1).getAsInt()));
                }

                tmp = jsonWall.getAsJsonObject().get("passable");
                if (tmp != null) {
                    // find the passable wall
                    newWall.setPassable(true);
//                    passableWall = newWall;
                }
            }
        }

        HashSet<Point> bigPacLoc = null;

        if (jsonObject.get("bigPac") != null) {
            bigPacLoc = new HashSet<>();
            JsonArray jsonBigPacs = jsonObject.get("bigPac").getAsJsonArray();
            for (JsonElement jsonBigPac : jsonBigPacs) {
                JsonArray jsonLoc = jsonBigPac.getAsJsonObject().get("loc").getAsJsonArray();
                Point loc = new Point(jsonLoc.get(0).getAsInt(), jsonLoc.get(1).getAsInt());
                bigPacLoc.add(loc);
            }
        }


        if (jsonObject.get("pac") != null) {
            JsonArray jsonPacs = jsonObject.get("pac").getAsJsonArray();
            HashSet<Point> locSet = new HashSet<>();

            for (JsonElement jsonPac : jsonPacs) {
                JsonArray jsonBeginLoc = jsonPac.getAsJsonObject().get("start").getAsJsonArray();
                Point beginLoc = new Point(jsonBeginLoc.get(0).getAsInt(), jsonBeginLoc.get(1).getAsInt());
                JsonArray jsonEndLoc = jsonPac.getAsJsonObject().get("end").getAsJsonArray();
                Point endLoc = new Point(jsonEndLoc.get(0).getAsInt(), jsonEndLoc.get(1).getAsInt());

                if (beginLoc.x == endLoc.x) {
                    for (int y = Math.min(beginLoc.y, endLoc.y); y <= Math.max(beginLoc.y, endLoc.y); y += 20) {
                        Point loc = new Point(beginLoc.x, y);
                        if (!locSet.contains(loc)) {
                            Pac newPac;
                            if (bigPacLoc != null && bigPacLoc.contains(loc)) {
                                newPac = new Pac(loc, 10, ScoreStrategy.getStrategy());
                                newPac.setScore(50);
                            } else {
                                newPac = new Pac(loc, ScoreStrategy.getStrategy());
                            }
                            pacs.add(newPac);
                            locSet.add(loc);
                        }
                    }
                } else {
                    for (int x = Math.min(beginLoc.x, endLoc.x); x <= Math.max(beginLoc.x, endLoc.x); x += 20) {
                        Point loc = new Point(x, beginLoc.y);
                        if (!locSet.contains(loc)) {
                            Pac newPac;
                            if (bigPacLoc != null && bigPacLoc.contains(loc)) {
                                newPac = new Pac(loc, 10, ScoreStrategy.getStrategy());
                                newPac.setScore(50);
                            } else {
                                newPac = new Pac(loc, ScoreStrategy.getStrategy());
                            }
                            pacs.add(newPac);
                            locSet.add(loc);
                        }
                    }
                }
            }
        }
    }

    /**
     * Read from file.
     * @param rd Reader
     * @return   String Builder
     * @throws IOException
     */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /**
     * Read json file from remote url
     *
     * @param url The url.
     * @return A JsonObject
     * @throws IOException The IO exception
     */
    private static JsonObject readJsonFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JsonObject json = new JsonParser().parse(jsonText).getAsJsonObject();
            return json;
        } finally {
            is.close();
        }
    }

    /**
     * Preprocess the map.
     */
    private void preProcess() {
        if (allPath == null) {
            allPath = new HashMap<>();
        } else {
            return;
        }

        HashMap<Point, Integer> posToInt = new HashMap<>();
        HashMap<Integer, Point> intToPos = new HashMap<>();
        int[] dx = {-20, 20, 0, 0};
        int[] dy = {0, 0, 20, -20};

        int cnt = 0;
        for (Pac pac : pacs) {
            cnt++;
            posToInt.put(pac.getLoc(), cnt);
            intToPos.put(cnt, pac.getLoc());
        }

        cnt++;
        posToInt.put(new Point(31 * 20, 11 * 20), cnt);
        intToPos.put(cnt, new Point(31 * 20, 11 * 20));

        cnt++;
        posToInt.put(new Point(32 * 20, 11 * 20), cnt);
        intToPos.put(cnt, new Point(32 * 20, 11 * 20));

        cnt++;
        posToInt.put(new Point(33 * 20, 11 * 20), cnt);
        intToPos.put(cnt, new Point(33 * 20, 11 * 20));

        cnt++;
        posToInt.put(new Point(30 * 20, 11 * 20), cnt);
        intToPos.put(cnt, new Point(30 * 20, 11 * 20));

        cnt++;
        posToInt.put(new Point(29 * 20, 11 * 20), cnt);
        intToPos.put(cnt, new Point(29 * 20, 11 * 20));

        cnt++;
        posToInt.put(new Point(31 * 20, 10 * 20), cnt);
        intToPos.put(cnt, new Point(31 * 20, 10 * 20));

        cnt++;
        posToInt.put(new Point(31 * 20, 12 * 20), cnt);
        intToPos.put(cnt, new Point(31 * 20, 12 * 20));

        cnt++;
        posToInt.put(new Point(31 * 20, 13 * 20), cnt);
        intToPos.put(cnt, new Point(31 * 20, 13 * 20));


//        cnt++;
//        posToInt.put(new Point(33*20,13*20), cnt);
//        intToPos.put(cnt, new Point(33*20,13*20));


        for (int i = 1; i <= cnt; i++) {
            Deque<Point> q = new ArrayDeque<>();
            HashMap<Point, Point> lastPos = new HashMap<>();
            q.push(intToPos.get(i));

            boolean[] vis = new boolean[cnt + 1];

            Arrays.fill(vis, false);

            vis[posToInt.get(q.getFirst())] = true;
            lastPos.put(q.getFirst(), null);
            while (!q.isEmpty()) {
                Point curPos = q.pollFirst();
                for (int j = 0; j < 4; j++) {
                    Point nextPos = new Point(curPos.x + dx[j], curPos.y + dy[j]);
                    if (!posToInt.containsKey(nextPos) || vis[posToInt.get(nextPos)]) {
                        continue;
                    }
                    vis[posToInt.get(nextPos)] = true;
                    lastPos.put(nextPos, curPos);
                    q.add(nextPos);
                }
            }
            allPath.put(intToPos.get(i), lastPos);
        }
    }

    /**
     * Initialize the map, add all the walls, pacs, ghosts and man.
     */
    public void init() {
//        wallTimer = 0;
        verticalWalls = new ArrayList<>();
        horizontalWalls = new ArrayList<>();
        initialVel = 10;
        pcs = new PropertyChangeSupport(this);
        man = new Man(new Point(620, 300), 19, new Point(0, 1), 20, 2);

        pacs = new ArrayList<>();
        ghosts = new ArrayList<>();
        pathArr = new int[XMAX][YMAX];

        parseMapJson(mapJson);


        preProcess();
        gameContext = new GameContext(this, man, ghosts, pacs, verticalWalls, horizontalWalls, allPath);

        addListener(man);
        man.setGameContext(gameContext);

        verticalWalls.sort((a, b) -> a.getBeginLoc().x - b.getBeginLoc().x);
        horizontalWalls.sort((a, b) -> a.getBeginLoc().y - b.getBeginLoc().y);
        for (Wall wall : verticalWalls) {
            addListener(wall);
        }
        for (Wall wall : horizontalWalls) {
            addListener(wall);
        }

        for (Pac pac : pacs) {
            addListener(pac);
            int pacX = pac.getLoc().x / EACHPIX;
            int pacY = pac.getLoc().y / EACHPIX;
            pathArr[pacX][pacY] = 1;
            pac.setGameContext(gameContext);
        }

        Point loc1 = new Point(29 * 20, 11 * 20);
        Point loc2 = new Point(33 * 20, 11 * 20);
        Point loc3 = new Point(31 * 20, 13 * 20);
        Point loc4 = new Point(31 * 20, 9 * 20);

        ghosts.add(new Ghost(loc1, initialVel, new Point(0, -1), 20, "blue", BeforeBirthStrategy.getStrategy(), pathArr));
        ghosts.add(new Ghost(loc2, initialVel, new Point(0, -1), 20, "pink", BeforeBirthStrategy.getStrategy(), pathArr));
        ghosts.add(new Ghost(loc3, initialVel, new Point(0, 1), 20, "red", BeforeBirthStrategy.getStrategy(), pathArr));
        ghosts.add(new Ghost(loc4, initialVel, new Point(-1, 0), 20, "yellow", RandomWalkStrategy.getStrategy(), pathArr));

        for (Ghost ghost : ghosts) {
            addListener(ghost);
            ghost.setGameContext(gameContext);
        }

        gameContext.setLevel(man.getLevel());
    }

    /**
     * Update all the objects.
     *
     * @param dirCmd The direction command corresponding to the key pressed in the front-end.
     * @return All the updated objects.
     */
    public PropertyChangeListener[] update(int dirCmd) {
        recoverGhosts();
        man.setNextLevel(false);
        Point newDir = man.getDir();
        if (dirCmd == 37) {
            newDir = new Point(-1, 0);
        } else if (dirCmd == 38) {
            newDir = new Point(0, -1);
        } else if (dirCmd == 39) {
            newDir = new Point(1, 0);
        } else if (dirCmd == 40) {
            newDir = new Point(0, 1);
        }

        pcs.firePropertyChange("move", null, new MoveCmd(man, newDir, verticalWalls, horizontalWalls));
        pcs.firePropertyChange("interact", null, InteractCmd.getCmd(newDir, man, ghosts, pcs));
        //interact again to ensure everything happened.
        pcs.firePropertyChange("interact", null, InteractCmd.getCmd(newDir, man, ghosts, pcs));

        gameContext.update();
        clearEatenPac();
        gameContext.addWallTimer();

        if (gameContext.getWallTimer() == GameContext.getWallleveltime()) {
            resetWall();
            gameContext.resetWallTimer();
        }

        return pcs.getPropertyChangeListeners();
    }

    /**
     * Reset the walls.
     */
    private void resetWall() {
        for (int i = 0; i < horizontalWalls.size(); i++) {
            horizontalWalls.get(i).resetColor();
        }
        for (int j = 0; j < verticalWalls.size(); j++) {
            verticalWalls.get(j).resetColor();
        }
    }

    /**
     * Reset all the ghosts.
     */
    private void recoverGhosts() {
        for (Ghost ghost : ghosts) {
            ghost.setDieScore(0);
        }
    }

    /**
     * Check all pacs and remove the eaten pacs.
     */
    private void clearEatenPac() {
        //TODO
        for (Pac pac : pacs) {
            if (pac.isEaten()) {
                pcs.removePropertyChangeListener(pac);
            }
        }
    }

    /**
     * Add a listener to pcs.
     *
     * @param pcl The listener
     */
    public void addListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    /**
     * remove a specific listener.
     *
     * @param pcl The listener to remove
     */
    public void removeListener(PropertyChangeListener pcl) {
        pcs.removePropertyChangeListener(pcl);
    }

    /**
     * Get the game context.
     *
     * @return game context
     */
    public GameContext getGameContext() {
        return gameContext;
    }
}
