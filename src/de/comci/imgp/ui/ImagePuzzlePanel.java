/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.comci.imgp.ui;

import static de.comci.imgp.ui.ImageUtil.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/**
 *
 * @author Sebastian
 */
public class ImagePuzzlePanel extends javax.swing.JPanel implements PropertyChangeListener, StateChangeListener {

    private ImagePuzzleModel model;
    private boolean showImage = false;
    private Color[] colors = {
        Color.RED,
        Color.BLACK
    };

    public boolean isTransparent() {
        return showImage;
    }

    public void setTransparent(boolean transparent) {
        if (this.showImage != transparent) {
            this.showImage = transparent;
            repaint();
        }        
    }

    public ImagePuzzleModel getModel() {
        return model;
    }

    public void setModel(ImagePuzzleModel model) {
        this.model = model;
        model.addPropertyChangeListener(this);
        model.addStateChangeListener(this);
    }
    private Image scaledImage;

    public ImagePuzzlePanel() {
        initComponents();
        model = new ImagePuzzleModel();
        model.setNumberOfTiles(6, 6);
        setModel(model);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (scaledImage != null) {
            g.drawImage(scaledImage, 0, 0, this);
        }
        List<Integer> seq = model.getRevealSequence();
        if (seq != null) {

            // draw hiding tiles
            int r, c, x, y;
            int w = getWidth() / model.getNumberOfTilesHorizontal(),
                h = getHeight() / model.getNumberOfTilesVertical();

            while (w * model.getNumberOfTilesHorizontal() < getWidth()) {
                w++;
            }
            while (h * model.getNumberOfTilesVertical() < getHeight()) {
                h++;
            }

            g.setColor(getColor());
            for (int tile : seq) {
                r = (int) Math.floor(tile / model.getNumberOfTilesHorizontal());
                c = tile % model.getNumberOfTilesHorizontal();
                x = c * w;
                y = r * h;
                //g.setColor((r % 2 + c % 2 - 1 == 0) ? colors[0] : colors[1]);
                g.fillRect(x, y, w, h);
            }
        }

    }
    
    private Color getColor() {
        if (showImage) {
            return new Color(model.getTileColor().getRed(), model.getTileColor().getGreen(), model.getTileColor().getBlue(), 120);
        } else {
            return model.getTileColor();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        scaleImage(model.getRawImage());
    }//GEN-LAST:event_formComponentResized

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    private void scaleImage(final Image image) {

        if (image == null) {
            System.out.println("Tried to scale a null image");
            return;
        }

        new SwingWorker<Image, Void>() {
            @Override
            protected Image doInBackground() throws Exception {

                double scaleFactor = Math.min(1d, getScaleFactorToFill(new Dimension(image.getWidth(ImagePuzzlePanel.this), image.getHeight(ImagePuzzlePanel.this)), getSize()));

                int scaleWidth = (int) Math.round(image.getWidth(ImagePuzzlePanel.this) * scaleFactor);
                int scaleHeight = (int) Math.round(image.getHeight(ImagePuzzlePanel.this) * scaleFactor);

                return image.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_FAST);
            }

            @Override
            protected void done() {
                try {
                    scaledImage = get();
                    repaint();
                } catch (InterruptedException ignore) {
                } catch (ExecutionException e) {
                }
            }
        }.execute();

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
    }

    @Override
    public void stateChanged(String newState) {

        System.out.println("received " + newState + " state");
        
        switch (newState) {
            case ImagePuzzleModel.STATE_READY:
                scaleImage(model.getRawImage());
                break;
            case ImagePuzzleModel.STATE_RUNNING:
                repaint();
                break;
            case ImagePuzzleModel.STATE_HALTED:
                repaint();
                break;
            case ImagePuzzleModel.STATE_REVEALED:
                repaint();
                break;
        }

    }
    
    public void setImage(File image) {
        model.setImage(image);
    }
    
}