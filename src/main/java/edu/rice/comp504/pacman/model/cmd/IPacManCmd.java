package edu.rice.comp504.pacman.model.cmd;

import edu.rice.comp504.pacman.model.MovingObject;
import edu.rice.comp504.pacman.model.Typeable;

/**
 * The IPacManCmd is an interface used to pass commands to Typeable objects.
 */
public interface IPacManCmd {

    /**
     * Execute the command.
     * @param context The typeable objects
     */
    public void execute(Typeable context);
}
