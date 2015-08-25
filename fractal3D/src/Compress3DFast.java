import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author nabinchha
 */

public class Compress3DFast
{
    public static void main(String[] args) throws IOException
    {
        File brainFile = new File("/Users/nabinchha/NetBeansProjects/fractal3D/resources/brainNew.rawb");
        File fractalFile = new File("/users/nabinchha/netbeansprojects/fractal3D/resources/branasd.txt");

        MyImage3D image3d = new MyImage3D(brainFile);
        MyImage3D domainBlock = new MyImage3D(Global.DB_SIDE);
        MyImage3D rangeBlock = new MyImage3D(Global.DB_SIDE);
        MyImage3D flippedRangeBlock = new MyImage3D(Global.DB_SIDE);
        MyImage3D reducedImage = new MyImage3D(image3d.side/2);

        double currDistance, minDistance;
        double infinity = Double.POSITIVE_INFINITY;
        int currSymmetry, currRangeX, currRangeY, currRangeZ, domainX, domainY, domainZ, currShift;
        double domainMean, rangeMean;
        AffineMap3D bestMap = new AffineMap3D();

        // open file for writing
        Writer out = null;
        try {
            out = new BufferedWriter(new FileWriter(fractalFile));
        } catch (IOException ex) {
            System.out.println("Error opening fractal file for writing!\n");
        }

        Util3D.ReduceImage(image3d, reducedImage);
        // write header file from image to fractal file
        Util3D.WriteHeader(image3d, fractalFile, out);

        // main loop
        int block = image3d.side/4;
        int blockReduced = block/2;
        MyImage3D dummy = new MyImage3D(block);
        MyImage3D dummyReduced = new MyImage3D(block/2);
       // int blockZ = 0, blockX = 0, blockY = 0;

        for(int blockZ = 0; blockZ < image3d.side; blockZ+=block)
        {
            for(int blockY = 0; blockY < image3d.side; blockY+=block)
            {
                for(int blockX = 0; blockX < image3d.side; blockX+=block)
                {
                    System.out.printf("Dx %d Dy %d Dz %d\n", blockX, blockY, blockZ);
                    Util3D.CopyImage(image3d, blockX, blockY, blockZ, dummy, 0, 0, 0, block, block, block);
                    Util3D.ReduceImage(dummy, dummyReduced);
                    //main loop
                    for(domainZ = 0; domainZ < dummy.side; domainZ += Global.DB_SIDE)
                    {
                        for(domainY = 0; domainY < dummy.side; domainY += Global.DB_SIDE)
                        {
                            for(domainX = 0; domainX < dummy.side; domainX += Global.DB_SIDE )
                            {
                                //System.out.printf("Dx %d Dy %d Dz %d\n", domainX, domainY, domainZ);

                                minDistance = infinity;

                                Util3D.CopyImage(dummy, domainX, domainY, domainZ,
                                                 domainBlock, 0, 0, 0,
                                                 Global.DB_SIDE, Global.DB_SIDE, Global.DB_SIDE);
                                domainMean = Util3D.Mean(domainBlock);

                                for(currRangeZ = 0; currRangeZ <= (dummyReduced.side - Global.DB_SIDE); currRangeZ++)
                                {
                                    for(currRangeY = 0; currRangeY <= (dummyReduced.side - Global.DB_SIDE); currRangeY++)
                                    {
                                        for(currRangeX = 0; currRangeX <= (dummyReduced.side - Global.DB_SIDE); currRangeX++)
                                        {
                                            // step3: get range block

                                            Util3D.CopyImage(dummyReduced, currRangeX, currRangeY, currRangeZ,
                                                             rangeBlock, 0, 0, 0,
                                                             Global.DB_SIDE, Global.DB_SIDE, Global.DB_SIDE);


                                            rangeMean = Util3D.Mean(rangeBlock);
                                            currShift = (int)(domainMean - rangeMean);
                                            Util3D.IntensityShift(rangeBlock, currShift);

                                            // step4 loop over symmetries
                                            for(currSymmetry = 0; currSymmetry < Global.NSYMS; currSymmetry++)
                                            {
                                                Util3D.Flip(rangeBlock, flippedRangeBlock, currSymmetry);
                                                currDistance = Util3D.L2Distance(domainBlock, flippedRangeBlock);
                                                if(currDistance < minDistance)
                                                {
                                                    minDistance = currDistance;
                                                    bestMap.shift = currShift;
                                                    bestMap.symmetry = currSymmetry;
                                                    bestMap.rangeX = currRangeX;
                                                    bestMap.rangeY = currRangeY;
                                                    bestMap.rangeZ = currRangeZ;
                                                }
                                            }
                                        }
                                    }
                                }
                                // write transformation to fractal code
                                Util3D.WriteBestMap(bestMap, fractalFile, out);
                            }
                        }
                    }

                }
            }
        }
        out.close();
    }
}
