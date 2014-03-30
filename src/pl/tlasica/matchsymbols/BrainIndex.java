package pl.tlasica.matchsymbols;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;

/**
 * Created by tomek on 28.03.14.
 */
public class BrainIndex {

    private static final int MAX_INDEX = 1000;
    private String      KEY = "BrainIndex";

    private Context context;

    public BrainIndex(Context ctx) {
        context = ctx;
    }

    public int maxIndex() {
        return MAX_INDEX;
    }

    public int current() {
        int r = preferences().getInt(KEY, 0);
        return r;
    }

    public int change(int newIndex) {
        return newIndex - current();
    }

    public void store(int newIndex) {
        SharedPreferences.Editor editor = preferences().edit();
        editor.putInt(KEY, newIndex);
        editor.commit();
    }

    public int calculate(List<GameResult> history) {
        double max = 0;
        double sum = 0;
        int maxLevelNum = 0;
        for(GameResult r: history) {
            Level l = Level.create(r.level);
            PointsCalculator calc = new PointsCalculator(l);
            max += calc.maxPointsPossible();
            if (r.success) {
                maxLevelNum = r.level;
                long score = calc.getPoints( r );
                sum += score;
            }
        }
        if (max>0) {
            Log.d("BRAININDEX", "max score:" + max + " act score:" + sum + " maxLevel:" + maxLevelNum);
            double maxIndex = 976.0 * ((float)maxLevelNum / Level.MAX_LEVEL_NUM);
            int index = (int)((sum/max) * maxIndex);
            Log.d("BRAININDEX", "maxIndex:" + maxIndex + " index:" + index);
            return index;
        }
        else return 0;
    }

    private SharedPreferences preferences() {
        return context.getSharedPreferences("pl.tlasica.matchsymbols", Context.MODE_PRIVATE);
    }
}
