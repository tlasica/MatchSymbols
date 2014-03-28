package pl.tlasica.matchsymbols;

/**
 * Created by tomek on 28.03.14.
 */
public class Level {

    public static final int MAX_LEVEL_NUM = 20;

    int             levelNum;
    GameDescriptor  gameDescr;
    long            durationMaxMs;
    long            durationGoldMs;
    long            durationSilverMs;

    public static Level create(int level) {
        switch (level) {
            case 1: return new Level(1, new GameDescriptor(2,2,2), 3000, 200, 500);
            case 2: return new Level(2, new GameDescriptor(3,2,2), 4000, 300, 700);
            case 3: return new Level(3, new GameDescriptor(3,3,2), 6000, 400, 900);
            case 4: return new Level(4, new GameDescriptor(4,3,2), 8000, 500, 1100);
            case 5: return new Level(5, new GameDescriptor(4,4,2), 10000, 600, 1300);
            case 6: return new Level(6, new GameDescriptor(5,4,2), 12000, 700, 1500);
            case 7: return new Level(7, new GameDescriptor(6,4,2), 14000, 800, 1700);
            case 8: return new Level(8, new GameDescriptor(6,5,2), 16000, 900, 1900);
            case 9: return new Level(9, new GameDescriptor(6,5,2), 18000, 1000, 2000);
            case 10: return new Level(10, new GameDescriptor(6,5,2), 17000, 1000, 2000);
            case 11: return new Level(11, new GameDescriptor(6,5,2), 16000, 1000, 2000);
            case 12: return new Level(12, new GameDescriptor(6,5,2), 15000, 1000, 2000);
            case 13: return new Level(13, new GameDescriptor(6,5,2), 14000, 1000, 2000);
            case 14: return new Level(14, new GameDescriptor(6,5,2), 13000, 1000, 2000);
            case 15: return new Level(15, new GameDescriptor(6,5,2), 12000, 1000, 2000);
            case 16: return new Level(16, new GameDescriptor(6,5,2), 11000, 1000, 2000);
            case 17: return new Level(17, new GameDescriptor(6,5,2), 10000, 1000, 2000);
            case 18: return new Level(18, new GameDescriptor(6,5,2), 9000, 1000, 2000);
            case 19: return new Level(19, new GameDescriptor(6,5,2), 8000, 1000, 2000);
            case 20: return new Level(20, new GameDescriptor(6,5,2), 6000, 1000, 2000);
            default: return create(20);
        }
    }

    private Level(int l, GameDescriptor descr, long durMax, long durGold, long durSilver) {
        levelNum = l;
        gameDescr = descr;
        durationMaxMs = durMax;
        durationGoldMs = durGold;
        durationSilverMs = durSilver;
    }

    public boolean maxDurationExceeded(long durMs) {
        return (durMs>durationMaxMs);
    }

    public boolean goldReward(long durMs) {
        return (durMs<=durationGoldMs);
    }

    public boolean silverReward(long durMs) {
        return (durMs<=durationSilverMs);
    }

    public int next() {
        return levelNum+1;
    }
}
