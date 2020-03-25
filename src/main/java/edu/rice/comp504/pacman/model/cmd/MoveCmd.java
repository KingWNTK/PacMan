package edu.rice.comp504.pacman.model.cmd;

import edu.rice.comp504.pacman.model.DispatchAdapter;
import edu.rice.comp504.pacman.model.Typeable;
import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;
import edu.rice.comp504.pacman.model.strategy.IUpdateStrategy;
import edu.rice.comp504.pacman.model.wall.Wall;

import java.awt.*;
import java.util.ArrayList;

/**
 * The is used to update the strategy of the Ghost.
 *
 * @author Tingting Zhou
 */

public class MoveCmd implements IPacManCmd {
    private Man man;
    private Point newManDir;
    private ArrayList<Wall> verticalWalls;
    private ArrayList<Wall> horizontalWalls;

    /**
     * The constructor.
     *
     * @param man             The man
     * @param newManDir       The new direction of Pacman
     * @param verticalWalls   The vertical walls
     * @param horizontalWalls The horizontal walls
     */
    public MoveCmd(Man man, Point newManDir, ArrayList<Wall> verticalWalls, ArrayList<Wall> horizontalWalls) {
        this.man = man;
        this.newManDir = newManDir;
        this.verticalWalls = verticalWalls;
        this.horizontalWalls = horizontalWalls;
    }

    @Override
    public void execute(Typeable context) {
        if (context.getType().equals("ghost")) {
            ((Ghost) context).getUpdateStrategy().update((Ghost) context, man);
        } else if (context.getType().equals("man")) {
            man.move(newManDir, verticalWalls, horizontalWalls);

        }
    }
}

