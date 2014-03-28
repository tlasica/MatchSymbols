package pl.tlasica.matchsymbols;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ToggleButton;

//TODO add best personal results history and information
//TODO start level should be a function of #points
//TODO add some nice background
//TODO add "share on FB" button
//TODO add some comparison with people around the world?

public class StartActivity extends Activity {

    private static final int REQ_CODE = 111;
    PersonalBest        personalBest;
    TextView            mTextBrainIndex;
    Settings            settings;
    ToggleButton        mToggleSoundButton;
    BrainIndex          brainIndex;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        // set up fonts
        FontManager.setTitleFont((Button) findViewById(R.id.buttonAchievements), Typeface.NORMAL);
        FontManager.setTitleFont((Button) findViewById(R.id.buttonInstruction), Typeface.NORMAL);
        FontManager.setTitleFont((Button) findViewById(R.id.buttonStart), Typeface.NORMAL);

        FontManager.setTitleFont((TextView) findViewById(R.id.tv_start_apptitle), Typeface.NORMAL);
        FontManager.setMainFont( (TextView)findViewById(R.id.tv_start_subtitle), Typeface.NORMAL);
        FontManager.setMainFont( (TextView)findViewById(R.id.tv_start_brain_index), Typeface.NORMAL);

        brainIndex = new BrainIndex(getApplicationContext());
        mTextBrainIndex = (TextView) findViewById(R.id.tv_start_brain_index);
        personalBest = new PersonalBest(getApplicationContext());
        updateBrainIndex();

        mToggleSoundButton = (ToggleButton) findViewById(R.id.toggleSoundButton);
        settings = new Settings(getApplicationContext());
        mToggleSoundButton.setChecked(settings.sound());
    }

    private void updateBrainIndex() {
        int index = brainIndex.current();
        String msg = getString(R.string.label_brain_index) + (index>0?index:"?") + "/" + brainIndex.maxIndex();
        mTextBrainIndex.setText(msg);
    }

    public void startGame(View view) {
        Log.d("START", "startGame()");
        // calculate next level
        int level = startLevelForBrainIndex();
        // start game activity
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("LEVEL", level);
        startActivityForResult(intent, REQ_CODE);
    }

    public void showInstruction(View view) {
        String help = getString(R.string.help);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Game Instructions")
                .setMessage(help)
                .setNeutralButton("Close", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private int startLevelForBrainIndex() {
        float r = (float)brainIndex.current() / (float)brainIndex.maxIndex();
        int level = (int)(r * 10.0) - 3;
        return (level>0) ? level : 1;

    }

    public void toggleSound(View view) {
        boolean sound = settings.switchSound();
        mToggleSoundButton.setChecked(sound);
        Log.d("START", "toggleSound(), sound will be:" + sound);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE) {

            if(resultCode == RESULT_OK){
                updateBrainIndex();
                long points = data.getLongExtra("POINTS", 0);
                if (personalBest.isNewBest(points)) {
                    personalBest.storeBest(points);
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult
}
