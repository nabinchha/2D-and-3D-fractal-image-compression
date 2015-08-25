import java.awt.Dimension;
import java.awt.Frame;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author Nabin Mulepati
 */
public class NM2DFractalImageFastDecompression {
    public static void main(String[] args) {
        String fractalFile = "resources/Test/28.txt";
        int iSide = 0, iNumPix = 0, iDBside = 0, iBlock = 0;
        ArrayList<NM2DAffineMap> maps = new ArrayList<NM2DAffineMap>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(fractalFile));
            String line = null;
            line = in.readLine();

            // read image header
            String[] parsed = NM2DUtility.parseString(line);
            iSide = Integer.parseInt(parsed[0]);
            iBlock = Integer.parseInt(parsed[1]);
            iNumPix = Integer.parseInt(parsed[2]);
            iDBside = Integer.parseInt(parsed[3]);

            line = in.readLine();
            while (line != null) {
                String[] temp = NM2DUtility.parseString(line);
                NM2DAffineMap tmap = new NM2DAffineMap();
                tmap.rangeX = Integer.parseInt(temp[0]);
                tmap.rangeY = Integer.parseInt(temp[1]);
                tmap.symmetry = temp[2].charAt(0);
                tmap.shift = (short)Integer.parseInt(temp[3]);
                maps.add(tmap);
                line = in.readLine();
            }
            in.close();
        } catch(FileNotFoundException e) {
            System.out.println("File not found!");
        } catch(IOException e) {
            System.out.println("Error reading file!");
        }

        int iterate, argOffset = 0, iterates = NMGlobals.DEFAULT_ITERATES;
        int domainX, domainY;

        NM2DImage image = new NM2DImage(iSide);
        NM2DImage reducedImage = new NM2DImage(iSide/2);
        NM2DImage rangeBlock = new NM2DImage(iDBside);
        NM2DImage transformedRangeBlock = new NM2DImage(iDBside);

        NM2DImage dummy = new NM2DImage(iBlock);
        NM2DImage rdummy = new NM2DImage(iBlock/2);
        
        int x = 0;
        int count = 0;
        for(int blockY = 0; blockY < image.side; blockY+= iBlock) {
            for(int blockX = 0; blockX < image.side; blockX+= iBlock) {
                // Loop over domain blocks
                for(iterate = 0; iterate < 16; ++iterate) {
                    x = count*iNumPix;
                    NM2DUtility.reduceImage(dummy, rdummy);

                    // loop over domain blocks.
                    for(domainY = 0; domainY < dummy.side; domainY += iDBside) {
                        for(domainX = 0; domainX < dummy.side; domainX += iDBside, x++) {
                            NM2DAffineMap tempMap = maps.get(x);
                            // extract range block
                            NM2DUtility.copyImage(rdummy, tempMap.rangeX, tempMap.rangeY,
                                    rangeBlock, 0, 0, iDBside, iDBside);

                            // shift color intensity of range block
                            NM2DUtility.shiftImageIntensity(rangeBlock, tempMap.shift);

                            // apply indicated symmetry
                            NM2DUtility.flipImage(rangeBlock, transformedRangeBlock, tempMap.symmetry);

                            // insert transformed block into image
                            NM2DUtility.copyImage(transformedRangeBlock, 0, 0,
                                    dummy, domainX, domainY, iDBside, iDBside);
                        }
                    }
                }
                NM2DUtility.copyImage(dummy, 0, 0, image, blockX, blockY, iBlock, iBlock);
                count++;
            }
        }
        //Util.shiftImageIntensity(image, -30);
        NMImageCanvas myCanvas = new NMImageCanvas(image.img);
        Frame f = new Frame( "Fractal Image Decompression" );
        f.add("Center", myCanvas);
        f.setSize(new Dimension(image.side, image.side+22));
        f.setVisible(true);
    }
}
