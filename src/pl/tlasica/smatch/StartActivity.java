package pl.tlasica.smatch;

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
import com.heyzap.sdk.ads.HeyzapAds;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmActivity;
import com.swarmconnect.SwarmLeaderboard;

//TODO add best personal results history and information
//TODO start level should be a function of #points
//TODO add some nice background
//TODO add "share on FB" button
//TODO add some comparison with people around the world?

public class StartActivity extends SwarmActivity {

    private static final int REQ_CODE = 111;
    PersonalBest        personalBest;
    TextView            mTextBrainIndex;
    Settings            settings;
    ToggleButton        mToggleSoundButton;
    BrainIndex          brainIndex;

    final int   SWARM_LEADERBOARD_ID = 15332;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize HeyzApp
        HeyzapAds.start(this);

        // Initialize Swarm
        int SWARM_APP_ID = 10560;
        String SWARM_APP_KEY = "ca4f8e194f72f503054e3a7381e0a557";

        Swarm.init(this, SWARM_APP_ID, SWARM_APP_KEY);
        Swarm.setAllowGuests(true);

        FontManager.init(getApplication());
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        // set up fonts
        FontManager.setTitleFont((Button) findViewById(R.id.buttonShare), Typeface.NORMAL);
        FontManager.setTitleFont((Button) findViewById(R.id.buttonInstruction), Typeface.NORMAL);
        FontManager.setTitleFont((Button) findViewById(R.id.buttonStart), Typeface.NORMAL);
        FontManager.setTitleFont((Button) findViewById(R.id.buttonLeaderboard), Typeface.NORMAL);

        FontManager.setTitleFont((TextView) findViewById(R.id.tv_start_apptitle), Typeface.NORMAL);
        FontManager.setMainFont( (TextView)findViewById(R.id.tv_start_subtitle), Typeface.NORMAL);
        FontManager.setMainFont( (TextView)findViewById(R.id.tv_start_brain_index), Typeface.NORMAL);

        //FontManager.setSmartSize((TextView) findViewById(R.id.tv_start_apptitle), 24);
        //FontManager.setSmartSize((TextView) findViewById(R.id.tv_start_subtitle), 16);
        //FontManager.setSmartSize((TextView) findViewById(R.id.tv_start_brain_index), 14);


        brainIndex = new BrainIndex(getApplicationContext());
        mTextBrainIndex = (TextView) findViewById(R.id.tv_start_brain_index);
        personalBest = new PersonalBest(getApplicationContext());
        updateBrainIndex();

        mToggleSoundButton = (ToggleButton) findViewById(R.id.toggleSoundButton);
        settings = new Settings(getApplicationContext());
        mToggleSoundButton.setChecked(settings.sound());
    }

    private void updateBrainIndex() {
        int index = brainIndex.currentIndex();
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

    public void showLeaderboard(View view) {
        SwarmLeaderboard.showLeaderboard(SWARM_LEADERBOARD_ID);
    }

    public void share(View view) {
        Log.d("START", "share()");
        // building message
        int index = brainIndex.currentIndex();
        String msg = String.format(getString(R.string.share_msg), index);
        // prepare intent
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_SUBJECT, "S*Match Brain Index");
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(intent, getString(R.string.share_how)));
    }

    private int startLevelForBrainIndex() {
        int maxSuccLevel = brainIndex.guesslevelFromIndex( brainIndex.currentIndex() );
        int startLevel = maxSuccLevel - 6;
        return (startLevel > 0) ? startLevel : 1;
    }

    public void toggleSound(View view) {
        boolean sound = settings.switchSound();
        mToggleSoundButton.setChecked(sound);
        Log.d("START", "toggleSound(), sound will be:" + sound);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE) {

            if(resultCode == RESULT_OK){
                long points = data.getLongExtra("POINTS", 0);
                if (personalBest.isNewBest(points)) {
                    personalBest.storeBest(points);
                }
                int brIndex = data.getIntExtra("BRAIN_INDEX", 0);
                if (brIndex > brainIndex.currentIndex()) {
                    Log.d("HURRA","hurra");
                    brainIndex.storeIndex(brIndex);
                    updateBrainIndex();
                    SwarmLeaderboard.submitScoreAndShowLeaderboard(SWARM_LEADERBOARD_ID, brIndex);
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult
}
