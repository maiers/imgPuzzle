/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.comci.imgp.ui;

import de.comci.imgp.ImagePuzzle;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingConstants;

/**
 *
 * @author Sebastian
 */
public class ControlFrame extends javax.swing.JFrame {

    private final ImagePuzzle dc;
    private DefaultListModel<File> files = new DefaultListModel<>();
    private Action setTileCount = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

            String cmd = tileButtonGroup.getSelection().getActionCommand();
            String[] split = cmd.split(",");
            int rows = Integer.parseInt(split[0]),
                    columns = Integer.parseInt(split[1]);

            imagePuzzlePanel1.getModel().setNumberOfTiles(rows, columns);

        }
    };
    private Action setAnswerDuration = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            imagePuzzlePanel1.getModel().setAnswerDuration(Integer.parseInt(cmd));
        }
        
    };
    private Action setFullScreenGraphicsDevice = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() instanceof JCheckBoxMenuItem) {
                JCheckBoxMenuItem check = (JCheckBoxMenuItem) e.getSource();

                String id = e.getActionCommand();
                for (GraphicsDevice d : getScreenDevices()) {
                    if (d.getIDstring().equals(id) && !d.equals(fullScreenTarget)) {
                        setFullScreenTarget(d);
                        return;
                    }
                }

                screenButtonGroup.clearSelection();
                setFullScreenTarget(null);

            }

        }
    };
    private GraphicsDevice fullScreenTarget;
    private FullscreenFrame fs;
    private final File defaultPictureDir;

    public ControlFrame(ImagePuzzle aThis, File pictureDir) {
        dc = aThis;
        defaultPictureDir = pictureDir;
        updateList();
        initComponents();
        
        ImagePuzzleSounds soundsOnKlick = new ImagePuzzleSounds(imagePuzzlePanel1.getModel());
        imagePuzzlePanel1.getModel().addStateChangeListener(soundsOnKlick);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            
            @Override
            public boolean dispatchKeyEvent(KeyEvent evt) {

                // only key pressed events
                if (evt.getID() != KeyEvent.KEY_PRESSED) {
                    return false;
                }
                
                switch (evt.getKeyCode()) {
                    case 83:
                    case 81:
                    case 87:
                    case 69:
                    case 65:
                    case 68:
                    case 89:
                    case 88:
                    case 67:
                        partyBuzzed(0);
                        break;
                    case 46:
                    case 44:
                    case 77:
                    case 76:
                    case 75:
                    case 74:
                    case 79:
                    case 73:
                    case 85:
                        partyBuzzed(1);
                        break;
                }

                return false;
            }
        });

    }

    private void partyBuzzed(int id) {
        imagePuzzlePanel1.getModel().buzz(id);
    }

    public GraphicsDevice getFullScreenTarget() {
        return fullScreenTarget;
    }

    public void setFullScreenTarget(GraphicsDevice newTargetDevice) {

        System.out.println("setFullScreenTarget");

        if (this.fullScreenTarget != newTargetDevice) {

            if (fs != null) {
                // remove
                fs.setVisible(false);
                fs.dispose();
                if (fullScreenTarget != null) {
                    fullScreenTarget.setFullScreenWindow(null);
                }
                fullScreenTarget = null;
            }


            if (newTargetDevice != null) {
                // set new target
                this.fullScreenTarget = newTargetDevice;
                fs = new FullscreenFrame();
                fs.setImagePuzzleModel(imagePuzzlePanel1.getModel());
                fs.setSize(newTargetDevice.getDisplayMode().getWidth(), newTargetDevice.getDisplayMode().getHeight());
                fs.setLocation(newTargetDevice.getDefaultConfiguration().getBounds().x, newTargetDevice.getDefaultConfiguration().getBounds().y);
                try {
                    newTargetDevice.setFullScreenWindow(fs);
                } finally {
                    newTargetDevice.setFullScreenWindow(null);
                }
            }

        }

    }

    private List<GraphicsDevice> getScreenDevices() {
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        List<GraphicsDevice> devices = Arrays.asList(g.getScreenDevices());
        return devices;
    }

    private ListCellRenderer<File> getFileListCellRenderer() {

        return new ListCellRenderer<File>() {
            JPanel panel = new JPanel();
            JLabel text = new JLabel("", SwingConstants.LEFT);
            Color selectionColor = new Color(200, 200, 200);

            {
                panel.add(text, BorderLayout.CENTER);
            }

            @Override
            public Component getListCellRendererComponent(JList<? extends File> list, File value, int index, boolean isSelected, boolean cellHasFocus) {

                text.setText(value.getName());
                Color color = (isSelected) ? selectionColor : Color.white;
                //color = (cellHasFocus) ? focusColor : color;
                panel.setBackground(color);
                return panel;

            }
        };

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        selectDirectoryDialog = new javax.swing.JDialog();
        directoryChooser = new javax.swing.JFileChooser();
        tileButtonGroup = new javax.swing.ButtonGroup();
        screenButtonGroup = new javax.swing.ButtonGroup();
        aboutDialog = new javax.swing.JDialog();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        tileColorChooserDialog = new javax.swing.JDialog();
        tileColorChooser = new javax.swing.JColorChooser();
        colorChooserOkBUtton = new javax.swing.JButton();
        colorChooserAbortButton = new javax.swing.JButton();
        answerDurationButtonGroup = new javax.swing.ButtonGroup();
        splitPaneMain = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        imageFileList = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        imagePuzzlePanel1 = new de.comci.imgp.ui.ImagePuzzlePanel();
        imagePuzzlePanel1.setTransparent(true);

        imagePuzzlePanel1.getModel().addStateChangeListener(getButtonControlByModelState());
        controlPanel = new javax.swing.JPanel();
        sliderSpeed = new javax.swing.JSlider();
        labelSlider = new javax.swing.JLabel();
        buttonRevealImage = new javax.swing.JButton();
        progressBarPictureVisible = new javax.swing.JProgressBar();
        buttonNextImage = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();
        butonStop = new javax.swing.JButton();
        mainMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        buttonFiles = new javax.swing.JMenuItem();
        buttonExit = new javax.swing.JMenuItem();
        settingsMenu = new javax.swing.JMenu();
        screenMenu = new javax.swing.JMenu();
        tileMenu = new javax.swing.JMenu();
        durationMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        directoryChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directoryChooserActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout selectDirectoryDialogLayout = new javax.swing.GroupLayout(selectDirectoryDialog.getContentPane());
        selectDirectoryDialog.getContentPane().setLayout(selectDirectoryDialogLayout);
        selectDirectoryDialogLayout.setHorizontalGroup(
            selectDirectoryDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(directoryChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
        );
        selectDirectoryDialogLayout.setVerticalGroup(
            selectDirectoryDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(directoryChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
        );

        aboutDialog.setTitle("About");
        aboutDialog.setAlwaysOnTop(true);
        aboutDialog.setLocationByPlatform(true);
        aboutDialog.setMinimumSize(new java.awt.Dimension(500, 350));
        aboutDialog.setModal(true);
        aboutDialog.setModalExclusionType(null);
        aboutDialog.setName("aboutDialog"); // NOI18N
        aboutDialog.setResizable(false);

        jTextPane1.setEditable(false);
        jTextPane1.setText("ImagePuzzle: A simple puzzle game.\nCopyright (C) 2012  Sebastian Maier\n\nThis program is free software: you can redistribute it and/or modify\nit under the terms of the GNU General Public License as published by\nthe Free Software Foundation, either version 3 of the License, or\n(at your option) any later version.\n\nThis program is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU General Public License for more details.\n\nYou should have received a copy of the GNU General Public License\nalong with this program.  If not, see <http://www.gnu.org/licenses/>.\n\nUses Sound Effects from: http://www.pacdv.com/sounds/index.html\n\nLibraries:\nhttp://code.google.com/p/jdbm2/"); // NOI18N
        jScrollPane2.setViewportView(jTextPane1);

        javax.swing.GroupLayout aboutDialogLayout = new javax.swing.GroupLayout(aboutDialog.getContentPane());
        aboutDialog.getContentPane().setLayout(aboutDialogLayout);
        aboutDialogLayout.setHorizontalGroup(
            aboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                .addContainerGap())
        );
        aboutDialogLayout.setVerticalGroup(
            aboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addContainerGap())
        );

        tileColorChooserDialog.setModal(true);
        tileColorChooserDialog.setResizable(false);

        colorChooserOkBUtton.setText("Ok");

        colorChooserAbortButton.setText("Cancel");

        javax.swing.GroupLayout tileColorChooserDialogLayout = new javax.swing.GroupLayout(tileColorChooserDialog.getContentPane());
        tileColorChooserDialog.getContentPane().setLayout(tileColorChooserDialogLayout);
        tileColorChooserDialogLayout.setHorizontalGroup(
            tileColorChooserDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tileColorChooserDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tileColorChooserDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tileColorChooserDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(colorChooserOkBUtton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(colorChooserAbortButton))
                    .addComponent(tileColorChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE))
                .addContainerGap())
        );
        tileColorChooserDialogLayout.setVerticalGroup(
            tileColorChooserDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tileColorChooserDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tileColorChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tileColorChooserDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(colorChooserAbortButton)
                    .addComponent(colorChooserOkBUtton))
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        splitPaneMain.setDividerLocation(200);

        jPanel2.setMinimumSize(new java.awt.Dimension(200, 400));

        imageFileList.setModel(getListModel());
        imageFileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        imageFileList.setCellRenderer(getFileListCellRenderer());
        imageFileList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                imageFileListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(imageFileList);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
        );

        splitPaneMain.setLeftComponent(jPanel2);

        javax.swing.GroupLayout imagePuzzlePanel1Layout = new javax.swing.GroupLayout(imagePuzzlePanel1);
        imagePuzzlePanel1.setLayout(imagePuzzlePanel1Layout);
        imagePuzzlePanel1Layout.setHorizontalGroup(
            imagePuzzlePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 574, Short.MAX_VALUE)
        );
        imagePuzzlePanel1Layout.setVerticalGroup(
            imagePuzzlePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 457, Short.MAX_VALUE)
        );

        sliderSpeed.setMajorTickSpacing(1);
        sliderSpeed.setMaximum(10);
        sliderSpeed.setMinimum(1);
        sliderSpeed.setMinorTickSpacing(1);
        sliderSpeed.setPaintLabels(true);
        sliderSpeed.setPaintTicks(true);
        sliderSpeed.setSnapToTicks(true);
        sliderSpeed.setEnabled(false);
        sliderSpeed.setInverted(true);
        sliderSpeed.setModel(imagePuzzlePanel1.getModel().getSpeedModel());
        Hashtable<Integer,JComponent> labelTable = new Hashtable<>();
        labelTable.put( new Integer( 1 ), new JLabel("Pretty fast") );
        labelTable.put( new Integer( 5 ), new JLabel("Average") );
        labelTable.put( new Integer( 10 ), new JLabel("Really slow") );
        sliderSpeed.setLabelTable( labelTable );

        labelSlider.setText("Speed");

        buttonRevealImage.setText("Reveal");
        buttonRevealImage.setEnabled(false);
        buttonRevealImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRevealImageActionPerformed(evt);
            }
        });

        progressBarPictureVisible.setModel(imagePuzzlePanel1.getModel().getProgressModel());

        buttonNextImage.setText("Next");
        buttonNextImage.setEnabled(false);
        buttonNextImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNextImageActionPerformed(evt);
            }
        });

        buttonStart.setText("Start");
        buttonStart.setEnabled(false);
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });

        butonStop.setText("Stop");
        butonStop.setEnabled(false);
        butonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butonStopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout controlPanelLayout = new javax.swing.GroupLayout(controlPanel);
        controlPanel.setLayout(controlPanelLayout);
        controlPanelLayout.setHorizontalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonStart, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(controlPanelLayout.createSequentialGroup()
                        .addComponent(butonStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonRevealImage, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressBarPictureVisible, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonNextImage, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(sliderSpeed, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)))
        );
        controlPanelLayout.setVerticalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(progressBarPictureVisible, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonNextImage, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(buttonStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonRevealImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butonStop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(controlPanelLayout.createSequentialGroup()
                        .addComponent(labelSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 8, Short.MAX_VALUE))
                    .addComponent(sliderSpeed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(controlPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imagePuzzlePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imagePuzzlePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(controlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        splitPaneMain.setRightComponent(jPanel1);

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");

        buttonFiles.setMnemonic('d');
        buttonFiles.setText("Open directory");
        buttonFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonFilesActionPerformed(evt);
            }
        });
        fileMenu.add(buttonFiles);

        buttonExit.setMnemonic('x');
        buttonExit.setText("Exit");
        buttonExit.setToolTipText("Exit the application");
        buttonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExitActionPerformed(evt);
            }
        });
        fileMenu.add(buttonExit);

        mainMenuBar.add(fileMenu);

        settingsMenu.setMnemonic('s');
        settingsMenu.setText("Settings");

        screenMenu.setMnemonic('f');
        screenMenu.setText("Fullscreen");
        for (GraphicsDevice d : getScreenDevices()) {
            JCheckBoxMenuItem btn = new JCheckBoxMenuItem(setFullScreenGraphicsDevice);
            screenButtonGroup.add(btn);
            btn.setText(String.format("%s [%d:%d]", d.getIDstring(), d.getDisplayMode().getWidth(), d.getDisplayMode().getHeight()));
            btn.setActionCommand(d.getIDstring());
            btn.setSelected(d.getIDstring().equals(getFullScreenTarget()));
            screenMenu.add(btn);
        }
        settingsMenu.add(screenMenu);

        tileMenu.setMnemonic('n');
        tileMenu.setText("Number of Tiles");
        for (int i = 3; i < 10; i++) {
            for (int j = 0; j < 2; j++) {
                JRadioButtonMenuItem btn = new JRadioButtonMenuItem(setTileCount);
                tileButtonGroup.add(btn);
                btn.setText("" + i * (i+j));
                btn.setActionCommand(i + "," + (i+j));
                btn.setSelected(imagePuzzlePanel1.getModel().getNumberOfTiles() == i * (i+j));
                tileMenu.add(btn);
            }
        }
        settingsMenu.add(tileMenu);

        durationMenu.setMnemonic('a');
        durationMenu.setText("Answer duration");
        for (int i = 1; i <= 10; i++) {
            JRadioButtonMenuItem btn = new JRadioButtonMenuItem(setAnswerDuration);
            answerDurationButtonGroup.add(btn);
            btn.setText(String.format("%d seconds", i));
            btn.setActionCommand("" + i*1000);
            btn.setSelected(imagePuzzlePanel1.getModel().getAnswerDuration() == i * 1000);
            durationMenu.add(btn);
        }
        settingsMenu.add(durationMenu);

        mainMenuBar.add(settingsMenu);

        helpMenu.setMnemonic('?');
        helpMenu.setText("?");
        helpMenu.setName(""); // NOI18N

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        mainMenuBar.add(helpMenu);

        setJMenuBar(mainMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPaneMain)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPaneMain)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_buttonExitActionPerformed

    private void buttonFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonFilesActionPerformed
        directoryChooser.setCurrentDirectory(defaultPictureDir);
        selectDirectoryDialog.setMinimumSize(new Dimension(600, 500));
        Point pos = this.getLocation();
        pos.translate(20, 20);
        selectDirectoryDialog.setLocation(pos);
        selectDirectoryDialog.setVisible(true);
    }//GEN-LAST:event_buttonFilesActionPerformed

    private void directoryChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directoryChooserActionPerformed
        System.out.println(evt.getActionCommand());
        if (evt.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
            selectDirectoryDialog.setVisible(false);
        } else {
            if (directoryChooser.getSelectedFile() != null) {
                dc.scanDirectory(directoryChooser.getSelectedFile().toPath());
                selectDirectoryDialog.setVisible(false);
                updateList();
            }
        }
    }//GEN-LAST:event_directoryChooserActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        System.exit(0);
    }//GEN-LAST:event_formWindowClosed

    private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
        imagePuzzlePanel1.getModel().start();
    }//GEN-LAST:event_buttonStartActionPerformed

    private void butonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butonStopActionPerformed
        imagePuzzlePanel1.getModel().stop();
    }//GEN-LAST:event_butonStopActionPerformed

    private void buttonRevealImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRevealImageActionPerformed
        imagePuzzlePanel1.getModel().reveal();
    }//GEN-LAST:event_buttonRevealImageActionPerformed

    private void imageFileListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_imageFileListValueChanged
        imagePuzzlePanel1.getModel().setImage((File) imageFileList.getSelectedValue());
    }//GEN-LAST:event_imageFileListValueChanged

    private void buttonNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNextImageActionPerformed
        int i = imageFileList.getSelectedIndex();
        if (i + 1 < imageFileList.getModel().getSize());
        imageFileList.setSelectedIndex(i + 1);
    }//GEN-LAST:event_buttonNextImageActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        Point p = getLocation();
        p.translate(50, 50);
        aboutDialog.setLocation(p);
        aboutDialog.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog aboutDialog;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.ButtonGroup answerDurationButtonGroup;
    private javax.swing.JButton butonStop;
    private javax.swing.JMenuItem buttonExit;
    private javax.swing.JMenuItem buttonFiles;
    private javax.swing.JButton buttonNextImage;
    private javax.swing.JButton buttonRevealImage;
    private javax.swing.JButton buttonStart;
    private javax.swing.JButton colorChooserAbortButton;
    private javax.swing.JButton colorChooserOkBUtton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JFileChooser directoryChooser;
    private javax.swing.JMenu durationMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JList imageFileList;
    private de.comci.imgp.ui.ImagePuzzlePanel imagePuzzlePanel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JLabel labelSlider;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JProgressBar progressBarPictureVisible;
    private javax.swing.ButtonGroup screenButtonGroup;
    private javax.swing.JMenu screenMenu;
    private javax.swing.JDialog selectDirectoryDialog;
    private javax.swing.JMenu settingsMenu;
    private javax.swing.JSlider sliderSpeed;
    private javax.swing.JSplitPane splitPaneMain;
    private javax.swing.ButtonGroup tileButtonGroup;
    private javax.swing.JColorChooser tileColorChooser;
    private javax.swing.JDialog tileColorChooserDialog;
    private javax.swing.JMenu tileMenu;
    // End of variables declaration//GEN-END:variables

    private void updateList() {

        files.clear();

        for (File f : dc.getFiles()) {
            files.addElement(f);
        }

    }

    public ListModel<File> getListModel() {
        return files;
    }

    private StateChangeListener getButtonControlByModelState() {

        return new StateChangeListener() {
            
            @Override
            public void stateChanged(StateChangeEvent evt) {

                switch (evt.getNewState()) {
                    case UNINITIALIZED:
                        buttonStart.setEnabled(false);
                        butonStop.setEnabled(false);
                        buttonRevealImage.setEnabled(false);
                        buttonNextImage.setEnabled(false);
                        sliderSpeed.setEnabled(false);
                        break;
                    case READY:
                        buttonStart.setEnabled(true);
                        butonStop.setEnabled(false);
                        buttonRevealImage.setEnabled(false);
                        buttonNextImage.setEnabled(true);
                        sliderSpeed.setEnabled(true);
                        break;
                    case RUNNING:
                        buttonStart.setEnabled(false);
                        butonStop.setEnabled(true);
                        buttonRevealImage.setEnabled(true);
                        buttonNextImage.setEnabled(true);
                        sliderSpeed.setEnabled(true);
                        break;
                    case HALTED:
                        buttonStart.setEnabled(true);
                        butonStop.setEnabled(false);
                        buttonRevealImage.setEnabled(true);
                        buttonNextImage.setEnabled(true);
                        sliderSpeed.setEnabled(true);
                        break;
                    case BUZZED:
                        buttonStart.setEnabled(false);
                        butonStop.setEnabled(false);
                        buttonRevealImage.setEnabled(true);
                        buttonNextImage.setEnabled(false);
                        sliderSpeed.setEnabled(false);
                        break;
                    case REVEALED:
                        buttonStart.setEnabled(true);
                        butonStop.setEnabled(false);
                        buttonRevealImage.setEnabled(false);
                        buttonNextImage.setEnabled(true);
                        sliderSpeed.setEnabled(true);
                        break;
                }

            }
        };

    }
    
}
