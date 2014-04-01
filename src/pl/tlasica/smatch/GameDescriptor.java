package pl.tlasica.smatch;

/**
 * Created by tomek on 28.03.14.
 */
public class GameDescriptor {
    int         numRows;
    int         numCols;
    int         numSame;

    public GameDescriptor(int r, int c, int same) {
        numRows = r;
        numCols = c;
        numSame = same;
    }
}
