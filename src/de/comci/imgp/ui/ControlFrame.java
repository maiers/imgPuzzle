/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.comci.imgp.ui;

import de.comci.imgp.ImagePuzzle;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ListModel;

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
    private Action setFullScreenGraphicsDevice = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            
            if (e.getSource() instanceof JCheckBoxMenuItem) {
                JCheckBoxMenuItem check = (JCheckBoxMenuItem)e.getSource();
                if (check.isSelected()) {
                    String id = e.getActionCommand();
                    for (GraphicsDevice d : getScreenDevices()) {
                        if (d.getIDstring().equals(id)) {
                            setFullScreenTarget(d);
                            return;
                        }
                    }
                } else {
                    setFullScreenTarget(null);
                }
            }
            
        }
        
    };
    private GraphicsDevice fullScreenTarget;
    private FullscreenFrame fs;

    public GraphicsDevice getFullScreenTarget() {
        return fullScreenTarget;
    }

    public void setFullScreenTarget(GraphicsDevice newTargetDevice) {
        
        if (this.fullScreenTarget != newTargetDevice) {
            
            if (this.fullScreenTarget != null) {
                // remove
                fs.setVisible(false);
                this.fullScreenTarget.setFullScreenWindow(null);                
            }
            
            this.fullScreenTarget = newTargetDevice;
            if (newTargetDevice != null) {
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

    /**
     * Creates new form ControlFrame
     */
    public ControlFrame(ImagePuzzle aThis) {
        dc = aThis;
        updateList();
        initComponents();
    }

    private List<GraphicsDevice> getScreenDevices() {
        
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        List<GraphicsDevice> devices = Arrays.asList(g.getScreenDevices());

        for (GraphicsDevice d : devices) {
            System.out.println(String.format("%s [%d:%d]", d.getIDstring(), d.getDisplayMode().getWidth(), d.getDisplayMode().getHeight()));
        }
        
        return devices;

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
        splitPaneMain = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        imageFileList = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        imagePuzzlePanel1 = new de.comci.imgp.ui.ImagePuzzlePanel();
        imagePuzzlePanel1.setTransparent(true);
        jPanel3 = new javax.swing.JPanel();
        sliderSpeed = new javax.swing.JSlider();
        labelSlider = new javax.swing.JLabel();
        buttonRevealImage = new javax.swing.JButton();
        progressBarPictureVisible = new javax.swing.JProgressBar();
        buttonNextImage = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        mainMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        buttonFiles = new javax.swing.JMenuItem();
        buttonExit = new javax.swing.JMenuItem();
        settingsMenu = new javax.swing.JMenu();
        tileMenu = new javax.swing.JMenu();
        screenMenu = new javax.swing.JMenu();
        menuAbout = new javax.swing.JMenu();

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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        splitPaneMain.setDividerLocation(200);

        jPanel2.setMinimumSize(new java.awt.Dimension(200, 400));

        imageFileList.setModel(getListModel());
        imageFileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
        );

        splitPaneMain.setLeftComponent(jPanel2);

        javax.swing.GroupLayout imagePuzzlePanel1Layout = new javax.swing.GroupLayout(imagePuzzlePanel1);
        imagePuzzlePanel1.setLayout(imagePuzzlePanel1Layout);
        imagePuzzlePanel1Layout.setHorizontalGroup(
            imagePuzzlePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 552, Short.MAX_VALUE)
        );
        imagePuzzlePanel1Layout.setVerticalGroup(
            imagePuzzlePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 405, Short.MAX_VALUE)
        );

        sliderSpeed.setMajorTickSpacing(1);
        sliderSpeed.setMaximum(10);
        sliderSpeed.setMinimum(1);
        sliderSpeed.setMinorTickSpacing(1);
        sliderSpeed.setPaintLabels(true);
        sliderSpeed.setPaintTicks(true);
        sliderSpeed.setSnapToTicks(true);
        sliderSpeed.setInverted(true);
        sliderSpeed.setModel(imagePuzzlePanel1.getModel().getSpeedModel());
        Hashtable<Integer,JComponent> labelTable = new Hashtable<>();
        labelTable.put( new Integer( 1 ), new JLabel("Pretty fast") );
        labelTable.put( new Integer( 5 ), new JLabel("Average") );
        labelTable.put( new Integer( 10 ), new JLabel("Really slow") );
        sliderSpeed.setLabelTable( labelTable );

        labelSlider.setText("Speed");

        buttonRevealImage.setText("Reveal");
        buttonRevealImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRevealImageActionPerformed(evt);
            }
        });

        progressBarPictureVisible.setModel(imagePuzzlePanel1.getModel().getProgressModel());

        buttonNextImage.setText("Next");
        buttonNextImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNextImageActionPerformed(evt);
            }
        });

        jButton1.setText("Start");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Stop");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonRevealImage, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressBarPictureVisible, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonNextImage, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(sliderSpeed, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(progressBarPictureVisible, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonNextImage, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonRevealImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(labelSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 8, Short.MAX_VALUE))
                    .addComponent(sliderSpeed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        splitPaneMain.setRightComponent(jPanel1);

        fileMenu.setText("File");

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

        settingsMenu.setText("Settings");

        tileMenu.setText("Tiles");
        for (int i = 3; i < 7; i++) {
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

        screenMenu.setText("Output");
        for (GraphicsDevice d : getScreenDevices()) {
            JCheckBoxMenuItem btn = new JCheckBoxMenuItem(setFullScreenGraphicsDevice);
            screenButtonGroup.add(btn);
            btn.setText(String.format("%s [%d:%d]", d.getIDstring(), d.getDisplayMode().getWidth(), d.getDisplayMode().getHeight()));
            btn.setActionCommand(d.getIDstring());
            btn.setSelected(d.getIDstring().equals(getFullScreenTarget()));
            screenMenu.add(btn);
        }
        settingsMenu.add(screenMenu);

        mainMenuBar.add(settingsMenu);

        menuAbout.setText("About");
        mainMenuBar.add(menuAbout);

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
        selectDirectoryDialog.setMinimumSize(new Dimension(600, 500));
        selectDirectoryDialog.setVisible(true);
    }//GEN-LAST:event_buttonFilesActionPerformed

    private void directoryChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directoryChooserActionPerformed
        dc.scanDirectory(directoryChooser.getSelectedFile().toPath());
        selectDirectoryDialog.setVisible(false);
        updateList();
    }//GEN-LAST:event_directoryChooserActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        System.exit(0);
    }//GEN-LAST:event_formWindowClosed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        imagePuzzlePanel1.getModel().start();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        imagePuzzlePanel1.getModel().stop();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void buttonRevealImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRevealImageActionPerformed
        imagePuzzlePanel1.getModel().reveal();
    }//GEN-LAST:event_buttonRevealImageActionPerformed

    private void imageFileListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_imageFileListValueChanged
        imagePuzzlePanel1.getModel().setImage((File)imageFileList.getSelectedValue());
    }//GEN-LAST:event_imageFileListValueChanged

    private void buttonNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNextImageActionPerformed
        int i = imageFileList.getSelectedIndex();
        if (i + 1 < imageFileList.getModel().getSize());
        imageFileList.setSelectedIndex(i+1);
    }//GEN-LAST:event_buttonNextImageActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem buttonExit;
    private javax.swing.JMenuItem buttonFiles;
    private javax.swing.JButton buttonNextImage;
    private javax.swing.JButton buttonRevealImage;
    private javax.swing.JFileChooser directoryChooser;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JList imageFileList;
    private de.comci.imgp.ui.ImagePuzzlePanel imagePuzzlePanel1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelSlider;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JMenu menuAbout;
    private javax.swing.JProgressBar progressBarPictureVisible;
    private javax.swing.ButtonGroup screenButtonGroup;
    private javax.swing.JMenu screenMenu;
    private javax.swing.JDialog selectDirectoryDialog;
    private javax.swing.JMenu settingsMenu;
    private javax.swing.JSlider sliderSpeed;
    private javax.swing.JSplitPane splitPaneMain;
    private javax.swing.ButtonGroup tileButtonGroup;
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
}