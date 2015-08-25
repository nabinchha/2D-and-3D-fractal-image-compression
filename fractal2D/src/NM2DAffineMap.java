/**
 *
 * @author Nabin Mulepati
 */

class NM2DAffineMap {
    int rangeX, rangeY;
    short shift;
    char symmetry;

    NM2DAffineMap() {
        rangeX = 0;
        rangeY = 0;
        symmetry = '0';
        shift = 0;
    }
    
    void clear() {
        rangeX = 0;
        rangeY = 0;
        symmetry = '0';
        shift = 0;
    }
}

