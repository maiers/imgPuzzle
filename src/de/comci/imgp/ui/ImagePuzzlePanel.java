/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.comci.imgp.ui;

import de.comci.imgp.ui.ImagePuzzleModel.Team;
import static de.comci.imgp.ui.ImageUtil.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/**
 *
 * @author Sebastian
 */
public class ImagePuzzlePanel extends javax.swing.JPanel implements PropertyChangeListener, StateChangeListener {

    private ImagePuzzleModel model;
    private boolean showImage = false;
    private final int screenRes;
    private Color countDownColor;
    final private Color transparentColor = new Color(0, 0, 0, 120);
    private final Color countdownColor = new Color(235, 106, 1, 100);
    private double targetAspectRatio = -1; // width / height

    public double getAspectTargetRatio() {
        return targetAspectRatio;
    }

    public double getAspectRatio() {
        return 1.0 * getWidth() / getHeight();
    }

    public void setTargetAspectRatio(double aspectRatio) {
        this.targetAspectRatio = aspectRatio;
    }

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
    private int tileWidth, tileHeight;

    public ImagePuzzlePanel() {
        initComponents();
        model = new ImagePuzzleModel();
        setModel(model);
        countDownColor = new Color(23, 156, 125, 180);
        screenRes = Toolkit.getDefaultToolkit().getScreenResolution();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    System.out.println(String.format("left mouse at %d:%d, that's tile #%d", e.getX(), e.getY(), getTileNumber(e.getPoint())));
                    model.toggleDelayedTile(getTileNumber(e.getPoint()));
                    repaint();
                }

            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateTileSize();
            }
        });
        updateTileSize();
    }
    
    private Rectangle getAvailableBounds() {
        Insets i = getInsets();

        // use aspect ratio
        double currentAspectRatio = getAspectRatio();
        if (targetAspectRatio != -1) {
            System.out.println("changed target ratio");
            // adjust aspect ratio
            if (targetAspectRatio > currentAspectRatio) {
                // increase
                i.top = getHeight() - (int)(getWidth() / targetAspectRatio) / 2;
                i.bottom = getHeight() - (int)(getWidth() / targetAspectRatio) / 2;
            } else {
                // decrease
                i.left = getWidth() - (int)(getHeight() * targetAspectRatio) / 2;
                i.right = getWidth() - (int)(getHeight() * targetAspectRatio) / 2;                
            }
        }               
        
        return new Rectangle(i.left, i.top, getWidth() - i.left - i.right, getHeight() - i.left - i.right);
    }
    
    

    private void updateTileSize() {
        Rectangle b = getAvailableBounds();
        tileWidth = b.width / model.getNumberOfTilesHorizontal();
        tileHeight = b.height / model.getNumberOfTilesVertical();
        while (tileWidth * model.getNumberOfTilesHorizontal() < b.width) {
            tileWidth++;
        }
        while (tileHeight * model.getNumberOfTilesVertical() < b.height) {
            tileHeight++;
        }
    }

    private int getTileNumber(Point location) {
        int column = (int) Math.floor(location.x / tileWidth);
        int row = (int) Math.floor(location.y / tileHeight);
        return row * getModel().getNumberOfTilesHorizontal() + column;
    }

    private Rectangle getTileBounds(int tileNumber) {
        Rectangle rectangle = new Rectangle();
        int row = (int) Math.floor(tileNumber / model.getNumberOfTilesHorizontal());
        int column = tileNumber % model.getNumberOfTilesHorizontal();
        rectangle.x = column * tileWidth;
        rectangle.y = row * tileHeight;
        rectangle.width = tileWidth;
        rectangle.height = tileHeight;
        return rectangle;
    }
    
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (scaledImage != null) {
            g.drawImage(scaledImage, 0, 0, this);
        }
        
        final Rectangle b = getAvailableBounds();
        
        List<Integer> seq = model.getRevealSequence();
        if (seq != null) {

            // draw hiding tiles
            g.setColor(getColor());
            for (int tile : seq) {
                Rectangle r = getTileBounds(tile);
                g.fillRect(r.x, r.y, r.width, r.height);
                if (model.isDelayedTile(tile) && isTransparent()) {
                    Graphics2D g2 = (Graphics2D) g.create(r.x, r.y, r.width, r.height);
                    g2.rotate(-.5);
                    g2.setColor(Color.RED);
                    int dist = 9;
                    for (int i = -8; i < Math.floor(1.0 * r.width / dist); i++) {
                        g2.drawLine(i * dist, 0, i * dist, r.height * 2);
                    }
                }
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            final int w = b.width,
                      h = b.height;

            int strW, x, y;

            int fontSize = (int) Math.round(w / 64 * screenRes / 72.0);
            Font font = new Font("Arial", Font.BOLD, fontSize);
            FontMetrics metrics = g2.getFontMetrics(font);

            // paint buzz block countdown
            int size = (int) Math.max(h / 6.0, 80);
            paintCountdown((Graphics2D) g2.create(10, 10, size, size), 0);
            paintCountdown((Graphics2D) g2.create(w - 10 - size, 10, size, size), 1);


            // paint team names during buzz
            if (team != null) {

                final int paddingX = 80, paddingY = 20;

                fontSize = (int) Math.round(w / 12 * screenRes / 72.0);
                font = new Font("Arial", Font.BOLD, fontSize);
                metrics = g2.getFontMetrics(font);

                strW = metrics.stringWidth(team.name);
                g2.setFont(font);
                x = (w - strW - paddingX) / 2;
                y = (h - metrics.getHeight()) - 2 * paddingY;

                // draw rectangular background
                g2.setColor(transparentColor);
                g2.fillRect(x, y, strW + paddingX, metrics.getHeight() + paddingY);
                g2.setColor(team.color);
                g2.fillRect(x, y, (int) ((strW + paddingX) * (1 - buzzCountdown)), metrics.getHeight() + paddingY);
                g2.setColor(Color.white);
                g2.drawString(team.name, x + paddingX / 2, y + metrics.getAscent() + paddingY / 2);

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
    
    Dimension getAvailableSize() {
        Rectangle i = getAvailableBounds();
        return new Dimension(i.width, i.height);
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

                double scaleFactor = Math.min(1d, getScaleFactorToFill(new Dimension(image.getWidth(ImagePuzzlePanel.this), image.getHeight(ImagePuzzlePanel.this)), getAvailableSize()));

                int scaleWidth = (int) Math.round(image.getWidth(ImagePuzzlePanel.this) * scaleFactor);
                int scaleHeight = (int) Math.round(image.getHeight(ImagePuzzlePanel.this) * scaleFactor);

                return image.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_FAST);
            }

            @Override
            protected void done() {
                try {
                    scaledImage = get();
                    repaint();
                } catch (InterruptedException | ExecutionException ignore) {
                }
            }
        }.execute();

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        if (evt.getPropertyName().equals(ImagePuzzleModel.PROPERTY_TILE_NUMBER)) {
            updateTileSize();
        }

    }
    private Team team = null;

    @Override
    public void stateChanged(StateChangeEvent evt) {

        System.out.println("received " + evt.getNewState() + " state");

        switch (evt.getNewState()) {
            case READY:
                stopBuzzCountdown();
                team = null;
                scaleImage(model.getRawImage());
                break;
            case RUNNING:
            case HALTED:
            case REVEALED:
                stopBuzzCountdown();
                team = null;
                repaint();
                break;
            case BUZZED:
                team = model.getTeamLastBuzzed();
                startBuzzCountdown();
                repaint();
                break;
        }

    }

    public void setImage(File image) {
        model.setImage(image);
    }
    Timer buzzCountdownTimer;
    double buzzCountdown = 1.0;
    int buzzCountdownDelay = 50;

    private void startBuzzCountdown() {
        buzzCountdown = 1.0;
        buzzCountdownTimer = new Timer(buzzCountdownDelay, new ActionListener() {
            int fullCount = (int) Math.floor(model.getAnswerDuration() / buzzCountdownDelay) - 1;
            double currentCount = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                currentCount += buzzCountdownDelay;
                buzzCountdown = currentCount / fullCount / buzzCountdownDelay;
                repaint();
            }
        });
        buzzCountdownTimer.setInitialDelay(0);
        buzzCountdownTimer.start();
    }

    private void stopBuzzCountdown() {
        if (buzzCountdownTimer != null) {
            buzzCountdownTimer.stop();
        }
    }

    private Color getCountdownColor() {
        return countDownColor;
    }

    public void setCountDownColor(Color countDownColor) {
        this.countDownColor = countDownColor;
    }

    private void paintCountdown(Graphics2D g, int teamId) {

        final float remaining = model.getNumberOfBlockedTilesForTeam(teamId);

        if (remaining == 0) {
            return;
        }

        final float total = model.getDurationBlocked();
        final float percentage = remaining / total;
        final Rectangle b = g.getClipBounds();
        final int strokeWidth = b.width / 8;
        final Stroke s = new BasicStroke(strokeWidth);
        g.setColor(transparentColor);
        g.fillArc(1, 1, b.width - 2, b.height - 2, 0, 360);
        g.setColor(countdownColor);
        g.setStroke(s);
        g.drawArc(strokeWidth / 2, strokeWidth / 2, b.width - 1 * strokeWidth, b.height - 1 * strokeWidth, 90, (int) (360 * percentage));

        final int fontSize = (int) b.width / 4;
        Font font = new Font("Arial", Font.BOLD, fontSize);
        FontMetrics metrics = g.getFontMetrics(font);
        g.setFont(font);
        g.setColor(Color.white);
        g.drawString(String.format("%.0f", remaining), (b.width - metrics.stringWidth(String.format("%.0f", remaining))) / 2, (b.height - metrics.getAscent()) / 2 + metrics.getAscent());

    }
}