package com.envyful.api.gui.page;

import com.envyful.api.gui.item.Displayable;

/**
 *
 * An interface representing a section of the {@link com.envyful.api.gui.Gui} where {@link Displayable}s can be placed.
 *
 */
public interface Pane {

    /**
     *
     * Adds the displayable item to a slot in the GUI.
     * The position of the item varies depending on the implementation of the Pane
     *
     * @param displayable The displayable to add
     */
    void add(Displayable displayable);

    /**
     *
     * Sets the displayable at posX and posY to the new displayable provided.
     *
     * @param posX The X position
     * @param posY The Y position
     * @param displayable The item to display at X and Y
     */
    void set(int posX, int posY, Displayable displayable);

    /**
     *
     * Sets the displayable at the position to the new displayable provided.
     * The meaning of the pos value varies depending on the implementation of the pane
     *
     * @param pos the new position
     * @param displayable The item to display at X and Y
     */
    void set(int pos, Displayable displayable);

    /**
     *
     * Fills the pane with the given item
     *
     * @param displayable The item to fill the pane with
     */
    void fill(Displayable displayable);

    /**
     *
     * Removes all displayable items from the pane
     *
     */
    void clear();

}
