import java.awt.Dimension;
import java.awt.Frame;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author nabinchha
 */

public class Decompress3D
{
    public static void main(String[] args)
    {
        String fractalFile = "/Users/nabinchha/NetBeansProjects/fractal3D/resources/HSImageFractal.txt";
        int side = 0, numVoxels = 0;
        ArrayList<AffineMap3D> maps = new ArrayList<AffineMap3D>();
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(fractalFile));
            String line = null;
            line = in.readLine();

            // read image header
            String[] parsed = Util3D.ParseString(line);
            side = Integer.parseInt(parsed[0]);
            numVoxels = side * side * side;


            line = in.readLine();
            while(line != null)
            {
                String[] temp = Util3D.ParseString(line);
                AffineMap3D tmap = new AffineMap3D();
                tmap.rangeX = (short)Integer.parseInt(temp[0]);
                tmap.rangeY = (short)Integer.parseInt(temp[1]);
                tmap.rangeZ = (short)Integer.parseInt(temp[2]);
                tmap.symmetry = temp[3].charAt(0);
                tmap.shift = (short)Integer.parseInt(temp[4]);
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

        short iterate, argOffset = 0, iterates = Global.DEFAULT_ITERATES;
        short domainX, domainY, domainZ;
        int numMaps = (numVoxels)/(Global.DB_SIDE * Global.DB_SIDE * Global.DB_SIDE);

        MyImage3D image = new MyImage3D(side);
        MyImage3D reducedImage = new MyImage3D(side/2);
        MyImage3D rangeBlock = new MyImage3D(Global.DB_SIDE);
        MyImage3D transformedRangeBlock = new MyImage3D(Global.DB_SIDE);


        // Loop over domain blocks
        for(iterate = 0; iterate < iterates; ++iterate)
        {
            Util3D.ReduceImage(image, reducedImage);

            // loop over domain blocks.

            int x;
            for(x = 0,domainZ = 0; domainZ < image.side; domainZ += Global.DB_SIDE)
            {
                for(domainY = 0; domainY < image.side; domainY += Global.DB_SIDE)
                {
                    for(domainX = 0; domainX < image.side; domainX += Global.DB_SIDE, x++)
                    {
                        AffineMap3D tempMap = maps.get(x);

                        // extract range block
                        Util3D.CopyImage(reducedImage, tempMap.rangeX, tempMap.rangeY, tempMap.rangeZ,
                                rangeBlock, 0, 0, 0, Global.DB_SIDE, Global.DB_SIDE, Global.DB_SIDE);

                        // shift color intensity of range block
                        Util3D.IntensityShift(rangeBlock, tempMap.shift);

                        // apply indicated symmetry
                        Util3D.Flip(rangeBlock, transformedRangeBlock, tempMap.symmetry);

                        // insert transformed block into image
                        Util3D.CopyImage(transformedRangeBlock, 0, 0, 0,
                                image, domainX, domainY, domainZ, Global.DB_SIDE, Global.DB_SIDE, Global.DB_SIDE);
                    }
                }
            }

        }

        int slice = 35;
        MyImage sliceImage = new MyImage(image.side, image.side);
        sliceImage.GetSliceFrom3D2(image, slice);

        MainCanvas myCanvas = new MainCanvas(sliceImage.img);
        Frame f = new Frame( "Fractal Image Decompression" );
        f.add("Center", myCanvas);
        f.setSize(new Dimension(sliceImage.width,sliceImage.height+22));
        f.setVisible(true);
    }
}
