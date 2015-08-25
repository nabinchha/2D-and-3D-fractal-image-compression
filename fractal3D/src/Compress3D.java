import java.awt.Dimension;
import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.imageio.ImageIO;

/**
 *
 * @author nabinchha
 */

public class Compress3D
{
    public static void main(String[] args) throws IOException
    {
        File brainFile = new File("/Users/nabinchha/NetBeansProjects/fractal3D/resources/brainNew.rawb");
        File fractalFile = new File("/users/nabinchha/netbeansprojects/fractal3D/resources/brainas.txt");

        MyImage3D image3d = new MyImage3D(brainFile);

        MyImage sliceImage = new MyImage(image3d.side, image3d.side);
        String path = "/Users/nabinchha/NetBeansProjects/fractal3D/resources/image/originalC";
        File fs;
        for(int i = 175; i < image3d.side; i+=15)
        {
            fs = new File(path + i +".png");
            sliceImage.GetSliceFrom3D3(image3d, i);
            ImageIO.write(sliceImage.img, "png", fs);
        }


        /*

        MyImage3D domainBlock = new MyImage3D(Global.DB_SIDE);
        MyImage3D rangeBlock = new MyImage3D(Global.DB_SIDE);
        MyImage3D flippedRangeBlock = new MyImage3D(Global.DB_SIDE);

//        MyImage3D smallImage = new MyImage3D(image3d.side/2);
//        MyImage3D smallImage1 = new MyImage3D(smallImage.side/2);
        MyImage3D reducedImage = new MyImage3D(image3d.side/2);

        double currDistance, minDistance;
        double infinity = Double.POSITIVE_INFINITY;
        int currSymmetry;
        int currRangeX, currRangeY, currRangeZ, domainX, domainY, domainZ, currShift;
        double domainMean, rangeMean;
        AffineMap3D bestMap = new AffineMap3D();

        // open file for writing
        Writer out = null;
        try {
            out = new BufferedWriter(new FileWriter(fractalFile));
        } catch (IOException ex) {
            System.out.println("Error opening fractal file for writing!\n");
        }


        // rescale(contract) image in spatial and intensity directions
        //Util3D.ReduceImage(image3d, smallImage);
        //Util3D.ReduceImage(smallImage, smallImage1);
        //image3d = smallImage1;
        Util3D.ReduceImage(image3d, reducedImage);

        //MyImage3D asdf = new MyImage3D(image3d.side);
        //Util3D.Flip(image3d, asdf, 8);

        // write header file from image to fractal file
        Util3D.WriteHeader(image3d, fractalFile, out);
        /*
        // main loop
        for(domainZ = 0; domainZ < image3d.side; domainZ += Global.DB_SIDE)
        {
            for(domainY = 0; domainY < image3d.side; domainY += Global.DB_SIDE)
            {
                for(domainX = 0; domainX < image3d.side; domainX += Global.DB_SIDE )
                {
                    System.out.printf("Dx %d Dy %d Dz %d\n", domainX, domainY, domainZ);

                    minDistance = infinity;

                    Util3D.CopyImage(image3d, domainX, domainY, domainZ, 
                                     domainBlock, 0, 0, 0,
                                     Global.DB_SIDE, Global.DB_SIDE, Global.DB_SIDE);
                    domainMean = Util3D.Mean(domainBlock);

                    for(currRangeZ = 0; currRangeZ <= (reducedImage.side - Global.DB_SIDE); currRangeZ++)
                    {
                        for(currRangeY = 0; currRangeY <= (reducedImage.side - Global.DB_SIDE); currRangeY++)
                        {
                            for(currRangeX = 0; currRangeX <= (reducedImage.side - Global.DB_SIDE); currRangeX++)
                            {
                                // step3: get range block

                                Util3D.CopyImage(reducedImage, currRangeX, currRangeY, currRangeZ, 
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
        
        out.close();
        
        int slice = 88;
        MyImage sliceImage = new MyImage(image3d.side, image3d.side);
        sliceImage.GetSliceFrom3D3(image3d, slice);
        MainCanvas myCanvas = new MainCanvas(sliceImage.img);
        Frame f = new Frame( "3D Fractal Image Compression" );
        f.add("Center", myCanvas);
        f.setSize(new Dimension(sliceImage.width,sliceImage.height+22));
        f.setVisible(true);
        */
         
    }
}
