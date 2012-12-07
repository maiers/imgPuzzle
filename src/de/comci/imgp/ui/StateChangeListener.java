/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.comci.imgp.ui;

import de.comci.imgp.ui.ImagePuzzleModel.STATE;

/**
 *
 * @author Sebastian
 */
interface StateChangeListener {
    
    public void stateChanged(STATE newState);
        
}
