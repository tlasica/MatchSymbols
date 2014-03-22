package pl.tlasica.matchsymbols;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by tomek on 22.03.14.
 */
public class GameActivity extends Activity implements Observer {

    private final int NUM_COLORS = 5;
    private final int NUM_ROUNDS = 10;

    GridView        mSymbolsGrid;
    Game            game;
    GameController  gameController;
    GameGridAdapter adapter;
    long            roundStartTimeMs;
    int             level = 1;
    List<GameResult>   history = new ArrayList<GameResult>();
    int             levelSuccesses = 0;
    int             numRounds = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // from http://zrgiu.com/blog/2011/01/making-your-android-app-look-better/
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        setContentView(R.layout.game_activity);

        // create grid view for symbols
        mSymbolsGrid = (GridView) findViewById(R.id.symbolsGrid);

        newGame(level);
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
        // tick time
        roundStartTimeMs = System.currentTimeMillis();
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
            mSymbolsGrid.setBackgroundColor(success ? Color.GREEN : Color.RED);
            // close activity after 300ms
            CountDownTimer timer = new CountDownTimer(300, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    mSymbolsGrid.setBackgroundColor(0);
                    if (numRounds < NUM_ROUNDS) newGame(level);
                    else {
                        showResults();
                    }
                }
            }.start();

        }
    }

    private void showResults() {
        long points = new PointsCalculator().getPoints(history);
        String msg = "You earned " + points + " points.\nCongratulations!";
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Game result")
                .setMessage(msg)
                .setNeutralButton("Close", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GameActivity.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void calculateNewLevel(boolean success) {
        if (success) {
            levelSuccesses++;
            if (nextLevel()) {
                ++level;
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

    private boolean nextLevel() {
        if (level==1 && levelSuccesses==2) return true;
        if (level==2 && levelSuccesses==3) return true;
        if (level==3 && levelSuccesses==3) return true;
        if (level==4 && levelSuccesses==4) return true;
        if (level==5 && levelSuccesses==4) return true;
        if (level==6 && levelSuccesses==4) return true;
        if (level==7 && levelSuccesses==4) return true;
        if (level==8 && levelSuccesses==4) return true;
        return false;
    }
}