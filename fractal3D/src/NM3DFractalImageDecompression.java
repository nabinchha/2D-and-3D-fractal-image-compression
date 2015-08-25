import java.awt.Dimension;
import java.awt.Frame;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author Nabin Mulepati
 */

public class NM3DFractalImageDecompression {
    public static void main(String[] args) {
        String fractalFile = "resources/HSImageFractal.txt";
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
                tmap.rangeX = (short)Integer.parseInt(temp[0]);
                tmap.rangeY = (short)Integer.parseInt(temp[1]);
                tmap.rangeZ = (short)Integer.parseInt(temp[2]);
                tmap.symmetry = temp[3].charAt(0);
                tmap.shift = (short)Integer.parseInt(temp[4]);
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
        short domainX, domainY, domainZ;
        int numMaps = (numVoxels)/(NMGlobals.DB_SIDE * NMGlobals.DB_SIDE * NMGlobals.DB_SIDE);

        NM3DImage image = new NM3DImage(side);
        NM3DImage reducedImage = new NM3DImage(side/2);
        NM3DImage rangeBlock = new NM3DImage(NMGlobals.DB_SIDE);
        NM3DImage transformedRangeBlock = new NM3DImage(NMGlobals.DB_SIDE);

        // Loop over domain blocks
        for(iterate = 0; iterate < iterates; ++iterate) {
            NM3DUtility.reduceImage(image, reducedImage);

            // loop over domain blocks.

            int x;
            for(x = 0,domainZ = 0; domainZ < image.side; domainZ += NMGlobals.DB_SIDE) {
                for(domainY = 0; domainY < image.side; domainY += NMGlobals.DB_SIDE) {
                    for(domainX = 0; domainX < image.side; domainX += NMGlobals.DB_SIDE, x++) {
                        NM3DAffineMap tempMap = maps.get(x);

                        // extract range block
                        NM3DUtility.copyImage(reducedImage, tempMap.rangeX, tempMap.rangeY, tempMap.rangeZ,
                                rangeBlock, 0, 0, 0, NMGlobals.DB_SIDE, NMGlobals.DB_SIDE, NMGlobals.DB_SIDE);

                        // shift color intensity of range block
                        NM3DUtility.shiftImageIntensity(rangeBlock, tempMap.shift);

                        // apply indicated symmetry
                        NM3DUtility.flipImage(rangeBlock, transformedRangeBlock, tempMap.symmetry);

                        // insert transformed block into image
                        NM3DUtility.copyImage(transformedRangeBlock, 0, 0, 0,
                                image, domainX, domainY, domainZ, NMGlobals.DB_SIDE, NMGlobals.DB_SIDE, NMGlobals.DB_SIDE);
                    }
                }
            }
        }

        int slice = 35;
        NM2DImage sliceImage = new NM2DImage(image.side, image.side);
        sliceImage.getSliceFrom3D2(image, slice);

        NMImageCanvas myCanvas = new NMImageCanvas(sliceImage.img);
        Frame f = new Frame( "Fractal Image Decompression" );
        f.add("Center", myCanvas);
        f.setSize(new Dimension(sliceImage.width,sliceImage.height+22));
        f.setVisible(true);
    }
}