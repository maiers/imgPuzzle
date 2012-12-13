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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

/**
 *
 * @author Sebastian
 */
public final class ImagePuzzleModel {

    private int lastBuzzed;
    private Timer buzzTimer;
    private int answerDuration = 9000;
    private GameMode gameMode = GameMode.PUNISHMENT;

    private void executeBuzz() {

        buzzTimer = new Timer(1000, new AbstractAction() {
            private GameState initialState = state;

            @Override
            public void actionPerformed(ActionEvent e) {

                if (initialState == GameState.RUNNING) {
                    if (state != GameState.REVEALED) {
                        switch (gameMode) {
                            case PUNISHMENT:
                                blockTeamBuzz(lastBuzzed);
                            case NORMAL:
                            default:
                                setState(GameState.HALTED);
                                ImagePuzzleModel.this.start();
                                break;
                        }
                    }
                } else {
                    setState(initialState);
                }

            }
        });

        stopRevealTimer();
        setState(GameState.BUZZED);

        buzzTimer.setInitialDelay(answerDuration);
        buzzTimer.setRepeats(false);
        buzzTimer.start();
    }
    private final Map<Integer, Integer> blockedTeams;
    private int durationBlocked = 3;

    public int getDurationBlocked() {
        return durationBlocked;
    }

    public void setDurationBlocked(int durationBlocked) {
        this.durationBlocked = durationBlocked;
    }

    private synchronized void blockTeamBuzz(int teamId) {
        blockedTeams.put(teamId, revealSequence.size());
    }

    int getTeamLastBuzzedId() {
        return lastBuzzed;
    }

    public enum GameMode {

        PUNISHMENT, NORMAL
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * mode how to play the game
     *
     * @param gameMode
     */
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * Amount of time (in milliseconds) a team has to answer
     */
    public int getAnswerDuration() {
        return answerDuration;
    }

    public void setAnswerDuration(int answerDuration) {
        System.out.println(String.format("Set answer duration to %.1f s", answerDuration / 1000.0));
        this.answerDuration = answerDuration;
    }

    public Set<Integer> getTeamIds() {
        return teams.keySet();
    }
    private List<Integer> delayedTiles;

    public void toggleDelayedTile(Integer tileNumber) {

        if (state != GameState.READY) {
            // not allowed in all states besides READY
            return;
        }

        if (tileNumber >= 0 && tileNumber <= numberOfTiles) {
            if (delayedTiles.contains(tileNumber)) {
                try {
                    delayedTiles.remove(tileNumber);
                } catch (IndexOutOfBoundsException ex) {
                    // ignore
                }
            } else {
                delayedTiles.add(tileNumber);
            }
            initRevealSequence();
            saveDelayedTiles();
        }

    }

    public void clearPersitentDelayedTiles() {
    }

    private RecordManager getRecordManager() throws IOException {
        return RecordManagerFactory.createRecordManager("delayedTiles");
    }

    private List<Integer> getRecord(final String recordName, final int tileCount) {
        try {
            RecordManager manager = getRecordManager();
            List<Integer> get = manager.<Integer, List<Integer>>hashMap(recordName).get(tileCount);
            manager.close();
            if (get == null) {
                get = new LinkedList<>();
            }
            return get;
        } catch (IOException ex) {
            Logger.getLogger(ImagePuzzleModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new LinkedList<>();
    }

    private void loadDelayedTiles() {
        delayedTiles = getRecord(imgFile.getAbsolutePath(), numberOfTiles);
    }

    private void saveDelayedTiles() {
        try {
            RecordManager manager = getRecordManager();
            PrimaryHashMap<Integer, List<Integer>> delayedTilesStorage = manager.<Integer, List<Integer>>hashMap(imgFile.getAbsolutePath());
            delayedTilesStorage.put(numberOfTiles, delayedTiles);
            manager.commit();
            manager.close();
        } catch (IOException ex) {
            Logger.getLogger(ImagePuzzleModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public enum GameState {

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
    private int numberOfTiles = 8 * 7;
    private Image rawImage;
    private int numberOfTilesHorizontal = 7;
    private List<PropertyChangeListener> propertyListener;
    private List<StateChangeListener> stateListener;
    private BoundedRangeModel speedModel;
    private Map<Integer, Team> teams;

    public class Team {

        public final Color color;
        public final String name;

        public Team(Color color, String name) {
            this.color = color;
            this.name = name;
        }
    }

    public BoundedRangeModel getSpeedModel() {
        return speedModel;
    }

    public ImagePuzzleModel() {
        propertyListener = new LinkedList<>();
        stateListener = new LinkedList<>();
        revealSequence = new LinkedList<>();
        delayedTiles = new LinkedList<>();
        state = GameState.UNINITIALIZED;
        teams = new HashMap<>();
        teams.put(0, new Team(new Color(23, 156, 125), "Team Grün"));
        teams.put(1, new Team(new Color(235, 106, 10), "Team Gelb"));
        blockedTeams = new HashMap<>();
        progressModel = new DefaultBoundedRangeModel(0, 0, 0, numberOfTiles);
        speedModel = new DefaultBoundedRangeModel(10, 0, 1, 12);
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

    private void fireStateChange(GameState newState) {
        StateChangeEvent evt = new StateChangeEvent(this, newState);
        for (StateChangeListener l : stateListener) {
            l.stateChanged(evt);
        }
    }
    private GameState state;

    private void startRevealTimer() {

        if (state == GameState.READY || state == GameState.REVEALED) {

            revealTimer = new Timer(getSpeedAsDelay(), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("timer timed");
                    revealNext();
                }
            });
            revealTimer.setInitialDelay(getSpeedAsDelay() / 2);
            revealTimer.start();
            setState(GameState.RUNNING);

        } else if (state == GameState.HALTED) {

            // assume timer exists
            revealTimer.setInitialDelay(0);
            revealTimer.start();
            setState(GameState.RUNNING);

        } else {
            // ignore others
        }

    }

    private void stopRevealTimer() {

        if (state == GameState.RUNNING) {

            revealTimer.stop();
            setState(GameState.HALTED);

        }

    }

    private void resetTimer() {

        if (state == GameState.HALTED) {

            revealTimer = null;
            setState(GameState.READY);

        }

    }

    public int getNumberOfTilesVertical() {
        return numberOfTilesVertical;
    }

    public void setImage(final File imgFile) {

        if (imgFile != null && this.imgFile != imgFile && imgFile.exists() && imgFile.isFile() && imgFile.getName().toLowerCase().endsWith("jpg")) {

            stop();
            setState(GameState.UNINITIALIZED);
            reset();

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
                        loadDelayedTiles();
                        initRevealSequence();
                        setState(GameState.READY);
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
        resetBuzzTimer();
        stopRevealTimer();
        setState(GameState.REVEALED);
    }

    public int getSpeedAsDelay() {
        return speedModel.getValue() * 200;
    }
    private List<Integer> revealSequence;

    public synchronized int getNumberOfBlockedTilesForTeam(int teamId) {
        if (!blockedTeams.containsKey(teamId)) {
            return 0;
        }
        return durationBlocked - (blockedTeams.get(teamId) - revealSequence.size()) + 1;
    }

    synchronized void revealNext() {

        if (!revealSequence.isEmpty()) {
            int r = revealSequence.remove(0);
            progressModel.setValue(numberOfTiles - revealSequence.size());
            System.out.println(String.format("revealed item %d", r));

            // count down on team block timer
            for (Iterator<Integer> i = blockedTeams.values().iterator(); i.hasNext();) {
                if (i.next() - durationBlocked > revealSequence.size()) {
                    // remove block information if down to zero
                    i.remove();
                }
            }

            if (revealSequence.isEmpty()) {
                stopRevealTimer();
            } else {
                fireStateChange(GameState.RUNNING);
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
        delayedTiles.clear();
        resetBuzzTimer();
        stop();
        initRevealSequence();
        resetTimer();
    }
    
    private void resetBuzzTimer() {
        if (buzzTimer != null) {
            buzzTimer.stop();
        }
        blockedTeams.clear();
    }

    public boolean isDelayedTile(int tileNumber) {
        return delayedTiles.contains(tileNumber);
    }

    private void initRevealSequence() {
        revealSequence = new LinkedList<>();
        for (int i = 0; i < numberOfTiles; i++) {
            // ignore delayed tile numbers
            if (!delayedTiles.contains(i)) {
                revealSequence.add(i);
            }
        }
        Collections.shuffle(revealSequence);
        // add delayed tile numbers
        List<Integer> delayedCopy = new LinkedList<>(delayedTiles);
        Collections.shuffle(delayedCopy);
        revealSequence.addAll(delayedCopy);
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

    private void setState(GameState state) {
        this.state = state;
        fireStateChange(state);
    }

    Image getRawImage() {
        return rawImage;
    }

    List<Integer> getRevealSequence() {
        return Collections.unmodifiableList(revealSequence);
    }

    public Team getTeamLastBuzzed() {
        return teams.get(lastBuzzed);
    }

    public Team getTeam(int id) {
        return teams.get(id);
    }

    /**
     * Buzz for one team
     *
     * @param id
     */
    public synchronized void buzz(int id) {

        if ((state != GameState.RUNNING && state != GameState.READY) || !teams.containsKey(id) || blockedTeams.containsKey(id)) {
            System.err.println("buzz can only be called in ready|running state, and team must exist and should not be blocked.");
            return;
        }

        System.out.println(String.format("party %s (#%d) has buzzed", teams.get(id), id));
        lastBuzzed = id;
        executeBuzz();

    }
}
