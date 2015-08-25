import java.awt.Dimension;
import java.awt.Frame;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author nabinchha
 */

public class Decompress3DFast
{
    public static void main(String[] args) throws IOException
    {
        String fractalFile = "/Users/nabinchha/NetBeansProjects/fractal3D/resources/brainFractalat4.txt";
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
                tmap.rangeX = Integer.parseInt(temp[0]);
                tmap.rangeY = Integer.parseInt(temp[1]);
                tmap.rangeZ = Integer.parseInt(temp[2]);
                tmap.symmetry = Integer.parseInt(temp[3]);
                tmap.shift = Integer.parseInt(temp[4]);
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
        int domainX, domainY, domainZ;
        int numMaps = (numVoxels)/(Global.DB_SIDE * Global.DB_SIDE * Global.DB_SIDE);

        MyImage3D image = new MyImage3D(side);
        MyImage3D reducedImage = new MyImage3D(side/2);
        MyImage3D rangeBlock = new MyImage3D(Global.DB_SIDE);
        MyImage3D transformedRangeBlock = new MyImage3D(Global.DB_SIDE);

        int x = 0;
        int block = image.side/4;
        MyImage3D dummy = new MyImage3D(block);
        MyImage3D rdummy = new MyImage3D(block/2);
        
        int count = 0;
        for(int blockZ = 0; blockZ < image.side; blockZ+=block)
        {
            for(int blockY = 0; blockY < image.side; blockY+=block)
            {
                for(int blockX = 0; blockX < image.side; blockX+=block)
                {
                    // Loop over domain blocks
                    for(iterate = 0; iterate < iterates; ++iterate)
                    {
                        x = count*1331;
                        Util3D.ReduceImage(dummy, rdummy);
                        // loop over domain blocks.
                        for(domainZ = 0; domainZ < dummy.side; domainZ += Global.DB_SIDE)
                        {
                            for(domainY = 0; domainY < dummy.side; domainY += Global.DB_SIDE)
                            {
                                for(domainX = 0; domainX < dummy.side; domainX += Global.DB_SIDE, x++)
                                {
                                    AffineMap3D tempMap = maps.get(x);
                                    // extract range block

                                    Util3D.CopyImage(rdummy, tempMap.rangeX, tempMap.rangeY, tempMap.rangeZ,
                                            rangeBlock, 0, 0, 0, Global.DB_SIDE, Global.DB_SIDE, Global.DB_SIDE);

                                    // shift color intensity of range block
                                    Util3D.IntensityShift(rangeBlock, tempMap.shift);

                                    // apply indicated symmetry
                                    Util3D.Flip(rangeBlock, transformedRangeBlock, tempMap.symmetry);

                                    // insert transformed block into image
                                    Util3D.CopyImage(transformedRangeBlock, 0, 0, 0,
                                            dummy, domainX, domainY, domainZ, Global.DB_SIDE, Global.DB_SIDE, Global.DB_SIDE);
                                }
                            }
                        }
                    }//end main for loop
                    Util3D.CopyImage(dummy, 0, 0, 0, image, blockX, blockY, blockZ, block, block, block);
                    count++;
                }
            }
        }


        MyImage sliceImage = new MyImage(image.side, image.side);
        String path = "/Users/nabinchha/NetBeansProjects/fractal3D/resources/image/c";
        File fs;
        for(int i = 0; i < image.side; i+=15)
        {
            fs = new File(path + i +".png");
            sliceImage.GetSliceFrom3D3(image, i);
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
