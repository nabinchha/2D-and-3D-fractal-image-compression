import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nabinchha
 */
public class Util
{
    // returns the mean intensity of all the pixels in this image
    static double Mean(MyImage image)
    {
        double sum = 0;
        for(int j = 0; j < image.side; ++j)
        {
            for(int i = 0; i < image.side; ++i)
            {
                sum += image.GetPixelGrey(i,j);
            }
        }
        return (sum/image.numPix);
    }

    // shift intensity of each pixel by shift
    static void IntensityShift(MyImage image, int shift)
    {
        for(int j = 0; j < image.side; ++j)
        {
            for(int i = 0; i < image.side; ++i)
            {
                int a, r, g, b;
                int argb = image.img.getRGB(i, j);

                a = ((argb >> 24) & 0xff);
                r = ((argb >> 16) & 0xff);
                g = ((argb >> 8 ) & 0xff);
                b = ((argb      ) & 0xff);

                a += shift;
                r += shift;
                g += shift;
                b += shift;

                argb = (a << 24) | (r << 16) | (g << 8) | b;
                //image.SetPixelGrey(i, j, r);
                image.SetPixel(i, j, argb);
            }
        }
    }


    // return the distance between two images
    // image1 and image2 MUST have the same size
    static double L2Distance(MyImage image1, MyImage image2)
    {
        double d, distance = 0;
        if(image1.side == image2.side)
        {
            for(int j = 0; j < image1.side; ++j)
            {
                for(int i = 0; i < image1.side; ++i)
                {
                    d = image1.GetPixelGrey(i, j) - image2.GetPixelGrey(i, j);
                    distance += d * d;
                }
            }
        }
        return distance;
    }

    static void CopyImage(MyImage srcImage, int srcX, int srcY,
                          MyImage destImage, int destX, int destY,
                          int width, int height)
    {
        int sourcePixel;
        for(int j = 0; j < height; ++j)
        {
            for(int i = 0; i < width; ++i)
            {
                sourcePixel = srcImage.GetPixelGrey(i + srcX, j + srcY);
                destImage.SetPixelGrey(i + destX, j + destY, sourcePixel);
            }
        }
    }

    static void ReduceImage(MyImage srcImage, MyImage destImage)
    {
        int r1, r2, r3, r4, r, tr;

        for(int j = 0; j < destImage.side; ++j)
        {
            for(int i = 0; i < destImage.side; ++i)
            {
                // spatial rescale by 2
                r1 = srcImage.GetPixelGrey(2*i,     2*j);
                r2 = srcImage.GetPixelGrey(2*i+1,   2*j);
                r3 = srcImage.GetPixelGrey(2*i,     2*j+1);
                r4 = srcImage.GetPixelGrey(2*i+1, 2*j+1);
                r = (r1 + r2 + r3 + r4)/4;
                destImage.SetPixelGrey(i, j, r);

                // intensity rescale by 3/4
                tr = destImage.GetPixelGrey(i, j);
                tr = tr * 3/4;
                destImage.SetPixelGrey(i, j, tr);
            }
        }
    }

    static void Flip(MyImage rangeBlock, MyImage transformedRangeBlock, char symmetry)
    {
        short x, y, t;
        int p;
        for(int j = 0; j < rangeBlock.side; ++j)
        {
            for(int i = 0; i < rangeBlock.side; ++i)
            {
                if((symmetry & Global.FLIP_X) != 0)
                    x = (short)(rangeBlock.side - 1 - i);
                else
                    x = (short)i;

                if((symmetry & Global.FLIP_Y) != 0)
                    y = (short)(rangeBlock.side - 1 - j);
                else
                    y = (short)j;

                // diagonal is not allowed unless width = height
                if((symmetry & Global.FLIP_DIAG) != 0)
                {
                    t = y;
                    y = x;
                    x = t;
                }

                p = rangeBlock.GetPixelGrey(i, j);
                transformedRangeBlock.SetPixelGrey(x, y, p);
            }
        }
    }

    static void WriteHeader(MyImage image, File fractalFile, Writer out, int block)
    {
        try {
            int bside = Global.DB_SIDE;
            out.write(Integer.toString(image.side));
            out.write(" ");
            out.write(Integer.toString(block));
            out.write(" ");
            out.write(Integer.toString((block * block) / (bside * bside) ));
            out.write(" ");
            out.write(Integer.toString(bside));
            out.write("\n");
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    static void WriteBestMap(AffineMap bestMap, File fractalFile, Writer out)
    {

        try {
            out.write(Integer.toString(bestMap.rangeX));
            out.write(" ");
            out.write(Integer.toString(bestMap.rangeY));
            out.write(" ");
            out.write(Integer.toString(bestMap.symmetry));
            out.write(" ");
            out.write(Integer.toString(bestMap.shift));
            out.write("\n");
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static String[] ParseString(String line)
    {
        String[] tokens = line.split(" ");
        return tokens;
    }
}


