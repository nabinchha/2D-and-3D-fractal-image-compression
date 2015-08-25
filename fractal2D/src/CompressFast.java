import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

/**
 * @author Nabin Mulepati
 */

public class CompressFast
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException
    {
        File imagefile = new File("/users/nabinchha/netbeansprojects/fractal2D/resources/Test/30.png");
        File fractalFile = new File("/users/nabinchha/netbeansprojects/fractal2D/resources/Test/30.txt");

        MyImage image = new MyImage(imagefile);
        MyImage domainBlock = new MyImage(Global.DB_SIDE);
        MyImage rangeBlock = new MyImage(Global.DB_SIDE);
        MyImage flippedRangeBlock = new MyImage(Global.DB_SIDE);
        MyImage reducedImage = new MyImage(image.side/2);

        double currDistance, minDistance;
        double infinity = Double.POSITIVE_INFINITY;
        char currSymmetry;
        int currRangeX, currRangeY, domainX, domainY;
        int currShift;
        double domainMean;
        AffineMap bestMap = new AffineMap();

        // open file for writing
        Writer out = null;
        try {
            out = new BufferedWriter(new FileWriter(fractalFile));
        } catch (IOException ex) {
            System.out.println("Error opening fractal file for writing!\n");
        }

        // rescale(contract) image in spatial and intensity directions
        Util.ReduceImage(image, reducedImage);

        int block = image.side/4;
        int blockReduced = block/2;
        MyImage dummy = new MyImage(block);
        MyImage dummyReduced = new MyImage(blockReduced);

        // write header file from image to fractal file
        Util.WriteHeader(image, fractalFile, out, block);
       
        for(int blockY = 0; blockY < image.side; blockY+=block)
        {
            for(int blockX = 0; blockX < image.side; blockX+=block)
            {
                System.out.printf("Dx %d Dy %d\n", blockX, blockY);
                Util.CopyImage(image, blockX, blockY, dummy, 0, 0, block, block);
                Util.ReduceImage(dummy, dummyReduced);

                // main loop
                for(domainY = 0; domainY < dummy.side; domainY += Global.DB_SIDE)
                {
                    for(domainX = 0; domainX < dummy.side; domainX += Global.DB_SIDE)
                    {
                        // step 2: get domain block
                        //System.out.printf("Dx %d Dy %d\n", domainX, domainY);
                        minDistance = infinity;

                        Util.CopyImage(dummy, domainX, domainY, domainBlock, 0, 0, Global.DB_SIDE, Global.DB_SIDE);
                        domainMean = Util.Mean(domainBlock);

                        for(currRangeY = 0; currRangeY <= (dummyReduced.side - Global.DB_SIDE); currRangeY++)
                        {
                            for(currRangeX = 0; currRangeX <= (dummyReduced.side - Global.DB_SIDE); currRangeX++)
                            {
                                // step3: get range block
                                Util.CopyImage(dummyReduced, currRangeX, currRangeY, rangeBlock,
                                        0, 0, Global.DB_SIDE, Global.DB_SIDE);

                                // best mean square fit is given y shifting
                                // means to be equivalent
                                currShift = (short)(domainMean - Util.Mean(rangeBlock));
                                Util.IntensityShift(rangeBlock, currShift);

                                // step4 loop over symmetries
                                for(currSymmetry = 0; currSymmetry < Global.NSYMS; currSymmetry++)
                                {
                                    Util.Flip(rangeBlock, flippedRangeBlock, currSymmetry);
                                    currDistance = Util.L2Distance(domainBlock, flippedRangeBlock);
                                    if(currDistance < minDistance)
                                    {
                                        minDistance = currDistance;
                                        bestMap.shift = (short)(currShift);
                                        bestMap.symmetry = currSymmetry;
                                        bestMap.rangeX = currRangeX;
                                        bestMap.rangeY = currRangeY;
                                    }
                                }
                            }
                        }
                        // write transformation to fractal code
                        Util.WriteBestMap(bestMap, fractalFile, out);
                    }
                }
            }
        }

        MainCanvas myCanvas = new MainCanvas(image.img);
        Frame f = new Frame( "Fractal Image Compression" );
        f.add("Center", myCanvas);
        f.setSize(new Dimension(image.side,image.side+22));
        f.setVisible(true);
        out.close();
    }
}
