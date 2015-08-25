import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author nabinchha
 */

class NM2DImage {
    BufferedImage img;
    int height;
    int width;
    int length;


    NM2DImage(File f) {
        try {
            img = ImageIO.read(f);
        } catch (IOException ex) {
            Logger.getLogger(NM2DImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        height = img.getHeight();
        width = img.getWidth();
        length = height * width;
    }
    
    NM2DImage(int w, int h) {
        height = h;
        width = w;
        length = h * w;
        img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
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
        int a = grey, r = grey, g = grey, b = grey;
        int rgb = (a << 24) | (r << 16) | (g << 8) | b;
        img.setRGB(x, y, rgb);
    }


    public int getPixel(int x, int y)
    {
	return img.getRGB(x, y);
    }


    public void setPixel(int x, int y, int intensity) {
        img.setRGB(x, y, intensity);
    }

    void getSliceFrom3D3(NM3DImage image3d, int slice) {
        
        for(int i = 0; i < image3d.side; ++i) {
            for(int j = 0; j < image3d.side; ++j) {
                int p = image3d.getGreyVoxelIntensity(i, j, slice);
                setGreyPixelIntensity(i, j, p);
            }
        }

    }
    
    void getSliceFrom3D2(NM3DImage image3d, int slice) {
        
        for(int i = 0; i < image3d.side; ++i) {
            for(int j = 0; j < image3d.side; ++j) {
                int p = image3d.getGreyVoxelIntensity(i, slice, j);
                setGreyPixelIntensity(i, j, p);
            }
        }

    }
    
    void getSliceFrom3D1(NM3DImage image3d, int slice) {
        
        for(int i = 0; i < image3d.side; ++i) {
            for(int j = 0; j < image3d.side; ++j) {
                int p = image3d.getGreyVoxelIntensity(slice, i, j);
                if(p == 255)
                    p = 0;
                setGreyPixelIntensity(i, j, p);
            }
        }
    }
}