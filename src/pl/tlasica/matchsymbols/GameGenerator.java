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
        Level levelObj = Level.create(level);
        return new GameGenerator(levelObj.gameDescr, numColors);
    }

    public GameGenerator(GameDescriptor gameDescr, int colors) {
        numRows = gameDescr.numRows;
        numCols = gameDescr.numCols;
        numIdentical = gameDescr.numSame;
        numColors = colors;
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
