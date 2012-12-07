/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.comci.imgp;

import de.comci.imgp.ui.ControlFrame;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Sebastian
 */
public class ImagePuzzle {

    private List<File> files;

    public ImagePuzzle() {

        File[] possiblePictureDir = {
            new File(System.getProperty("user.home") + "/Pictures"),
            new File(System.getProperty("user.home") + "/My Documents/My Pictures")
        };
        
        File pictureDir = null;
        for (File f : possiblePictureDir) {
            if (f.exists()) {
                System.out.println("found dir " + f.getAbsolutePath());
                pictureDir = f;
                this.scanDirectory(f.toPath());
                break;
            }
        }
        
        final File defaultPictureDir = pictureDir;
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {                
                ControlFrame controlFrame = new ControlFrame(ImagePuzzle.this, defaultPictureDir);
                controlFrame.setLocationRelativeTo(null);
                controlFrame.setVisible(true);
            }
        });

    }

    public final void scanDirectory(Path dir) {

        File[] listFiles = dir.toFile().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("jpg");
            }
        });
        files = Arrays.asList(listFiles);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        ImagePuzzle dalliClick = new ImagePuzzle();

    }

    public Iterable<File> getFiles() {
        return files;
    }
   
}
