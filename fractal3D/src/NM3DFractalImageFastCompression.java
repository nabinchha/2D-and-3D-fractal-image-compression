import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Nabin Mulepati
 */

public class NM3DFractalImageFastCompression {
    public static void main(String[] args) throws IOException {
        
        File brainFile = new File("/resources/brainNew.rawb");
        File fractalFile = new File("/resources/branasd.txt");

        NM3DImage image3d = new NM3DImage(brainFile);
        NM3DImage domainBlock = new NM3DImage(NMGlobals.DB_SIDE);
        NM3DImage rangeBlock = new NM3DImage(NMGlobals.DB_SIDE);
        NM3DImage flippedRangeBlock = new NM3DImage(NMGlobals.DB_SIDE);
        NM3DImage reducedImage = new NM3DImage(image3d.side/2);

        double currDistance, minDistance;
        double infinity = Double.POSITIVE_INFINITY;
        int currSymmetry, currRangeX, currRangeY, currRangeZ, domainX, domainY, domainZ, currShift;
        double domainMean, rangeMean;
        NM3DAffineMap bestMap = new NM3DAffineMap();

        // open file for writing
        Writer out = null;
        try {
            out = new BufferedWriter(new FileWriter(fractalFile));
        } catch (IOException ex) {
            System.out.println("Error opening fractal file for writing!\n");
        }

        NM3DUtility.reduceImage(image3d, reducedImage);
        // write header file from image to fractal file
        NM3DUtility.writeHeader(image3d, fractalFile, out);

        // main loop
        int block = image3d.side/4;
        int blockReduced = block/2;
        NM3DImage dummy = new NM3DImage(block);
        NM3DImage dummyReduced = new NM3DImage(block/2);
       // int blockZ = 0, blockX = 0, blockY = 0;

        for(int blockZ = 0; blockZ < image3d.side; blockZ+=block) {
            for(int blockY = 0; blockY < image3d.side; blockY+=block) {
                for(int blockX = 0; blockX < image3d.side; blockX+=block) {
                    System.out.printf("Dx %d Dy %d Dz %d\n", blockX, blockY, blockZ);
                    NM3DUtility.copyImage(image3d, blockX, blockY, blockZ, dummy, 0, 0, 0, block, block, block);
                    NM3DUtility.reduceImage(dummy, dummyReduced);
                    
                    //main loop
                    for(domainZ = 0; domainZ < dummy.side; domainZ += NMGlobals.DB_SIDE) {
                        for(domainY = 0; domainY < dummy.side; domainY += NMGlobals.DB_SIDE) {
                            for(domainX = 0; domainX < dummy.side; domainX += NMGlobals.DB_SIDE ) {
                                //System.out.printf("Dx %d Dy %d Dz %d\n", domainX, domainY, domainZ);
                                minDistance = infinity;

                                NM3DUtility.copyImage(dummy, domainX, domainY, domainZ,
                                                 domainBlock, 0, 0, 0,
                                                 NMGlobals.DB_SIDE, NMGlobals.DB_SIDE, NMGlobals.DB_SIDE);
                                domainMean = NM3DUtility.getMeanImageIntensity(domainBlock);

                                for(currRangeZ = 0; currRangeZ <= (dummyReduced.side - NMGlobals.DB_SIDE); currRangeZ++) {
                                    for(currRangeY = 0; currRangeY <= (dummyReduced.side - NMGlobals.DB_SIDE); currRangeY++) {
                                        for(currRangeX = 0; currRangeX <= (dummyReduced.side - NMGlobals.DB_SIDE); currRangeX++) {
                                            // step3: get range block
                                            NM3DUtility.copyImage(dummyReduced, currRangeX, currRangeY, currRangeZ,
                                                             rangeBlock, 0, 0, 0,
                                                             NMGlobals.DB_SIDE, NMGlobals.DB_SIDE, NMGlobals.DB_SIDE);


                                            rangeMean = NM3DUtility.getMeanImageIntensity(rangeBlock);
                                            currShift = (int)(domainMean - rangeMean);
                                            NM3DUtility.shiftImageIntensity(rangeBlock, currShift);

                                            // step4 loop over symmetries
                                            for(currSymmetry = 0; currSymmetry < NMGlobals.NSYMS; currSymmetry++) {
                                                NM3DUtility.flipImage(rangeBlock, flippedRangeBlock, currSymmetry);
                                                currDistance = NM3DUtility.getL2Distance(domainBlock, flippedRangeBlock);
                                                if(currDistance < minDistance) {
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
                                NM3DUtility.writeBestMap(bestMap, fractalFile, out);
                            }
                        }
                    }
                }
            }
        }
        out.close();
    }
}