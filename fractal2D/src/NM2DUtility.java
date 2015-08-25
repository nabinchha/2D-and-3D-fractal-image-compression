import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nabin Mulepati
 */

public class NM2DUtility {
    // returns the mean intensity of all the pixels in this image
    static double getMeanImageIntensity(NM2DImage image) {
        double sum = 0;
        for(int j = 0; j < image.side; ++j) {
            for(int i = 0; i < image.side; ++i) {
                sum += image.getGreyPixelIntensity(i,j);
            }
        }
        return (sum/image.numPix);
    }

    // shift intensity of each pixel by shift
    static void shiftImageIntensity(NM2DImage image, int shift) {
        for(int j = 0; j < image.side; ++j) {
            for(int i = 0; i < image.side; ++i) {
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
                //image.setGreyPixelIntensity(i, j, r);
                image.setPixelIntensity(i, j, argb);
            }
        }
    }

    // return the distance between two images
    // image1 and image2 MUST have the same size
    static double getL2Distance(NM2DImage image1, NM2DImage image2) {
        double d, distance = 0;
        if(image1.side == image2.side) {
            for(int j = 0; j < image1.side; ++j) {
                for(int i = 0; i < image1.side; ++i) {
                    d = image1.getGreyPixelIntensity(i, j) - image2.getGreyPixelIntensity(i, j);
                    distance += d * d;
                }
            }
        }
        return distance;
    }

    static void copyImage(NM2DImage srcImage, int srcX, int srcY,
                          NM2DImage destImage, int destX, int destY,
                          int width, int height) {
        int sourcePixel;
        for(int j = 0; j < height; ++j) {
            for(int i = 0; i < width; ++i) {
                sourcePixel = srcImage.getGreyPixelIntensity(i + srcX, j + srcY);
                destImage.setGreyPixelIntensity(i + destX, j + destY, sourcePixel);
            }
        }
    }

    static void reduceImage(NM2DImage srcImage, NM2DImage destImage) {
        int r1, r2, r3, r4, r, tr;

        for(int j = 0; j < destImage.side; ++j) {
            for(int i = 0; i < destImage.side; ++i) {
                // spatial rescale by 2
                r1 = srcImage.getGreyPixelIntensity(2*i,     2*j);
                r2 = srcImage.getGreyPixelIntensity(2*i+1,   2*j);
                r3 = srcImage.getGreyPixelIntensity(2*i,     2*j+1);
                r4 = srcImage.getGreyPixelIntensity(2*i+1, 2*j+1);
                r = (r1 + r2 + r3 + r4)/4;
                destImage.setGreyPixelIntensity(i, j, r);

                // intensity rescale by 3/4
                tr = destImage.getGreyPixelIntensity(i, j);
                tr = tr * 3/4;
                destImage.setGreyPixelIntensity(i, j, tr);
            }
        }
    }

    static void flipImage(NM2DImage rangeBlock, NM2DImage transformedRangeBlock, char symmetry) {
        short x, y, t;
        int p;
        for(int j = 0; j < rangeBlock.side; ++j) {
            for(int i = 0; i < rangeBlock.side; ++i) {
                if((symmetry & NMGlobals.FLIP_X) != 0) {
                    x = (short)(rangeBlock.side - 1 - i);
                } else {
                    x = (short)i;
                }
                
                if((symmetry & NMGlobals.FLIP_Y) != 0) {
                    y = (short)(rangeBlock.side - 1 - j);
                } else {
                    y = (short)j;
                }
                
                // diagonal is not allowed unless width = height
                if((symmetry & NMGlobals.FLIP_DIAG) != 0) {
                    t = y;
                    y = x;
                    x = t;
                }

                p = rangeBlock.getGreyPixelIntensity(i, j);
                transformedRangeBlock.setGreyPixelIntensity(x, y, p);
            }
        }
    }

    static void writeHeader(NM2DImage image, File fractalFile, Writer out, int block) {
        try {
            int bside = NMGlobals.DB_SIDE;
            out.write(Integer.toString(image.side));
            out.write(" ");
            out.write(Integer.toString(block));
            out.write(" ");
            out.write(Integer.toString((block * block) / (bside * bside) ));
            out.write(" ");
            out.write(Integer.toString(bside));
            out.write("\n");
        } catch (IOException ex) {
            Logger.getLogger(NM2DUtility.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    static void writeBestMap(NM2DAffineMap bestMap, File fractalFile, Writer out) {

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
            Logger.getLogger(NM2DUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static String[] parseString(String line) {
        String[] tokens = line.split(" ");
        return tokens;
    }
}