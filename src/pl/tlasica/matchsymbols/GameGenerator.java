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
    int numIdentical;
    int numColors;
    Random random = new Random(System.currentTimeMillis());

    public static GameGenerator create(int level, int numColors) {
        switch (level) {
            case 1: return new GameGenerator(2, 2, 2, numColors);
            case 2: return new GameGenerator(3, 2, 2, numColors);
            case 3: return new GameGenerator(3, 3, 2, numColors);
            case 4: return new GameGenerator(4, 3, 2, numColors);
            case 5: return new GameGenerator(4, 4, 2, numColors);
            case 6: return new GameGenerator(5, 4, 2, numColors);
            case 7: return new GameGenerator(6, 4, 2, numColors);
            case 8: return new GameGenerator(6, 5, 2, numColors);
            default: return new GameGenerator(6, 4, 3, numColors);
        }
    }

    public GameGenerator(int r, int c, int p, int k) {
        numRows = r;
        numCols = c;
        numIdentical = p;
        numColors = k;
    }

    //TODO: algorytm generowania będzie do zmiany gdy zmieni się #ident
    public Game generate() {
        Game game = new Game(numRows, numCols, numIdentical);
        // generate unique symbols
        int numUniqSymbols = numRows * numCols - (numIdentical-1);
        List<Character> symbols = uniqueSymbols(numUniqSymbols);
        // assign colors
        Map<Character, Integer> symbolColors = new HashMap<Character, Integer>();
        for(Character c: symbols) {
            int color = random.nextInt(numColors);
            symbolColors.put(c, color);
        }
        // choose random symbol and add it to symbols so that numIdentical are present
        int pos = random.nextInt(symbols.size());
        Character original = symbols.get(pos);
        List<Character> copies = Collections.nCopies(numIdentical-1, original);
        symbols.addAll(copies);
        assert symbols.size()==numRows * numCols;
        // shuffle list
        Collections.shuffle(symbols, random);
        // set game symbols & colors
        for(int r=0; r<numRows; ++r) {
            for(int c=0; c<numCols; ++c) {
                int idx = r * numCols + c;
                char symb = symbols.get(idx);
                game.setSymbol(r, c, symb);
                int color = symbolColors.get(symb);
                game.setColorIndex(r, c, color);
            }
        }
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
