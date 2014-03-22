package pl.tlasica.matchsymbols;

import java.util.*;

/**
 * Created by tomek on 22.03.14.
 * Generates a game with given size (rows and columns)
 * and given number of same symbols
 */
public class GameGenerator {

    int numRows;
    int numCols;
    int numPairs;
    int numColors;
    Random random = new Random(System.currentTimeMillis());

    public GameGenerator(int r, int c, int p, int k) {
        numRows = r;
        numCols = c;
        numPairs = p;
        numColors = k;
    }

    public Game generate() {
        Game game = new Game(numRows, numCols);
        // generate unique symbols
        int numSymbols = numRows * numCols - numPairs;
        List<Character> symbols = uniqueSymbols(numSymbols);
        // add pairs for random symbols
        Set<Integer> pairPos = new HashSet<Integer>();
        while(pairPos.size()<numPairs) {
            int pos = random.nextInt(numSymbols);
            boolean isNewPos = pairPos.add(pos);
            if (isNewPos) {
                symbols.add( symbols.get(pos));
            }
        }
        assert symbols.size()==numSymbols;
        // shuffle list
        Collections.shuffle(symbols, random);
        // random colors for each symbol
        Map<Character, Integer> symbColors = new HashMap<Character, Integer>();
        for(Character c: symbols) {
            if (!symbColors.containsValue(c)) {
                int color = random.nextInt(numColors);
                symbColors.put(c, color);
            }
        }
        // set game symbols & colors
        for(int r=0; r<numRows; ++r) {
            for(int c=0; c<numCols; ++c) {
                int idx = r * numCols + c;
                char symb = symbols.get(idx);
                game.setSymbol(r, c, symb);
                int color = symbColors.get(symb);
                game.setColorIndex(r, c, color);
            }
        }
        // TODO: set symbol colors


        return game;
    }

    private List<Character> uniqueSymbols(int n) {
        Set<Character> set = new HashSet<Character>(n);
        List<Character> symbols = new ArrayList<Character>();
        while(set.size()<n) {
            Character c = randomSymbol();
            if (set.add(c)) {
                symbols.add(c);
            }
        }
        return symbols;
    }

    private char randomSymbol() {
        char c = (char)('A' + random.nextInt(26));
        return c;
    }

}
