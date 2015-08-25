
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

class MyImage
{
    BufferedImage img;
    int height;
    int width;
    int length;


    MyImage(File f)
    {
        try
        {
            img = ImageIO.read(f);
        }
        catch (IOException ex)
        {
            Logger.getLogger(MyImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        height = img.getHeight();
        width = img.getWidth();
        length = height * width;
    }
    MyImage(int w, int h)
    {
        height = h;
        width = w;
        length = h * w;
        img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    }

    // we're assuming that the input image is greyscale
    public int GetPixelGrey(int x, int y)
    {
	int a, r, g, b;
 	int argb = img.getRGB(x, y);

        a = ((argb >> 24) & 0xff);
        r = ((argb >> 16) & 0xff);
        g = ((argb >> 8 ) & 0xff);
        b = ((argb      ) & 0xff);

        return r;
    }

    public void SetPixelGrey(int x, int y, int grey)
    {
        int a = grey, r = grey, g = grey, b = grey;
        int rgb = (a << 24) | (r << 16) | (g << 8) | b;
        img.setRGB(x, y, rgb);
    }


    public int GetPixel(int x, int y)
    {
	return img.getRGB(x, y);
    }


    public void SetPixel(int x, int y, int intensity)
    {
        img.setRGB(x, y, intensity);
    }

    void GetSliceFrom3D3(MyImage3D image3d, int slice)
    {
        
        for(int i = 0; i < image3d.side; ++i)
        {
            for(int j = 0; j < image3d.side; ++j)
            {
                int p = image3d.GetPixelGrey(i, j, slice);
                SetPixelGrey(i, j, p);
            }
        }

    }
    void GetSliceFrom3D2(MyImage3D image3d, int slice)
    {
        
        for(int i = 0; i < image3d.side; ++i)
        {
            for(int j = 0; j < image3d.side; ++j)
            {
                int p = image3d.GetPixelGrey(i, slice, j);
                SetPixelGrey(i, j, p);
            }
        }

    }
    void GetSliceFrom3D1(MyImage3D image3d, int slice)
    {
        
        for(int i = 0; i < image3d.side; ++i)
        {
            for(int j = 0; j < image3d.side; ++j)
            {
                int p = image3d.GetPixelGrey(slice, i, j);
                if(p == 255)
                    p = 0;
                SetPixelGrey(i, j, p);
            }
        }
    }
}

