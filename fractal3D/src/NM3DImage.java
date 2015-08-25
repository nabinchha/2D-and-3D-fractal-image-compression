import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Nabin Mulepati
 */

class NM3DImage {
    int side;
    int vside;
    int numVoxels;
    private int voxel[][][];
    private byte data[];

    NM3DImage(File brainFile) {
        side = 181;
        vside = side - side % 4;
        numVoxels = side * side * side;
        voxel = new int[side][side][side];
        data = new byte[numVoxels];

        try {
            FileInputStream input = new FileInputStream(brainFile);
            input.read(data);
        } catch(FileNotFoundException e) {
            System.out.println("File not found!");
        } catch(IOException e) {
            System.out.println("Error reading file!");
        }

        //initialize voxels
        int count = 0;
        for(int j = 0; j < side; ++j) {
            for(int i = 0; i < side; ++i) {
                for(int k = 0; k < side; ++k) {
                    voxel[i][j][k] = (int)data[count] & 0xff;
                    count++;
                }
            }
        }
        side = vside;
        numVoxels = side * side * side;
    }

    NM3DImage(int sideValue) {
        side = sideValue;

        numVoxels = side * side * side;
        voxel = new int[side][side][side];

        //initialize voxels
        int count = 0;
        for(int j = 0; j < side; ++j) {
            for(int i = 0; i < side; ++i) {
                for(int k = 0; k < side; ++k) {
                    voxel[i][j][k] = 0;
                    count++;
                }
            }
        }
    }

    // we're assuming that the input image is greyscale
    public int getGreyVoxelIntensity(int x, int y, int z) {
        return (voxel[x][y][z]);
    }

    public void setGreyVoxelIntensity(int x, int y, int z, int grey) {
        voxel[x][y][z] = grey;
    }
}
