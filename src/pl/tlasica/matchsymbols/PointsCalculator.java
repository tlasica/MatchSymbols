package pl.tlasica.matchsymbols;

import android.util.Log;

import java.util.List;

/**
 * Created by tomek on 23.03.14.
 */
public class PointsCalculator {

    public long getPoints(GameResult res) {
        if (res.success) {
            return pointsForDuration(res.level, res.durationMs);
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

    public long pointsForDuration(int level, long durationMs) {
        long maxDur = getLevelMaxDurationMs(level);
        long basicPoints = 0;
        if (durationMs <= maxDur) {
            float ratio = (float)(maxDur-durationMs) / (float)maxDur;
            basicPoints = (long) (100.0 * ratio);
            return level * basicPoints;
        }
        else return 0;
    }


    public long getLevelMaxDurationMs(int level) {
        switch (level) {
            case 1: return 2000;
            case 2: return 4000;
            case 3: return 8000;
            case 4: return 16000;
            case 5: return 32000;
            case 6: return 64000;
            case 7: return 128000;
            case 8: return 256000;
            default: return 256000;
        }

    }

    public boolean isVeryGoodResult(int level, long dur) {
        long max = getLevelMaxDurationMs(level);
        float ratio = (float)dur / (float)max;
        return ratio < 0.20;
    }
}
