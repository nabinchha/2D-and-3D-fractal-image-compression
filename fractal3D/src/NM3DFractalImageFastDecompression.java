import java.awt.Dimension;
import java.awt.Frame;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author Nabin Mulepati
 */

public class NM3DFractalImageFastDecompression {
    public static void main(String[] args) throws IOException {
        String fractalFile = "/Users/nabinchha/NetBeansProjects/fractal3D/resources/brainFractalat4.txt";
        int side = 0, numVoxels = 0;
        ArrayList<NM3DAffineMap> maps = new ArrayList<NM3DAffineMap>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(fractalFile));
            String line = null;
            line = in.readLine();

            // read image header
            String[] parsed = NM3DUtility.parseString(line);
            side = Integer.parseInt(parsed[0]);
            numVoxels = side * side * side;


            line = in.readLine();
            while(line != null) {
                String[] temp = NM3DUtility.parseString(line);
                NM3DAffineMap tmap = new NM3DAffineMap();
                tmap.rangeX = Integer.parseInt(temp[0]);
                tmap.rangeY = Integer.parseInt(temp[1]);
                tmap.rangeZ = Integer.parseInt(temp[2]);
                tmap.symmetry = Integer.parseInt(temp[3]);
                tmap.shift = Integer.parseInt(temp[4]);
                maps.add(tmap);
                line = in.readLine();
            }
            in.close();
        } catch(FileNotFoundException e) {
            System.out.println("File not found!");
        } catch(IOException e) {
            System.out.println("Error reading file!");
        }

        short iterate, argOffset = 0, iterates = NMGlobals.DEFAULT_ITERATES;
        int domainX, domainY, domainZ;
        int numMaps = (numVoxels)/(NMGlobals.DB_SIDE * NMGlobals.DB_SIDE * NMGlobals.DB_SIDE);

        NM3DImage image = new NM3DImage(side);
        NM3DImage reducedImage = new NM3DImage(side/2);
        NM3DImage rangeBlock = new NM3DImage(NMGlobals.DB_SIDE);
        NM3DImage transformedRangeBlock = new NM3DImage(NMGlobals.DB_SIDE);

        int x = 0;
        int block = image.side/4;
        NM3DImage dummy = new NM3DImage(block);
        NM3DImage rdummy = new NM3DImage(block/2);
        
        int count = 0;
        for(int blockZ = 0; blockZ < image.side; blockZ+=block) {
            for(int blockY = 0; blockY < image.side; blockY+=block) {
                for(int blockX = 0; blockX < image.side; blockX+=block) {
                    // Loop over domain blocks
                    for(iterate = 0; iterate < iterates; ++iterate) {
                        x = count*1331;
                        NM3DUtility.reduceImage(dummy, rdummy);
                        // loop over domain blocks.
                        for(domainZ = 0; domainZ < dummy.side; domainZ += NMGlobals.DB_SIDE) {
                            for(domainY = 0; domainY < dummy.side; domainY += NMGlobals.DB_SIDE) {
                                for(domainX = 0; domainX < dummy.side; domainX += NMGlobals.DB_SIDE, x++) {
                                    NM3DAffineMap tempMap = maps.get(x);
                                    // extract range block

                                    NM3DUtility.copyImage(rdummy, tempMap.rangeX, tempMap.rangeY, tempMap.rangeZ,
                                            rangeBlock, 0, 0, 0, NMGlobals.DB_SIDE, NMGlobals.DB_SIDE, NMGlobals.DB_SIDE);

                                    // shift color intensity of range block
                                    NM3DUtility.shiftImageIntensity(rangeBlock, tempMap.shift);

                                    // apply indicated symmetry
                                    NM3DUtility.flipImage(rangeBlock, transformedRangeBlock, tempMap.symmetry);

                                    // insert transformed block into image
                                    NM3DUtility.copyImage(transformedRangeBlock, 0, 0, 0,
                                            dummy, domainX, domainY, domainZ, NMGlobals.DB_SIDE, NMGlobals.DB_SIDE, NMGlobals.DB_SIDE);
                                }
                            }
                        }
                    }//end main for loop
                    NM3DUtility.copyImage(dummy, 0, 0, 0, image, blockX, blockY, blockZ, block, block, block);
                    count++;
                }
            }
        }

        NM2DImage sliceImage = new NM2DImage(image.side, image.side);
        String path = "/Users/nabinchha/NetBeansProjects/fractal3D/resources/image/c";
        File fs;
        for(int i = 0; i < image.side; i+=15) {
            fs = new File(path + i +".png");
            sliceImage.getSliceFrom3D3(image, i);
            ImageIO.write(sliceImage.img, "png", fs);
        }
           
        /*
        MainCanvas myCanvas = new MainCanvas(sliceImage.img);
        Frame f = new Frame( "3D Fractal Image Decompression" );
        f.add("Center", myCanvas);
        f.setSize(new Dimension(sliceImage.width,sliceImage.height+22));
        f.setVisible(true);
        */
    }
}