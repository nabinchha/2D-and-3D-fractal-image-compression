import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nabin Mulepati
 */

public class NM3DUtility {
    
    static String[] parseString(String line) {
        String[] tokens = line.split(" ");
        return tokens;
    }

    // returns the mean intensity of all the voxels in this image
    static double getMeanImageIntensity(NM3DImage image) {
        double sum = 0;
        for(int k = 0; k < image.side; ++k) {
            for(int j = 0; j < image.side; ++j) {
                for(int i = 0; i < image.side; ++i) {
                    sum += image.getGreyVoxelIntensity(i,j, k);
                }
            }
        }
        return (sum/image.numVoxels);
    }

    // shift intensity of each voxel by shift
    static void shiftImageIntensity(NM3DImage image, int shift) {
        for(int k = 0; k < image.side; ++k) {
            for(int j = 0; j < image.side; ++j) {
                for(int i = 0; i < image.side; ++i) {
                    int newVal = shift + image.getGreyVoxelIntensity(i, j, k);
                    image.setGreyVoxelIntensity(i, j, k, newVal);
                }
            }
        }
    }

    // return the distance between two images
    // image1 and image2 MUST have the same size
    static double getL2Distance(NM3DImage image1, NM3DImage image2) {
        double d, distance = 0;
        for(int k = 0; k < image1.side; ++k) {
            for(int j = 0; j < image1.side; ++j) {
                for(int i = 0; i < image1.side; ++i) {
                    d = image1.getGreyVoxelIntensity(i, j, k) - image2.getGreyVoxelIntensity(i, j, k);
                    distance += d * d;
                }
            }
        }
        return distance;
    }

    static void copyImage(NM3DImage srcImage, int srcX, int srcY, int srcZ,
                          NM3DImage destImage, int destX, int destY, int destZ,
                          int length, int width, int height) {
        int sourcePixel;
        for(int k = 0; k < length; ++k) {
            for(int j = 0; j < width; ++j) {
                for(int i = 0; i < height; ++i) {
                    sourcePixel = srcImage.getGreyVoxelIntensity(i + srcX, j + srcY, k + srcZ);
                    destImage.setGreyVoxelIntensity(i + destX, j + destY, k + destZ, sourcePixel);
                }
            }
        }
    }

    static void reduceImage(NM3DImage srcImage, NM3DImage destImage) {
        int r, r1, r2, r3, r4, r5, r6, r7, r8, tr;

        for(int k = 0; k < destImage.side; ++k) {
            for(int j = 0; j < destImage.side; ++j) {
                for(int i = 0; i < destImage.side; ++i) {
                    // spatial rescale by 2
                    r1 = srcImage.getGreyVoxelIntensity(2*i,       2*j,      2*k);
                    r2 = srcImage.getGreyVoxelIntensity(2*i+1,     2*j,      2*k);
                    r3 = srcImage.getGreyVoxelIntensity(2*i,       2*j+1,    2*k);
                    r4 = srcImage.getGreyVoxelIntensity(2*i+1,     2*j+1,    2*k);
                    r5 = srcImage.getGreyVoxelIntensity(2*i,       2*j,      2*k+1);
                    r6 = srcImage.getGreyVoxelIntensity(2*i+1,     2*j,      2*k+1);
                    r7 = srcImage.getGreyVoxelIntensity(2*i,       2*j+1,    2*k+1);
                    r8 = srcImage.getGreyVoxelIntensity(2*i+1,     2*j+1,    2*k+1);

                    r = (r1 + r2 + r3 + r4 + r5 + r6 + r7 + r8)/8;
                    destImage.setGreyVoxelIntensity(i, j, k, r);

                    // intensity rescale by 3/4
                    tr = destImage.getGreyVoxelIntensity(i, j, k);
                    tr = tr * 3/4;
                    destImage.setGreyVoxelIntensity(i, j, k, tr);
                }
            }
        }
    }

    static void flipImage(NM3DImage rangeBlock, NM3DImage transformedRangeBlock, int symmetry) {
        short x, y, z, t;
        int p;
        for(int k = 0; k < rangeBlock.side; ++k) {
            for(int j = 0; j < rangeBlock.side; ++j) {
                for(int i = 0; i < rangeBlock.side; ++i) {
                    if((symmetry & NMGlobals.FLIP_XY) != 0) {
                        x = (short)(rangeBlock.side - 1 - k);
                    } else {
                        x = (short)k;
                    }
                    
                    if((symmetry & NMGlobals.FLIP_YZ) != 0) {
                        y = (short)(rangeBlock.side - 1 - i);
                    } else {
                        y = (short)i;
                    }
                    
                    if((symmetry & NMGlobals.FLIP_XZ) != 0) {
                        z = (short)(rangeBlock.side - 1 - j);
                    } else {
                        z = (short)j;
                    }
                    
                    // diagonal is not allowed unless width = height
                    /*
                    if((symmetry & NMGlobals.FLIP_DIAG) != 0)
                    {
                        t = x;
                        x = y;
                        y = z;
                        z = t;
                    }
                     */
                    
                    p = rangeBlock.getGreyVoxelIntensity(i, j, k);
                    transformedRangeBlock.setGreyVoxelIntensity(x, y, z, p);
                }
            }
        }
    }

    static void writeHeader(NM3DImage image, File fractalFile, Writer out) {
        try {
            out.write(Integer.toString(image.side));
            out.write("\n");
        } catch (IOException ex) {
            Logger.getLogger(NM3DUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void writeBestMap(NM3DAffineMap bestMap, File fractalFile, Writer out) {
        try {
            out.write(Integer.toString(bestMap.rangeX));
            out.write(" ");
            out.write(Integer.toString(bestMap.rangeY));
            out.write(" ");
            out.write(Integer.toString(bestMap.rangeZ));
            out.write(" ");
            out.write(Integer.toString(bestMap.symmetry));
            out.write(" ");
            out.write(Integer.toString(bestMap.shift));
            out.write("\n");
        } catch (IOException ex) {
            Logger.getLogger(NM3DUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}