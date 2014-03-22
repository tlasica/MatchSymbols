package pl.tlasica.matchsymbols;

import junit.framework.TestCase;

/**
 * Created by tomek on 22.03.14.
 */
public class GameGeneratorTest extends TestCase {
    public void testGenerate() throws Exception {
        int numColors = 4;
        GameGenerator gen = new GameGenerator(3,3,1,numColors);
        Game game = gen.generate();

    }
}
