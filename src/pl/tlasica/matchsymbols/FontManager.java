package pl.tlasica.matchsymbols;

import android.app.Application;
import android.graphics.Typeface;
import android.widget.Button;

/**
 * Created by tomek on 22.03.14.
 */
public class FontManager {

    private static Typeface fontNOZSTUDIO;
    private static Application app;

    public static void init(Application a) {
        app = a;
        getNOZSTUDIOFont();
    }

    public static void setTypeface(Button button, int style) {
        if (button!=null) {
            button.setTypeface( getNOZSTUDIOFont(), style );
        }

    }

    private static Typeface getNOZSTUDIOFont() {
        if (fontNOZSTUDIO==null) {
            fontNOZSTUDIO = Typeface.createFromAsset(app.getAssets(),"fonts/nozstudio.ttf");
        }
        return fontNOZSTUDIO;
    }
}
