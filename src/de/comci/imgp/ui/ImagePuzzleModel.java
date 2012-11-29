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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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
class ImagePuzzleModel {

    public static final String PROPERTY_NUMBER_OF_REVEALED_TILES = "PROPERTY_NUMBER_OF_REVEALED_TILES";
    public static final String PROPERTY_TILE_NUMBER = "PROPERTY_TILE_NUMBER";
    public static final String PROPERTY_SPEED = "PROPERTY_SPEED";
    public static final String PROPERTY_TILE_COLOR = "PROPERTY_TILE_COLOR";
    public static final String PROPERTY_IMAGE = "PROPERTY_IMAGE";
    public static final String STATE_RUNNING = "STATE_RUNNING";
    public static final String STATE_HALTED = "STATE_HALTED";
    public static final String STATE_READY = "STATE_READY";
    public static final String STATE_REVEALED = "STATE_REVEALED";
    public static final String STATE_UNINITIALIZED = "STATE_UNINITIALIZED";
    private BoundedRangeModel progressModel;
    private int numberOfTilesVertical = 6;
    private File imgFile;
    private Color tileColor = new Color(25, 25, 25);
    private Timer timer;
    private int numberOfTiles = 36;
    private Image rawImage;
    private int numberOfTilesHorizontal = 6;
    private List<PropertyChangeListener> propertyListener;
    private List<StateChangeListener> stateListener;
    private BoundedRangeModel speedModel;

    public BoundedRangeModel getSpeedModel() {
        return speedModel;
    }

    public ImagePuzzleModel() {
        propertyListener = new LinkedList<>();
        stateListener = new LinkedList<>();
        revealSequence = new LinkedList<>();
        state = STATE_UNINITIALIZED;
        progressModel = new DefaultBoundedRangeModel(0,0,0,numberOfTiles);
        speedModel = new DefaultBoundedRangeModel(3,0,1,10);
        speedModel.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                setSpeed(speedModel.getValue());
            }
        });
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

    private void fireStateChange(String newState) {
        for (StateChangeListener l : stateListener) {
            l.stateChanged(newState);
        }
    }
    private String state;

    private void startTimer() {

        if (state.equals(STATE_READY)) {

            timer = new Timer(getSpeedAsDelay(), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("timer timed");
                    revealNext();
                }
            });
            timer.start();
            setState(STATE_RUNNING);

        } else if (state.equals(STATE_HALTED) || state.equals(STATE_REVEALED)) {

            // assume timer exists
            timer.setInitialDelay(0);
            timer.start();
            setState(STATE_RUNNING);

        } else {
            // ignore others
        }

    }

    private void stopTimer() {

        if (state.equals(STATE_RUNNING)) {

            timer.stop();
            setState(STATE_HALTED);

        }

    }

    private void resetTimer() {

        if (state.equals(STATE_HALTED)) {

            timer = null;
            setState(STATE_READY);

        }

    }

    public int getNumberOfTilesVertical() {
        return numberOfTilesVertical;
    }

    public void setImage(final File imgFile) {

        if (imgFile != null && this.imgFile != imgFile && imgFile.exists() && imgFile.isFile() && imgFile.getName().toLowerCase().endsWith("jpg")) {

            stopTimer();
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
                        setState(STATE_READY);
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
        stopTimer();
        setState(STATE_REVEALED);
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
                stopTimer();
            } else {
                fireStateChange(STATE_RUNNING);
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
            if (timer != null) {
                timer.setDelay(getSpeedAsDelay());
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
            stopTimer();
            int oNoT = numberOfTiles;
            this.numberOfTilesVertical = numberOfTilesVertical;
            this.numberOfTilesHorizontal = numberOfTilesHorizontal;
            this.numberOfTiles = this.numberOfTilesHorizontal * this.numberOfTilesVertical;
            progressModel.setMaximum(numberOfTiles);
            firePropertyChange(PROPERTY_TILE_NUMBER, oNoT, numberOfTiles);
            resetTimer();
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
        stopTimer();
    }

    public void start() {
        if (revealSequence.isEmpty()) {
            reset();
        }
        startTimer();
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

    private void setState(String state) {
        this.state = state;
        fireStateChange(state);
    }

    Image getRawImage() {
        return rawImage;
    }

    List<Integer> getRevealSequence() {
        return Collections.unmodifiableList(revealSequence);
    }
}
