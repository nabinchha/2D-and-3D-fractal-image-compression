import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Nabin Mulepati
 */

class NM2DImage {
    BufferedImage img;
    int side;
    int numPix;

    NM2DImage(File f) {
        try {
            img = ImageIO.read(f);
        } catch (IOException ex) {
            Logger.getLogger(NM2DImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        side = img.getHeight();
        numPix = side * side;
    }
    
    NM2DImage(int s) {
        side = s;
        numPix = s * s;
        img = new BufferedImage(s, s, BufferedImage.TYPE_INT_RGB);
    }

    // we're assuming that the input image is greyscale
    public int getGreyPixelIntensity(int x, int y) {
	int a, r, g, b;
 	int argb = img.getRGB(x, y);

        a = ((argb >> 24) & 0xff);
        r = ((argb >> 16) & 0xff);
        g = ((argb >> 8 ) & 0xff);
        b = ((argb      ) & 0xff);

        return r;
    }

    public void setGreyPixelIntensity(int x, int y, int grey) {
        int a = 0, r = grey, g = grey, b = grey;
        int rgb = (a << 24) | (r << 16) | (g << 8) | b;
        img.setRGB(x, y, rgb);
    }

    public void setPixelIntensity(int x, int y, int intensity) {
        img.setRGB(x, y, intensity);
    }
}
