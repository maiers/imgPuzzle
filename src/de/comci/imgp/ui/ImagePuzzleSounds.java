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
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Sebastian Maier
 */
class ImagePuzzleSounds implements StateChangeListener {

    private final ImagePuzzleModel model;
    private final Map<Integer, URL> teamSounds;
    private final static String[] defaultFiles = new String[]{"woohoo.wav", "go-go-go.wav"};

    public ImagePuzzleSounds(ImagePuzzleModel model) {
        this.model = model;
        teamSounds = new HashMap<>();
        for (Integer key : model.getTeamIds()) {
            teamSounds.put(key, getClass().getClassLoader().getResource("resources/" + defaultFiles[key % 2]));
        }
    }

    public void setTeamSound(Integer teamId, File sound) throws MalformedURLException {
        teamSounds.put(teamId, sound.toURI().toURL());
    }

    @Override
    public void stateChanged(StateChangeEvent evt) {

        if (evt.getNewState() == GameState.BUZZED) {
            int teamId = evt.getSrcModel().getTeamLastBuzzed();
            try {
                // source: http://www3.ntu.edu.sg/home/ehchua/programming/java/J8c_PlayingSound.html
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(teamSounds.get(teamId));
                // Get a sound clip resource.
                Clip clip = AudioSystem.getClip();
                // Open audio clip and load samples from the audio input stream.
                clip.open(audioIn);
                clip.start();
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
                Logger.getLogger(ImagePuzzleSounds.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}