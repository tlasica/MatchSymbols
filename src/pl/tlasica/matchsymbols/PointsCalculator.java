package pl.tlasica.matchsymbols;

import android.util.Log;

import java.util.List;

/**
 * Created by tomek on 23.03.14.
 */
public class PointsCalculator {

    private Level level;

    public PointsCalculator(Level l) {
        this.level = l;
    }

    public long getPoints(GameResult res) {
        if (res.success) {
            return pointsForDuration(res.durationMs);
        }
        else return 0;
    }

    public long getPoints(List<GameResult> history) {
        long sum = 0;
        for(GameResult r: history) {
            long p = getPoints(r);
            sum += p;
        }
        return sum;
    }

    public long pointsForDuration(long durationMs) {
        long maxDur = level.durationMaxMs;
        long basicPoints = 0;
        if (durationMs <= maxDur) {
            float ratio = (float)(maxDur-durationMs) / (float)maxDur;
            basicPoints = (long) (100.0 * ratio);
            return (long)(levelWeight() * (float)basicPoints);
        }
        else return 0;
    }

    public double levelWeight() {
        switch (level.levelNum) {
            case 1: return 1;
            case 2: return 1.2;
            case 3: return 1.4;
            case 4: return 2;
            case 5: return 3;
            case 6: return 5;
            case 7: return 9;
            case 8: return 10;
            case 9: return 11;
            case 10: return 12;
            case 11: return 13;
            case 12: return 14;
            case 13: return 15;
            case 14: return 15.5;
            case 15: return 16;
            case 16: return 16.5;
            case 17: return 17;
            case 18: return 18;
            case 19: return 19;
            case 20: return 20;
            default: return 20;
        }
    }

    public long maxPointsPossible() {
        return (long)(100 * levelWeight());
    }

}
