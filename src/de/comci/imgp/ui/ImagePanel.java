// Fraunhofer Institute for Computer Graphics Research (IGD)
// Competence Center for Information Visualization and Visual Analytics
//
// Copyright (c) 2011-2012 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.
package de.comci.imgp.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.SwingWorker;
import static de.comci.imgp.ui.ImageUtil.*;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sebastian Maier
 */
public class ImagePanel extends javax.swing.JPanel {

    private Image rawImage, scaledImage;
    
    
    /**
     * Creates new form ImagePanel
     */
    public ImagePanel() {
        initComponents();
        addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                resizeImage(rawImage);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void componentShown(ComponentEvent e) {
                resizeImage(rawImage);
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }

    public Image getRawImage() {
        return rawImage;
    }

    public void setRawImage(Image rawImage) {
        this.rawImage = rawImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (scaledImage != null) {
            g.drawImage(scaledImage, 0, 0, this);
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    private void resizeImage(final Image rawImage) {
        SwingWorker<Image, Void> w = new SwingWorker<Image, Void>() {

            private final Rectangle bounds = ImagePanel.this.getBounds();
            
            @Override
            protected Image doInBackground() throws Exception {
                
                int w = rawImage.getWidth(ImagePanel.this),
                    h = rawImage.getHeight(ImagePanel.this);
                
                double scaleFactor = Math.min(1d, getScaleFactorToFill(new Dimension(w, h), getSize()));

                int scaleWidth = (int) Math.round(w * scaleFactor);
                int scaleHeight = (int) Math.round(h * scaleFactor);
                
                return rawImage.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_FAST);
                
            }

            @Override
            protected void done() {
                super.done();
                
                try {
                    scaledImage = get();
                    repaint();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ImagePanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(ImagePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
            
        };
        w.execute();
        
    }
    
    
    
}
