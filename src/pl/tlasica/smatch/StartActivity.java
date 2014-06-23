package pl.tlasica.smatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.facebook.*;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.heyzap.sdk.ads.HeyzapAds;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmActivity;
import com.swarmconnect.SwarmLeaderboard;

import java.util.List;

//TODO add best personal results history and information
//TODO start level should be a function of #points
//TODO add some nice background
//TODO add "share on FB" button
//TODO add some comparison with people around the world?

/*
var title = 'My Title';
var summary = 'This is my summary';
var url = 'http://www.mydomain.com/path/to/page';
var image = 'http://www.mydomain.com/images/myimage.png';

var fb = window.open('http://www.facebook.com/sharer.php?s=100&p[title]='+encodeURIComponent(title)+'&p[url]='+encodeURIComponent(url)+'&p[summary]='+encodeURIComponent(summary)+'&p[images][0]='+encodeURIComponent(image));
fb.focus();
*/

public class StartActivity extends SwarmActivity {

    private static final int REQ_CODE = 111;
    PersonalBest        personalBest;
    TextView            mTextBrainIndex;
    Settings            settings;
    ToggleButton        mToggleSoundButton;
    BrainIndex          brainIndex;

    final int       SWARM_LEADERBOARD_ID = 15332;
    final int       SWARM_APP_ID = 10560;
    final String    SWARM_APP_KEY = "ca4f8e194f72f503054e3a7381e0a557";

    final String    APP_URL = "http://bit.ly/1ibDjjJ";
    final String    PIC_URL = "https://raw.githubusercontent.com/tlasica/MatchSymbols/master/icon_96.png";

    private UiLifecycleHelper uiHelper;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize HeyzApp
        HeyzapAds.start(this);

        // Initialize Swarm if enabled
        if (Swarm.isEnabled()) initSwarm();

        FontManager.init(getApplication());
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        // set up fonts
        FontManager.setTitleFont((Button) findViewById(R.id.buttonInstruction), Typeface.NORMAL);
        FontManager.setTitleFont((Button) findViewById(R.id.buttonContinue), Typeface.NORMAL);
        FontManager.setTitleFont((Button) findViewById(R.id.buttonNewGame), Typeface.NORMAL);
        FontManager.setTitleFont((Button) findViewById(R.id.buttonLeaderboard), Typeface.NORMAL);
        FontManager.setTitleFont((Button) findViewById(R.id.buttonSwarm), Typeface.NORMAL);

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

        // configure facebook
        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);

        AppRater rater = new AppRater(this);
        rater.appLaunched();
    }


    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
        updateSwarmUI();
        // show/hide continue button
        Button btn = (Button) findViewById(R.id.buttonContinue);
        int nextLevel = startLevelForBrainIndex();
        if (nextLevel>1) {
            btn.setVisibility(View.VISIBLE);
            String text = String.format(getString(R.string.button_continue), nextLevel);
            btn.setText(text);
        }
        else {
            btn.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    private void updateSwarmUI() {
        Button btnInit = (Button) findViewById(R.id.buttonSwarm);
        Button btnBoard = (Button) findViewById(R.id.buttonLeaderboard);
        // init button hidden if swat enabled
        // TODO: na razie go całkowicie ukrywamy, bo jest przy komicie wynikow
        btnInit.setVisibility(View.GONE);
        //if (Swarm.isEnabled()) btnInit.setVisibility(View.GONE);
        //else btnInit.setVisibility(View.VISIBLE);
        // leaderboard allowed if swarm initialized and enabled
        boolean swarmEnabled = Swarm.isEnabled();
        boolean swarmInitialized = Swarm.isInitialized();
        if (swarmEnabled)
            btnBoard.setVisibility(View.VISIBLE);
        else
            btnBoard.setVisibility(View.GONE);
    }

    private void updateBrainIndex() {
        int index = brainIndex.currentIndex();
        String msg = getString(R.string.label_brain_index) + " " + (index>0?index:"?") + "/" + brainIndex.maxIndex();
        mTextBrainIndex.setText(msg);
    }

    private void initSwarm() {
        Log.d("SWARM", "initSwarm()");
        Swarm.init(this, SWARM_APP_ID, SWARM_APP_KEY);
        Swarm.setAllowGuests(true);
    }

    public void onInitSwarm(View view) {
        initSwarm();
    }

    public void startNewGame(View view) {
        Log.d("START", "startGame()");
        // calculate next level
        int level = 1;
        // start game activity
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("LEVEL", level);
        startActivityForResult(intent, REQ_CODE);
    }

    public void continueGame(View view) {
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

    public void facebookLoginAndShare(View view) {
        if (Session.getActiveSession() != null && Session.getActiveSession().isOpened()) {
            facebookShareWithFeedDialog();
        }
        else {
            Session.openActiveSession(this, true, new Session.StatusCallback() {

                // callback when session changes state
                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    if (state == SessionState.OPENED) {
                        facebookShareWithFeedDialog();
                    }

                }
            });
        }

    }

    private void facebookShareWithFeedDialog() {
        int index = brainIndex.currentIndex();
        String msg = String.format(getString(R.string.share_msg), index);

        Bundle params = new Bundle();
        params.putString("caption", "Train your brain and think faster with S*MATCH");
        params.putString("description", "This simple but addictive android game is a perfect training to make you think, remember and recognize pictures faster");
        params.putString("link", APP_URL);
        params.putString("picture", PIC_URL);
        params.putString("name", msg);

        WebDialog feedDialog = (
                new WebDialog.FeedDialogBuilder(this, Session.getActiveSession(), params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error == null) {
                            final String postId = values.getString("post_id");
                            if (postId != null) Log.d("FB", "posted");
                        } else if (error instanceof FacebookOperationCanceledException) {
                            Log.i("FB", "Publish cancelled");
                        } else {
                            Log.w("FB", "Error posting story");
                        }
                    }

                })
                .build();
        feedDialog.show();
    }


    public void share(View view) {

        Log.d("START", "share()");
        // building message
        int index = brainIndex.currentIndex();
        String msg = String.format(getString(R.string.share_msg), index);
        String url = APP_URL;
        msg = msg + " " + url;
        // prepare intent
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_SUBJECT, "S*Match Brain Index");
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(intent, getString(R.string.share_how)));
    }

    /*
    public void shareOnFacebookWithSharerUrl(View view) {
        // building message
        int index = brainIndex.currentIndex();
        String msg = String.format(getString(R.string.share_msg), index);
        String url = "http://bit.ly/1ibDjjJ";
        // show
        Uri uri = Uri.parse("http://m.facebook.com/sharer.php?u=APP_URL&p[title]="+msg+"&p[images][0]="+PIC_URL+"&t=dupa");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    */

    private int startLevelForBrainIndex() {
        int maxSuccLevel = brainIndex.guesslevelFromIndex( brainIndex.currentIndex() );
        int startLevel = maxSuccLevel - 4;
        return (startLevel > 0) ? startLevel : 1;
    }

    public void toggleSound(View view) {
        boolean sound = settings.switchSound();
        mToggleSoundButton.setChecked(sound);
        Log.d("START", "toggleSound(), sound will be:" + sound);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
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
                    //TODO: można tutaj sprawdzać if (Swarm.isEnabled()) ale tak jest ok.
                    if (!Swarm.isEnabled()) initSwarm();
                    SwarmLeaderboard.submitScoreAndShowLeaderboard(SWARM_LEADERBOARD_ID, brIndex);
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        else {
            uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
                @Override
                public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                    Log.e("Activity", String.format("Error: %s", error.toString()));
                }

                @Override
                public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                    Log.i("Activity", "Success!");
                }
            });
        }
    }//onActivityResult
}
