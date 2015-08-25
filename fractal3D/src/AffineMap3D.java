/**
 *
 * @author nabinchha
 */

class  AffineMap3D
{
    int rangeX, rangeY, rangeZ;
    int shift, symmetry;

    AffineMap3D()
    {
        rangeX = 0;
        rangeY = 0;
        rangeZ = 0;
        symmetry = 0;
        shift = 0;
    }
    void Clear()
    {
        rangeX = 0;
        rangeY = 0;
        rangeZ = 0;
        symmetry = 0;
        shift = 0;
    }
}