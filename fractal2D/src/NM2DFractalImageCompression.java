import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

/** 
 * @author Nabin Mulepati
 */

public class NM2DFractalImageCompression
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        File imagefile = new File("resources/Test/2.png");
        File fractalFile = new File("resources/Test/2.txt");

        NM2DImage image = new NM2DImage(imagefile);

        NM2DImage domainBlock = new NM2DImage(NMGlobals.DB_SIDE);
        NM2DImage rangeBlock = new NM2DImage(NMGlobals.DB_SIDE);
        NM2DImage flippedRangeBlock = new NM2DImage(NMGlobals.DB_SIDE);
        NM2DImage reducedImage = new NM2DImage(image.side/2);

        double currDistance, minDistance;
        double infinity = Double.POSITIVE_INFINITY;
        char currSymmetry;
        int currRangeX, currRangeY, domainX, domainY;
        short currShift;
        int domainMean;
        NM2DAffineMap bestMap = new NM2DAffineMap();

        // open file for writing
        Writer out = null;
        try {
            out = new BufferedWriter(new FileWriter(fractalFile));
        } catch (IOException ex) {
            System.out.println("Error opening fractal file for writing!\n");
        }
        // write header file from image to fractal file
        NM2DUtility.writeHeader(image, fractalFile, out, 0);

        // rescale(contract) image in spatial and intensity directions
        NM2DUtility.reduceImage(image, reducedImage);

        boolean canWrite = false;

        // main loop
        for(domainY = 0; domainY < image.side; domainY += NMGlobals.DB_SIDE) {
            for(domainX = 0; domainX < image.side; domainX += NMGlobals.DB_SIDE) {
                // step 2: get domain block
                System.out.printf("Dx %d Dy %d\n", domainX, domainY);

                minDistance = infinity;

                NM2DUtility.copyImage(image, domainX, domainY, domainBlock, 0, 0, NMGlobals.DB_SIDE, NMGlobals.DB_SIDE);
                domainMean = (int)NM2DUtility.getMeanImageIntensity(domainBlock);

                for(currRangeY = 0; currRangeY <= (reducedImage.side - NMGlobals.DB_SIDE); currRangeY++) {
                    for(currRangeX = 0; currRangeX <= (reducedImage.side - NMGlobals.DB_SIDE); currRangeX++) {
                        // step3: get range block
                        NM2DUtility.copyImage(reducedImage, currRangeX, currRangeY, rangeBlock,
                                0, 0, NMGlobals.DB_SIDE, NMGlobals.DB_SIDE);

                        // best mean square fit is given y shifting
                        // means to be equivalent

                        currShift = (short)(domainMean - NM2DUtility.getMeanImageIntensity(rangeBlock));
                        NM2DUtility.shiftImageIntensity(rangeBlock, currShift);

                        // step4 loop over symmetries
                        for(currSymmetry = 0; currSymmetry < NMGlobals.NSYMS; currSymmetry++) {
                            NM2DUtility.flipImage(rangeBlock, flippedRangeBlock, currSymmetry);
                            currDistance = NM2DUtility.getL2Distance(domainBlock, flippedRangeBlock);
                            if(currDistance < minDistance) {
                                minDistance = currDistance;
                                bestMap.shift = currShift;
                                bestMap.symmetry = currSymmetry;
                                bestMap.rangeX = currRangeX;
                                bestMap.rangeY = currRangeY;
                            }
                        }
                    }
                }
                // write transformation to fractal code
                NM2DUtility.writeBestMap(bestMap, fractalFile, out);
            }
        }
/*
        MainCanvas myCanvas = new MainCanvas(image.img);
        Frame f = new Frame( "Fractal Image Compression" );
        f.add("Center", myCanvas);
        f.setSize(new Dimension(image.side,image.side+22));
        f.setVisible(true);
        out.close();
 * 
 */
    }
}
