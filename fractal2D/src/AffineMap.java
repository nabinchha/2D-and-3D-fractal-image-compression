/**
 *
 * @author nabinchha
 */
class  AffineMap
{
    int rangeX, rangeY;
    short shift;
    char symmetry;

    AffineMap()
    {
        rangeX = 0;
        rangeY = 0;
        symmetry = '0';
        shift = 0;
    }
    void Clear()
    {
        rangeX = 0;
        rangeY = 0;
        symmetry = '0';
        shift = 0;
    }
}

