package pl.tlasica.matchsymbols;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

//TODO add best personal results history and information
//TODO start level should be a function of #points
//TODO add some nice background
//TODO add "share on FB" button
//TODO add some comparison with people around the world?

public class StartActivity extends Activity {

    private static final int REQ_CODE = 111;
    PersonalBest        personalBest;
    TextView            mTextPersonalBest;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FontManager.init(getApplication());
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);

        // set up fonts
        Button btnStart = (Button) findViewById(R.id.buttonStart);
        FontManager.setMainFont(btnStart, Typeface.NORMAL);

        TextView tv = (TextView) findViewById(R.id.tv_start_apptitle);
        FontManager.setMainFont(tv, Typeface.NORMAL);

        tv = (TextView) findViewById(R.id.tv_start_best_label);
        FontManager.setMainFont(tv, Typeface.NORMAL);

        mTextPersonalBest = (TextView) findViewById(R.id.tv_start_best_points);
        FontManager.setMainFont(mTextPersonalBest, Typeface.NORMAL);

        personalBest = new PersonalBest(getApplicationContext());

        updatePersonalBest();
    }

    private void updatePersonalBest() {
        long best = personalBest.retrieve();
        mTextPersonalBest.setText(String.valueOf(best));
    }

    public void startGame(View view) {
        Log.d("START", "startGame()");

        // start game activity
        Intent intent = new Intent(this, GameActivity.class);
        startActivityForResult(intent, REQ_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE) {

            if(resultCode == RESULT_OK){
                long points = data.getLongExtra("POINTS", 0);
                if (personalBest.isNewBest(points)) {
                    personalBest.storeBest(points);
                    updatePersonalBest();
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult
}
