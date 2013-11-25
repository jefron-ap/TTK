/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.ttk.pl.fx.helper;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author kec
 */
public class IconHelper {
            
    public static ImageView getImageView(Image icon) {
        ImageView iv = new ImageView();
        iv.setImage(icon);
        return iv;
    }
        
    public static Image setupImage(String image) {
        Image icon = new Image(image, false);
        return icon;
    }
    public static ImageView setupImageView(String image) {
        Image icon = new Image(image, false);
        ImageView iv = new ImageView();
        iv.setImage(icon);
        return iv;
    }

}
