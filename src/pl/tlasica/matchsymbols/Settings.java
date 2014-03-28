package pl.tlasica.matchsymbols;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tomek on 28.03.14.
 */
public class Settings {

    private Context context;

    public Settings(Context context) {
        this.context = context;
    }

    public boolean sound() {
        return preferences().getBoolean("SOUND", true);
    }

    public boolean switchSound() {
        boolean curr = sound();
        boolean sound = !curr;
        SharedPreferences.Editor editor = preferences().edit();
        editor.putBoolean("SOUND", sound);
        editor.commit();
        return sound;
    }

    private SharedPreferences preferences() {
        return context.getSharedPreferences("pl.tlasica.matchsymbols", Context.MODE_PRIVATE);
    }

}
