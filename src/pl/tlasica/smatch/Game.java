package pl.tlasica.smatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomek on 22.03.14.
 */
public class Game {

    class Cell {
        public char symbol;
        public int colorIndex;
        public String toString() {
            return symbol+":"+colorIndex;
        }
    }

    int         numRows;
    int         numCols;
    int         numSame;
    Cell[][]    cells;

    public Game(int rows, int cols, int same) {
        numRows = rows;
        numCols = cols;
        numSame = same;
        cells = new Cell[rows][cols];
        for(int r=0; r<numRows; ++r)
            for(int c=0; c<numCols; ++c)
                cells[r][c] = new Cell();
    }

    public Cell cell(int row, int col) {
        assert row>=0 && row<numRows;
        assert col>=0 && col<numCols;
        return cells[row][col];
    }

    public Cell cell(int position) {
        int r = position / numCols;
        int c = position % numCols;
        return cell(r, c);
    }

    public void setSymbol(int row, int col, char symb) {
        assert row>=0 && row<numRows;
        assert col>=0 && col<numCols;
        cells[row][col].symbol = symb;
    }

    public void setColorIndex(int row, int col, int color) {
        assert row>=0 && row<numRows;
        assert col>=0 && col<numCols;
        cells[row][col].colorIndex = color;
    }

    public int numRows() {
        return  numRows;
    }

    public int numCols() {
        return  numCols;
    }

    public int[] solution() {
        Map<Character, Integer> map = new HashMap<Character, Integer>();
        int numCells = numRows * numCols;
        for(int i=0; i<numCells; ++i ) {
            Cell cell = cell(i);
            if (map.containsKey(cell.symbol)) {
                int prev = map.get(cell.symbol);
                int[] res = {prev, i};
                return res;
            }
            map.put(cell.symbol, i);
        }
        return null;
    }

    public String toString() {
        return Arrays.deepToString(cells);
    }
}
