/**
 *
 * @author Nabin Mulepati
 */

class NM3DAffineMap {
    int rangeX, rangeY, rangeZ;
    int shift, symmetry;

    NM3DAffineMap() {
        rangeX = 0;
        rangeY = 0;
        rangeZ = 0;
        symmetry = 0;
        shift = 0;
    }
    
    void clear() {
        rangeX = 0;
        rangeY = 0;
        rangeZ = 0;
        symmetry = 0;
        shift = 0;
    }
}