import java.awt.Dimension;
import java.awt.Frame;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author Nabin Mulepati
 */

public class NM2DFractalImageDecompression {
    public static void main(String[] args) {
        String fractalFile = "resources/peppers.txt";
        int iSide = 0;
        ArrayList<NM2DAffineMap> maps = new ArrayList<NM2DAffineMap>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(fractalFile));
            String line = null;
            line = in.readLine();

            // read image header
            String[] parsed = NM2DUtility.parseString(line);
            iSide = Integer.parseInt(parsed[0]);

            line = in.readLine();
            while(line != null) {
                String[] temp = NM2DUtility.parseString(line);
                NM2DAffineMap tmap = new NM2DAffineMap();
                tmap.rangeX = (short)Integer.parseInt(temp[0]);
                tmap.rangeY = (short)Integer.parseInt(temp[1]);
                tmap.symmetry = temp[2].charAt(0);
                tmap.shift = (short)Integer.parseInt(temp[3]);
                maps.add(tmap);
                line = in.readLine();
            }
            in.close();
            System.out.println(maps.size());
        } catch(FileNotFoundException e) {
            System.out.println("File not found!");
        } catch(IOException e) {
            System.out.println("Error reading file!");
        }


        short iterate, argOffset = 0, iterates = NMGlobals.DEFAULT_ITERATES;
        short domainX, domainY;
        int numMaps = (iSide * iSide)/(NMGlobals.DB_SIDE * NMGlobals.DB_SIDE);

        NM2DImage image = new NM2DImage(iSide);
        NM2DImage reducedImage = new NM2DImage(iSide/2);
        NM2DImage rangeBlock = new NM2DImage(NMGlobals.DB_SIDE);
        NM2DImage transformedRangeBlock = new NM2DImage(NMGlobals.DB_SIDE);

        // Loop over domain blocks
        for(iterate = 0; iterate < 32; ++iterate) {
            NM2DUtility.reduceImage(image, reducedImage);

            // loop over domain blocks.

            int x;
            for(x = 0,domainY = 0; domainY < image.side; domainY += NMGlobals.DB_SIDE) {
                for(domainX = 0; domainX < image.side; domainX += NMGlobals.DB_SIDE, x++) {
                    NM2DAffineMap tempMap = maps.get(x);
                    // extract range block
                    NM2DUtility.copyImage(reducedImage, tempMap.rangeX, tempMap.rangeY,
                            rangeBlock, (short)0, (short)0, NMGlobals.DB_SIDE, NMGlobals.DB_SIDE);

                    // shift color intensity of range block
                    NM2DUtility.shiftImageIntensity(rangeBlock, tempMap.shift);

                    // apply indicated symmetry
                    NM2DUtility.flipImage(rangeBlock, transformedRangeBlock, tempMap.symmetry);

                    // insert transformed block into image
                    NM2DUtility.copyImage(transformedRangeBlock, (short)0, (short)0,
                            image, domainX, domainY, NMGlobals.DB_SIDE, NMGlobals.DB_SIDE);
                }

            }
        }
        NMImageCanvas myCanvas = new NMImageCanvas(image.img);
        Frame f = new Frame( "Fractal Image Decompression" );
        f.add("Center", myCanvas);
        f.setSize(new Dimension(image.side,image.side+22));
        f.setVisible(true);
    }
}