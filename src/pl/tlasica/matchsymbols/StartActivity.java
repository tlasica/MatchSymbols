package pl.tlasica.matchsymbols;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FontManager.init(getApplication());
        setContentView(R.layout.main);

        Button btnStart = (Button) findViewById(R.id.buttonStart);
        FontManager.setTypeface(btnStart, Typeface.NORMAL);
    }

    public void startGame(View view) {
        Log.d("START", "startGame()");

        // start game activity
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
