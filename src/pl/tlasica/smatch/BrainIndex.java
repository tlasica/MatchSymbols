package pl.tlasica.smatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import java.util.List;

/**
 * Created by tomek on 28.03.14.
 */
public class BrainIndex {

    private static final int MAX_INDEX = 1000;
    private String      KEY_IDX = "BrainIndex";

    private Context context;

    public BrainIndex(Context ctx) {
        context = ctx;
    }

    public static int maxIndex() {
        return MAX_INDEX;
    }

    public int currentIndex() {
        int r = preferences().getInt(KEY_IDX, 0);
        return r;
    }

    public int change(int newIndex) {
        return newIndex - currentIndex();
    }

    public void storeIndex(int newIndex) {
        SharedPreferences.Editor editor = preferences().edit();
        editor.putInt(KEY_IDX, newIndex);
        editor.commit();
    }

    public int calculate(List<GameResult> history) {
        if (history.size()<=1) return 0;
        // get maximum finished level (assuming that last result is a failure)
        int maxSuccLevel = history.get(history.size()-1).level-1;
        // for up to 4 maximum successfull rounds calculate %time
        int start = history.size()-5;
        int end = history.size()-2;
        if (start<0) start = 0;
        if (end<0) end = 0;
        double sumWeight = 0;
        double sumPoints = 0;
        for(GameResult res: history.subList(start, end)) {
            assert res.success;
            if (res.success) {
                Level l = Level.create(res.level);
                PointsCalculator calc = new PointsCalculator(l);
                double maxDur = (l.durationMaxMs - l.durationGoldMs);
                double actDur = res.durationMs;
                double percent = 1.0 - actDur / maxDur;
                if (percent<0.0) percent = 0.0;
                double weight = calc.levelWeight();
                sumWeight += weight;
                sumPoints += percent * weight;
            }
        }
        // calculate total
        Pair<Integer, Integer> range = indexRange(maxSuccLevel);
        double timePoints = (sumPoints/sumWeight) * (range.second-range.first);
        int base = range.first;

        Log.d("BRAININDEX", "maxsucclevel: " + maxSuccLevel + "base:" + base + " timepts:" + timePoints);
        return (int)(base + timePoints);
    }


    private Pair<Integer,Integer> indexRange(int maxSuccLevel) {
        switch (maxSuccLevel) {
            case 0: return new Pair(0, 10);
            case 1: return new Pair(0, 80);
            case 2: return new Pair(80, 160);
            case 3: return new Pair(160, 240);
            case 4: return new Pair(240, 320);
            case 5: return new Pair(320, 380);
            case 6: return new Pair(380, 435);   // 55
            case 7: return new Pair(435, 485);   // 50
            case 8: return new Pair(485, 530);
            case 9: return new Pair(530, 570);
            case 10: return new Pair(570,605);
            case 11: return new Pair(605,635);
            case 12: return new Pair(635,665);
            case 13: return new Pair(665,695);
            case 15: return new Pair(695,725);
            case 16: return new Pair(725,765);
            case 17: return new Pair(765,805);
            case 18: return new Pair(805,845);
            case 19: return new Pair(845,905);
            case 20: return new Pair(905,1000);
            default: return indexRange(20);
        }

    }

    public int guesslevelFromIndex(int index) {
        for(int i=0; i<=Level.MAX_LEVEL_NUM; i++) {
            Pair<Integer, Integer> range = indexRange(i);
            if (index>=range.first && index<range.second) return i;
        }
        return 0;
    }


    private SharedPreferences preferences() {
        return context.getSharedPreferences("pl.tlasica.smatch", Context.MODE_PRIVATE);
    }
}
