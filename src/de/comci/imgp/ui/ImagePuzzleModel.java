/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.comci.imgp.ui;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Sebastian
 */
final class ImagePuzzleModel {
    private int lastBuzzed;
    private Timer buzzTimer;
    private int answerDuration = 3000;

    /**
     * Amount of time (in milliseconds) a team has to answer 
     */
    public int getAnswerDuration() {
        return answerDuration;
    }

    public void setAnswerDuration(int answerDuration) {
        this.answerDuration = answerDuration;
    }

    private void buzz() {
        
        if (state != STATE.RUNNING && state != STATE.READY) {
            System.err.println("buzz can only be called in ready|running state");
            return;
        }
        
        buzzTimer = new Timer(1000, new AbstractAction() {

            private STATE initialState = state;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                setState(initialState);
                if (initialState == STATE.RUNNING) {
                    setState(STATE.HALTED);
                    ImagePuzzleModel.this.start();
                }
            }
            
        });        
        
        stopRevealTimer();
        setState(STATE.BUZZED);
        
        buzzTimer.setInitialDelay(answerDuration);
        buzzTimer.setRepeats(false);
        buzzTimer.start();
        
    }

    public enum STATE {
        RUNNING, HALTED, READY, REVEALED, UNINITIALIZED, BUZZED
    }
    
    public static final String PROPERTY_NUMBER_OF_REVEALED_TILES = "PROPERTY_NUMBER_OF_REVEALED_TILES";
    public static final String PROPERTY_TILE_NUMBER = "PROPERTY_TILE_NUMBER";
    public static final String PROPERTY_SPEED = "PROPERTY_SPEED";
    public static final String PROPERTY_TILE_COLOR = "PROPERTY_TILE_COLOR";
    public static final String PROPERTY_IMAGE = "PROPERTY_IMAGE";
    
    private BoundedRangeModel progressModel;
    private int numberOfTilesVertical = 8;
    private File imgFile;
    private Color tileColor = new Color(25, 25, 25);
    private Timer revealTimer;
    private int numberOfTiles = 8*7;
    private Image rawImage;
    private int numberOfTilesHorizontal = 7;
    private List<PropertyChangeListener> propertyListener;
    private List<StateChangeListener> stateListener;
    private BoundedRangeModel speedModel;
    private Map<Integer, String> teams;

    public BoundedRangeModel getSpeedModel() {
        return speedModel;
    }

    public ImagePuzzleModel() {
        propertyListener = new LinkedList<>();
        stateListener = new LinkedList<>();
        revealSequence = new LinkedList<>();
        state = STATE.UNINITIALIZED;
        teams = new HashMap<>();
        teams.put(0, "Team 1");
        teams.put(1, "Team 2");
        progressModel = new DefaultBoundedRangeModel(0, 0, 0, numberOfTiles);
        speedModel = new DefaultBoundedRangeModel(8, 0, 1, 10);
        speedModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setSpeed(speedModel.getValue());
            }
        });
    }

    public void addTeam(int id, String name) {
        teams.put(id, name);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyListener.add(listener);
    }

    public void addStateChangeListener(StateChangeListener listener) {
        stateListener.add(listener);
    }

    private void firePropertyChange(String property, Object oldValue, Object newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, property, oldValue, newValue);
        for (PropertyChangeListener l : propertyListener) {
            l.propertyChange(evt);
        }
    }

    private void fireStateChange(STATE newState) {
        for (StateChangeListener l : stateListener) {
            l.stateChanged(newState);
        }
    }
    private STATE state;

    private void startRevealTimer() {

        if (state == STATE.READY) {

            revealTimer = new Timer(getSpeedAsDelay(), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("timer timed");
                    revealNext();
                }
            });
            revealTimer.start();
            setState(STATE.RUNNING);

        } else if (state == STATE.HALTED || state == STATE.REVEALED) {

            // assume timer exists
            revealTimer.setInitialDelay(0);
            revealTimer.start();
            setState(STATE.RUNNING);

        } else {
            // ignore others
        }

    }

    private void stopRevealTimer() {

        if (state == STATE.RUNNING) {

            revealTimer.stop();
            setState(STATE.HALTED);

        }

    }

    private void resetTimer() {

        if (state == STATE.HALTED) {

            revealTimer = null;
            setState(STATE.READY);

        }

    }

    public int getNumberOfTilesVertical() {
        return numberOfTilesVertical;
    }

    public void setImage(final File imgFile) {

        if (imgFile != null && this.imgFile != imgFile && imgFile.exists() && imgFile.isFile() && imgFile.getName().toLowerCase().endsWith("jpg")) {

            stopRevealTimer();
            resetTimer();

            this.imgFile = imgFile;

            new SwingWorker<Image, Void>() {
                @Override
                protected Image doInBackground() throws Exception {
                    System.out.println("loading image from file ...");
                    Image rawImage = null;
                    try {
                        rawImage = ImageIO.read(imgFile);
                        //rawImage = Toolkit.getDefaultToolkit().getImage(image.getAbsolutePath());
                    } catch (IOException ex) {
                        Logger.getLogger(ImagePuzzlePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return rawImage;
                }

                @Override
                protected void done() {
                    try {
                        System.out.println("Image loaded from file.");
                        rawImage = get();
                        initRevealSequence();
                        setState(STATE.READY);
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(ImagePuzzlePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.execute();
        }
    }

    public void reveal() {
        progressModel.setValue(progressModel.getMaximum());
        revealSequence.clear();
        stopRevealTimer();
        setState(STATE.REVEALED);
    }

    public int getSpeedAsDelay() {
        return speedModel.getValue() * 200;
    }
    private List<Integer> revealSequence;

    void revealNext() {

        if (!revealSequence.isEmpty()) {
            int r = revealSequence.remove(0);
            progressModel.setValue(numberOfTiles - revealSequence.size());
            System.out.println(String.format("revealed item %d", r));

            if (revealSequence.isEmpty()) {
                stopRevealTimer();
            } else {
                fireStateChange(STATE.RUNNING);
            }
        }

    }

    public void setTileColor(Color tileColor) {
        if (tileColor != null && tileColor != this.tileColor) {
            Color old = this.tileColor;
            this.tileColor = tileColor;
            firePropertyChange(PROPERTY_TILE_COLOR, old, tileColor);
        }
    }

    public void setSpeed(int speed) {
        if (speed >= 1 && speed <= 10) {
            System.out.println(String.format("speed set to %d", speed));
            int oldSpeed = this.speedModel.getValue();
            this.speedModel.setValue(speed);
            if (revealTimer != null) {
                revealTimer.setDelay(getSpeedAsDelay());
            }
            firePropertyChange(PROPERTY_SPEED, oldSpeed, speed);
        }
    }

    public BoundedRangeModel getProgressModel() {
        return progressModel;
    }

    public File getImage() {
        return imgFile;
    }

    public void setNumberOfTiles(int numberOfTilesVertical, int numberOfTilesHorizontal) {

        if (numberOfTilesVertical > 0 && numberOfTilesHorizontal > 0) {
            System.out.println(String.format("Setting number of tiles to (%d,%d)", numberOfTilesVertical, numberOfTilesHorizontal));
            int oNoT = numberOfTiles;
            this.numberOfTilesVertical = numberOfTilesVertical;
            this.numberOfTilesHorizontal = numberOfTilesHorizontal;
            this.numberOfTiles = this.numberOfTilesHorizontal * this.numberOfTilesVertical;
            progressModel.setMaximum(numberOfTiles);
            firePropertyChange(PROPERTY_TILE_NUMBER, oNoT, numberOfTiles);
            reset();
        }

    }

    public void reset() {
        stop();
        initRevealSequence();
        resetTimer();
    }

    private void initRevealSequence() {
        revealSequence = new LinkedList<>();
        for (int i = 0; i < numberOfTiles; i++) {
            revealSequence.add(i);
        }
        Collections.shuffle(revealSequence);
        progressModel.setValue(0);
        System.out.println(String.format("Receal Sequence initialized with %d steps", revealSequence.size()));
    }

    void stop() {
        stopRevealTimer();
    }

    public void start() {
        if (revealSequence.isEmpty()) {
            reset();
        }
        startRevealTimer();
    }

    public int getNumberOfTilesHorizontal() {
        return numberOfTilesHorizontal;
    }

    public int getVisibleNumberOfTiles() {
        return getNumberOfTiles() - revealSequence.size();
    }

    public int getNumberOfTiles() {
        return numberOfTiles;
    }

    public int getSpeed() {
        return speedModel.getValue();
    }

    public Color getTileColor() {
        return tileColor;
    }

    private void setState(STATE state) {
        this.state = state;
        fireStateChange(state);
    }

    Image getRawImage() {
        return rawImage;
    }

    List<Integer> getRevealSequence() {
        return Collections.unmodifiableList(revealSequence);
    }

    public int getTeamLastBuzzed() {
        return lastBuzzed;
    }
    
    public String getTeamName(int id) {
        return teams.get(id);
    }

    public void buzz(int id) {
        if ((state == STATE.RUNNING || state == STATE.READY) && teams.containsKey(id)) {
            System.out.println(String.format("party %s (#%d) has buzzed", teams.get(id), id));
            lastBuzzed = id;
            buzz();
        }
    }
    
}
