package pl.tlasica.matchsymbols;

import android.app.Application;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by tomek on 22.03.14.
 */
public class FontManager {

    private static Typeface fontNOZSTUDIO;
    private static Typeface fontLuckiestGuy;
    private static Typeface fontRocko;
    private static Application app;

    public static void init(Application a) {
        app = a;
        getNOZSTUDIOFont();
    }

    public static void setSmartSize(TextView view, int size) {
        view.setTextSize(size * app.getResources().getDisplayMetrics().density);
    }

    //TODO: change for better font
    public static void setSymbolsFont(Button button, int style) {
        if (button!=null) {
            button.setTypeface( getLuckiestGuyFont(), style );
        }

    }

    public static void setMainFont(Button button, int style) {
        if (button!=null) {
            button.setTypeface( getRockoFont(), style );
        }

    }

    public static void setMainFont(TextView view, int style) {
        view.setTypeface( getRockoFont(), style );
    }

    public static void setTitleFont(Button button, int style) {
        if (button!=null) {
            button.setTypeface( getNOZSTUDIOFont(), style );
        }

    }

    public static void setTitleFont(TextView view, int style) {
        view.setTypeface( getNOZSTUDIOFont(), style );
    }

    private static Typeface getRockoFont() {
        if (fontRocko==null) {
            fontRocko = Typeface.createFromAsset(app.getAssets(),"fonts/RockoFLF.ttf");
        }
        return fontRocko;
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
