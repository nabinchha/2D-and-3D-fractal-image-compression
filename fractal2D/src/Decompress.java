import java.awt.Dimension;
import java.awt.Frame;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author nabinchha
 */
public class Decompress
{
    public static void main(String[] args)
    {
        String fractalFile = "/Users/nabinchha/NetBeansProjects/fractal2D/resources/peppers.txt";
        int iSide = 0;
        ArrayList<AffineMap> maps = new ArrayList<AffineMap>();
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(fractalFile));
            String line = null;
            line = in.readLine();

            // read image header
            String[] parsed = Util.ParseString(line);
            iSide = Integer.parseInt(parsed[0]);

            line = in.readLine();
            while(line != null)
            {
                String[] temp = Util.ParseString(line);
                AffineMap tmap = new AffineMap();
                tmap.rangeX = (short)Integer.parseInt(temp[0]);
                tmap.rangeY = (short)Integer.parseInt(temp[1]);
                tmap.symmetry = temp[2].charAt(0);
                tmap.shift = (short)Integer.parseInt(temp[3]);
                maps.add(tmap);
                line = in.readLine();
            }
            in.close();
            System.out.println(maps.size());
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File not found!");
        }
        catch(IOException e)
        {
            System.out.println("Error reading file!");
        }


        short iterate, argOffset = 0, iterates = Global.DEFAULT_ITERATES;
        short domainX, domainY;
        int numMaps = (iSide * iSide)/(Global.DB_SIDE * Global.DB_SIDE);

        MyImage image = new MyImage(iSide);
        MyImage reducedImage = new MyImage(iSide/2);
        MyImage rangeBlock = new MyImage(Global.DB_SIDE);
        MyImage transformedRangeBlock = new MyImage(Global.DB_SIDE);


        // Loop over domain blocks
        for(iterate = 0; iterate < 32; ++iterate)
        {
            Util.ReduceImage(image, reducedImage);

            // loop over domain blocks.

            int x;
            for(x = 0,domainY = 0; domainY < image.side; domainY += Global.DB_SIDE)
            {
                for(domainX = 0; domainX < image.side; domainX += Global.DB_SIDE, x++)
                {
                    AffineMap tempMap = maps.get(x);
                    // extract range block
                    Util.CopyImage(reducedImage, tempMap.rangeX, tempMap.rangeY,
                            rangeBlock, (short)0, (short)0, Global.DB_SIDE, Global.DB_SIDE);

                    // shift color intensity of range block
                    Util.IntensityShift(rangeBlock, tempMap.shift);

                    // apply indicated symmetry
                    Util.Flip(rangeBlock, transformedRangeBlock, tempMap.symmetry);

                    // insert transformed block into image
                    Util.CopyImage(transformedRangeBlock, (short)0, (short)0,
                            image, domainX, domainY, Global.DB_SIDE, Global.DB_SIDE);
                }

            }
        }
        MainCanvas myCanvas = new MainCanvas(image.img);
        Frame f = new Frame( "Fractal Image Decompression" );
        f.add("Center", myCanvas);
        f.setSize(new Dimension(image.side,image.side+22));
        f.setVisible(true);
    }
}
