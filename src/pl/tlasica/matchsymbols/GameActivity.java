package pl.tlasica.matchsymbols;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

//TODO add playing some sounds after succ/failure
//TODO description with nice font and large font
//TODO big button EXIT (niebieski lub czerwony)
//TODO show points gained so far (large)
//TODO oddziele activity z wynikiem i przyciskiem play / menu / share
//TODO reklama wyskakująca po zakończeniu / przed wynikami?
//TODO lepsza ikonka (może taka 2x2)
//TODO przerobić na symbolId i renderer (można by wówczas mieć np. rysunki)
//TODO font powinien byc rozmiarowo lepszy albo i czcionka

/**
 * Created by tomek on 22.03.14.
 */
public class GameActivity extends Activity implements Observer {

    private final int NUM_COLORS = 5;
    private final int NUM_ROUNDS = 20;

    private final int COLOR_RED = Color.parseColor("#FF4444");
    private final int COLOR_GREEN = Color.parseColor("#99CC00");

    GridView        mSymbolsGrid;
    TextView        mPointsText;
    Game            game;
    GameController  gameController;
    GameGridAdapter adapter;
    long            roundStartTimeMs;
    int             level = 1;              // TODO: to be provided in the Intent
    List<GameResult>   history = new ArrayList<GameResult>();
    int             levelSuccesses = 0;
    int             numRounds = 0;
    SoundPoolPlayer sounds;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // from http://zrgiu.com/blog/2011/01/making-your-android-app-look-better/
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.game_activity);

        // create grid view for symbols
        mSymbolsGrid = (GridView) findViewById(R.id.symbolsGrid);
        mSymbolsGrid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

        // text view for points
        mPointsText = (TextView) findViewById(R.id.tv_game_points);
        FontManager.setMainFont(mPointsText, Typeface.NORMAL);

        sounds = new SoundPoolPlayer(this);

        newGame(level);
    }

    @Override
    protected void onDestroy() {
        sounds.release();
        super.onDestroy();
    }

    private void newGame(int level) {
        numRounds++;
        // create game
        GameGenerator generator = GameGenerator.create(level, NUM_COLORS);
        this.game = generator.generate();
        // and game controller
        this.gameController = new GameController(game);
        this.gameController.addObserver( this );
        // set grid columns
        mSymbolsGrid.setNumColumns(game.numCols());
        // set adapater
        adapter = new GameGridAdapter(this, gameController);
        mSymbolsGrid.setAdapter( adapter );
        // update points
        updatePoints();
        // tick time
        roundStartTimeMs = System.currentTimeMillis();
    }

    private void updatePoints() {
        long points = new PointsCalculator().getPoints(history);
        String msg = getString(R.string.game_points) + " " + points;
        mPointsText.setText( msg );
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable == gameController) {
            boolean success = gameController.isSuccess();
            Log.d("GAME","game finished with success:"+success);
            // save in history
            saveRoundInHistory(success);
            // play sound TODO
            // calculate new level
            calculateNewLevel(success);
            // set background green/red
            mSymbolsGrid.setBackgroundColor(success ? COLOR_GREEN : COLOR_RED);
            // play sound
            if (success) sounds.playYes(); else sounds.playNo();
            // close activity after 300ms
            CountDownTimer timer = new CountDownTimer(300, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    mSymbolsGrid.setBackgroundColor(Color.WHITE);
                    //mSymbolsGrid.setBackground(mSymbolsGridBackground);
                    if (numRounds < NUM_ROUNDS) newGame(level);
                    else {
                        showResults();
                    }
                }
            }.start();

        }
    }

    private void showResults() {
        final long points = new PointsCalculator().getPoints(history);
        String msg = "You earned " + points + " points.\nCongratulations!";
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Game result")
                .setMessage(msg)
                .setNeutralButton("Close", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent ret = new Intent();
                        ret.putExtra("POINTS", points);
                        GameActivity.this.setResult(RESULT_OK, ret);
                        GameActivity.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void calculateNewLevel(boolean success) {
        if (success) {
            levelSuccesses++;
            if (nextLevelReady()) {
                level = nextLevel(level);
                levelSuccesses=0;
            }
        }
    }

    private void saveRoundInHistory(boolean success) {
        GameResult h = new GameResult();
        h.durationMs = System.currentTimeMillis() - roundStartTimeMs;
        h.level = level;
        h.success = success;
        history.add(h);
    }

    private boolean nextLevelReady() {
        if (level==1 && levelSuccesses==2) return true;
        if (level==2 && levelSuccesses==2) return true;
        if (level==3 && levelSuccesses==3) return true;
        if (level==4 && levelSuccesses==3) return true;
        if (level==5 && levelSuccesses==3) return true;
        if (level==6 && levelSuccesses==3) return true;
        if (level==7 && levelSuccesses==3) return true;
        if (level==8 && levelSuccesses==3) return true;
        return false;
    }

    private int nextLevel(int level) {
        if (level<7) return ++level;
        else return 7;
    }
}