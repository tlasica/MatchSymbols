package pl.tlasica.smatch;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by tomek on 22.03.14.
 */
public class FontManager {

    private static Typeface fontTitle;
    private static Typeface fontSymbols;
    private static Typeface fontNormal;
    private static Application app;

    public static void init(Application a) {
        app = a;
        getTitleFont();
    }

    public static int getFontSize (Activity activity, int size) {
        DisplayMetrics dMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dMetrics);

        // lets try to get them back a font size realtive to the pixel width of the screen
        final float WIDE = activity.getResources().getDisplayMetrics().widthPixels;
        int valueWide = (int)(WIDE / (float)size / (dMetrics.scaledDensity));
        return valueWide;
    }

    public static void setSmartSize(TextView view, int size) {
        view.setTextSize(size * app.getResources().getDisplayMetrics().density);
    }

    public static void setSymbolsFont(TextView view, int style) {
        if (view!=null) {
            view.setTypeface( getSymbolsFont(), style );
        }
    }

    public static void setSymbolsFont(Button button, int style) {
        if (button!=null) {
            button.setTypeface( getSymbolsFont(), style );
        }

    }

    public static void setMainFont(Button button, int style) {
        if (button!=null) {
            button.setTypeface( getNormalFont(), style );
        }

    }

    public static void setMainFont(TextView view, int style) {
        view.setTypeface( getNormalFont(), style );
    }

    public static void setButtonFont(Button button, int style) {
        if (button!=null) {
            button.setTypeface( getSymbolsFont(), style );
        }
    }

    public static void setTitleFont(TextView view, int style) {
        view.setTypeface( getSymbolsFont(), style );
    }

    private static Typeface getNormalFont() {
        if (fontNormal==null) {
            fontNormal = Typeface.createFromAsset(app.getAssets(),"fonts/Roboto-Regular.ttf");
        }
        return fontNormal;
    }

    private static Typeface getTitleFont() {
        if (fontTitle ==null) {
            fontTitle = Typeface.createFromAsset(app.getAssets(),"fonts/Android.ttf");
        }
        return fontTitle;
    }

    private static Typeface getSymbolsFont() {
        if (fontSymbols ==null) {
            fontSymbols = Typeface.createFromAsset(app.getAssets(),"fonts/junegull.ttf");
        }
        return fontSymbols;
    }

}
