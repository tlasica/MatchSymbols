package pl.tlasica.matchsymbols;

import android.app.Application;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by tomek on 22.03.14.
 */
public class FontManager {

    private static Typeface fontNOZSTUDIO;
    private static Typeface fontLuckiestGuy;
    private static Application app;

    public static void init(Application a) {
        app = a;
        getNOZSTUDIOFont();
    }

    //TODO: change for better font
    public static void setSymbolsFont(Button button, int style) {
        if (button!=null) {
            button.setTypeface( getLuckiestGuyFont(), style );
        }

    }

    public static void setMainFont(Button button, int style) {
        if (button!=null) {
            button.setTypeface( getNOZSTUDIOFont(), style );
        }

    }

    public static void setMainFont(TextView view, int style) {
        view.setTypeface( getNOZSTUDIOFont(), style );
    }

    private static Typeface getNOZSTUDIOFont() {
        if (fontNOZSTUDIO==null) {
            fontNOZSTUDIO = Typeface.createFromAsset(app.getAssets(),"fonts/nozstudio.ttf");
        }
        return fontNOZSTUDIO;
    }

    private static Typeface getLuckiestGuyFont() {
        if (fontLuckiestGuy==null) {
            fontLuckiestGuy = Typeface.createFromAsset(app.getAssets(),"fonts/LuckiestGuy.ttf");
        }
        return fontLuckiestGuy;
    }

}
