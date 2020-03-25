package edu.rice.comp504.pacman.model.strategy;

import edu.rice.comp504.pacman.model.Typeable;
import edu.rice.comp504.pacman.model.ghost.Ghost;
import edu.rice.comp504.pacman.model.man.Man;
import edu.rice.comp504.pacman.model.pac.Pac;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * The ICollisionStrategy is an interface designed to detect collision.
 */
public interface ICollisionStrategy extends Typeable {

    /**
     * Detect the interaction.
     * @param context   The Typeable objects
     * @param man       The pacman
     * @param ghosts    The list of ghosts
     */
    void interact(Typeable context, Man man, ArrayList<Ghost> ghosts);
}
