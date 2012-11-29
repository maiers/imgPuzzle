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
                
        for (File f : possiblePictureDir) {
            if (f.exists()) {
                System.out.println("found dir " + f.getAbsolutePath());
                scanDirectory(f.toPath());
                break;
            }
        }
        
        //scanDirectory(new File("D:/Image/2008/USA/Auswahl").toPath());
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ControlFrame(ImagePuzzle.this).setVisible(true);
            }
        });

    }

    public void scanDirectory(Path dir) {

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
