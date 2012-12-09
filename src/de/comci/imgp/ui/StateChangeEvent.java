// Fraunhofer Institute for Computer Graphics Research (IGD)
// Competence Center for Information Visualization and Visual Analytics
//
// Copyright (c) 2011-2012 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.
package de.comci.imgp.ui;

import de.comci.imgp.ui.ImagePuzzleModel.GameState;

/**
 *
 * @author Sebastian Maier
 */
public class StateChangeEvent {

    public GameState getNewState() {
        return newState;
    }

    public ImagePuzzleModel getSrcModel() {
        return srcModel;
    }
    
    private final GameState newState;
    private final ImagePuzzleModel srcModel;

    StateChangeEvent(ImagePuzzleModel srcModel, GameState newState) {
        this.srcModel = srcModel;
        this.newState = newState;
    }

}