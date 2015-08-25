import java.awt.Dimension;
import java.awt.Frame;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author nabinchha
 */
public class DecompressFast
{
    public static void main(String[] args)
    {
        String fractalFile = "/Users/nabinchha/NetBeansProjects/fractal2D/resources/Test/27.txt";
        int iSide = 0, iNumPix = 0, iDBside = 0, iBlock = 0;
        ArrayList<AffineMap> maps = new ArrayList<AffineMap>();
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(fractalFile));
            String line = null;
            line = in.readLine();

            // read image header
            String[] parsed = Util.ParseString(line);
            iSide = Integer.parseInt(parsed[0]);
            iBlock = Integer.parseInt(parsed[1]);
            iNumPix = Integer.parseInt(parsed[2]);
            iDBside = Integer.parseInt(parsed[3]);

            line = in.readLine();
            while(line != null)
            {
                String[] temp = Util.ParseString(line);
                AffineMap tmap = new AffineMap();
                tmap.rangeX = Integer.parseInt(temp[0]);
                tmap.rangeY = Integer.parseInt(temp[1]);
                tmap.symmetry = temp[2].charAt(0);
                tmap.shift = (short)Integer.parseInt(temp[3]);
                maps.add(tmap);
                line = in.readLine();
            }
            in.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File not found!");
        }
        catch(IOException e)
        {
            System.out.println("Error reading file!");
        }

        int iterate, argOffset = 0, iterates = Global.DEFAULT_ITERATES;
        int domainX, domainY;

        MyImage image = new MyImage(iSide);
        MyImage reducedImage = new MyImage(iSide/2);
        MyImage rangeBlock = new MyImage(iDBside);
        MyImage transformedRangeBlock = new MyImage(iDBside);

        MyImage dummy = new MyImage(iBlock);
        MyImage rdummy = new MyImage(iBlock/2);
        int x = 0;
        int count = 0;
        for(int blockY = 0; blockY < image.side; blockY+= iBlock)
        {
            for(int blockX = 0; blockX < image.side; blockX+= iBlock)
            {
                // Loop over domain blocks
                for(iterate = 0; iterate < 16; ++iterate)
                {
                    x = count*iNumPix;
                    Util.ReduceImage(dummy, rdummy);

                    // loop over domain blocks.
                    for(domainY = 0; domainY < dummy.side; domainY += iDBside)
                    {
                        for(domainX = 0; domainX < dummy.side; domainX += iDBside, x++)
                        {
                            AffineMap tempMap = maps.get(x);
                            // extract range block
                            Util.CopyImage(rdummy, tempMap.rangeX, tempMap.rangeY,
                                    rangeBlock, 0, 0, iDBside, iDBside);

                            // shift color intensity of range block
                            Util.IntensityShift(rangeBlock, tempMap.shift);

                            // apply indicated symmetry
                            Util.Flip(rangeBlock, transformedRangeBlock, tempMap.symmetry);

                            // insert transformed block into image
                            Util.CopyImage(transformedRangeBlock, 0, 0,
                                    dummy, domainX, domainY, iDBside, iDBside);
                        }

                    }
                }
                Util.CopyImage(dummy, 0, 0, image, blockX, blockY, iBlock, iBlock);
                count++;
            }
        }
        //Util.IntensityShift(image, -30);
        MainCanvas myCanvas = new MainCanvas(image.img);
        Frame f = new Frame( "Fractal Image Decompression" );
        f.add("Center", myCanvas);
        f.setSize(new Dimension(image.side, image.side+22));
        f.setVisible(true);
    }
}
