package edu.rice.comp504.pacman.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.rice.comp504.pacman.model.DispatchAdapter;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;


/**
 * The pac man controller creates the adapter(s) that communicate with the view.
 * The controller responds to requests from the view after contacting the adapter(s).
 */
public class PacManController {

    /**
     * The controller's main function, it holds the service.
     *
     * @param args The args.
     */
    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        staticFiles.location("/public");

        Gson gson = new Gson();
        Map<String, DispatchAdapter> dm = new HashMap<>();

        //Front-end call this end point to start game.
        get("/restart", (request, response) -> {
            String id = request.queryParams("id");
            if (dm.containsKey(id)) {
                dm.get(id).init();
            } else {
                DispatchAdapter da;
                if (request.queryParams("mapId") != null) {
                    da = new DispatchAdapter(Integer.parseInt(request.queryParams("mapId")));
                } else {
                    da = new DispatchAdapter(0);
                }
                dm.put(id, da);
                dm.get(id).init();
            }
            return gson.toJson("ok");
        });

        post("/restartCustomized", (request, response) -> {
            JsonObject jo = new JsonParser().parse(request.body()).getAsJsonObject();
            String id = jo.get("id").getAsString();
            if (dm.containsKey(id)) {
                dm.get(id).init();
            } else {
                DispatchAdapter da = new DispatchAdapter(jo.get("map").getAsJsonObject());
                dm.put(id, da);
                dm.get(id).init();
            }
            return gson.toJson("ok");
        });

        //This end point should perform an update on all the models and return the models.
        get("/update", (request, response) -> {
            String id = request.queryParams("id");
            int c = Integer.parseInt(request.queryParams("cmd"));
            if (dm.containsKey(id)) {
                return gson.toJson(dm.get(id).update(c));
            }
            return "not ok";
        });

        get("/quit", (request, response) -> {
            String id = request.queryParams("id");
            dm.remove(id);
            return "quit";
        });
    }

    /**
     * Deploy the app to Heroku.
     * @return The port number
     */
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
