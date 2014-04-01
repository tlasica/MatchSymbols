package pl.tlasica.smatch;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tomek on 23.03.14.
 * Class responsible for player achievements like best score, brain power and badges
 */
public class Player {

    private long        bestScore;
    private int         brainPowerIndex;    // 0..1000
    private Context     context;
    private String      KEY = "PersonalBest";


    private Player(Context context) {
        this.context = context;
        retrieve();
    }

    private void retrieve() {
        bestScore = preferences().getLong("BEST_SCORE", 0);
        brainPowerIndex = preferences().getInt("BRAIN_POWER_INDEX", 0);
    }


    private SharedPreferences preferences() {
        return context.getSharedPreferences("pl.tlasica.matchsymbols", Context.MODE_PRIVATE);
    }

    private static Player instance = null;

    public Player getInstance(Context context) {
        if (instance == null) {
            instance = new Player(context);
        }
        return instance;
    }
}
