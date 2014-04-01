package pl.tlasica.smatch;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tomek on 23.03.14.
 */
public class PersonalBest {

    private long        result;
    private Context     context;
    private String      KEY = "PersonalBest";


    public PersonalBest(Context context) {
        this.context = context;
    }

    public long retrieve() {
        long r = preferences().getLong(KEY, 0);
        if (r>result) result = r;
        return result;
    }

    public boolean isNewBest(long v) {
        long curr = retrieve();
        return (v>curr);
    }

    public boolean storeBest(long v) {
        if (isNewBest(v)) {
            SharedPreferences.Editor editor = preferences().edit();
            result = v;
            editor.putLong(KEY, result);
            editor.commit();
            return true;
        }
        else return false;
    }

    private SharedPreferences preferences() {
        return context.getSharedPreferences("pl.tlasica.smatch", Context.MODE_PRIVATE);
    }
}
