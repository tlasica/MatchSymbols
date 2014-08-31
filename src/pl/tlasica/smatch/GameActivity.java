package pl.tlasica.smatch;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.swarmconnect.SwarmActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

//TODO description with nice font and large font
//TODO big button EXIT (niebieski lub czerwony)
//TODO oddziele activity z wynikiem i przyciskiem play / menu / share
//TODO reklama wyskakująca po zakończeniu / przed wynikami?
//TODO przerobić na symbolId i renderer (można by wówczas mieć np. rysunki)

/**
 * Created by tomek on 22.03.14.
 */
public class GameActivity extends SwarmActivity implements Observer {

    private final int NUM_COLORS = 5;

    private final int COLOR_RED = Color.parseColor("#FF4444");
    private final int COLOR_GREEN = Color.parseColor("#99CC00");

    GridView        mSymbolsGrid;
    TextView        mGoalText;
    TextView        mTimeLeftText;
    TextView        mCurrLevelText;
    Button          buttonGameOver;
    TextView        mResultText;
    TextView        mGameOverText;

    Game            game;
    GameController  gameController;
    GameGridAdapter adapter;
    long            roundStartTimeMs;
    Level           level;
    List<GameResult>   history = new ArrayList<GameResult>();
    int             levelSuccesses = 0;
    int             numRounds = 0;
    SoundPoolPlayer sounds;

    private boolean isFinished = false;

    PointsCalculator    pointsCalc;
    CountDownTimer  roundTimer = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // from http://zrgiu.com/blog/2011/01/making-your-android-app-look-better/
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.game_activity);

        // create grid view for symbols
        mSymbolsGrid = (GridView) findViewById(R.id.symbolsGrid);
        mSymbolsGrid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

        mGoalText = (TextView) findViewById(R.id.tv_game_goal);
        FontManager.setMainFont(mGoalText, Typeface.NORMAL);

        mTimeLeftText = (TextView) findViewById(R.id.tv_game_time_left);
        FontManager.setMainFont(mTimeLeftText, Typeface.NORMAL);

        mCurrLevelText = (TextView) findViewById(R.id.tv_game_level);
        FontManager.setMainFont(mCurrLevelText, Typeface.NORMAL);

        mGameOverText = (TextView) findViewById(R.id.tv_game_over);
        FontManager.setSymbolsFont(mResultText, Typeface.NORMAL);
        mGameOverText.setVisibility(View.GONE);

        buttonGameOver = (Button) findViewById(R.id.buttonGameOver);
        FontManager.setButtonFont(buttonGameOver, Typeface.NORMAL);
        buttonGameOver.setVisibility(View.GONE);

        mResultText = (TextView) findViewById(R.id.tv_game_result);
        FontManager.setMainFont(mResultText, Typeface.NORMAL);
        mResultText.setVisibility(View.GONE);

        sounds = new SoundPoolPlayer(this);

        int startLevel = getIntent().getIntExtra("LEVEL",1);
        Log.d("GAME", "start level:" + startLevel);
        newGame(startLevel);
    }

    @Override
    protected void onDestroy() {
        roundTimer.cancel();
        sounds.release();
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        if (!isFinished) super.onBackPressed();
        else onGameOver(null);
    }

    private void newGame(int aLevel) {
        numRounds++;
        // create game
        level = Level.create(aLevel);
        GameGenerator generator = GameGenerator.create(level.levelNum, NUM_COLORS);
        this.game = generator.generate();
        // and game controller
        this.gameController = new GameController(game);
        this.gameController.addObserver( this );
        pointsCalc = new PointsCalculator(level);
        // set grid columns
        mSymbolsGrid.setNumColumns(game.numCols());
        // set adapater
        adapter = new GameGridAdapter(this, this, gameController);
        mSymbolsGrid.setAdapter( adapter );
        // update level
        updateLevel();
        // update goal
        updateGoal();
        // tick time
        roundStartTimeMs = System.currentTimeMillis();
        roundTimer = startRoundTimer();
    }

    private void updateGoal() {
        String msg = String.format( getString(R.string.game_goal), game.numSame);
        mGoalText.setText(msg);
    }

    private CountDownTimer startRoundTimer() {
        long roundDuration = level.durationMaxMs;
        CountDownTimer timer = new CountDownTimer(roundDuration+100, 99) {
            @Override
            public void onTick(long millisUntilFinished) {
                //TODO: play some sound if time < 5% and <5 sekund
                // update round points
                long dur = System.currentTimeMillis() - roundStartTimeMs;
                long timeLeft = level.durationMaxMs - dur;
                int secLeft = (int)((timeLeft) / 1000.0);
                if (secLeft<0) secLeft = 0;
                String msg = String.format( getString(R.string.game_round_points), secLeft);
                mTimeLeftText.setText(msg);
            }

            @Override
            public void onFinish() {
                saveRoundInHistory(false);
                roundFailure();
            }
        }.start();
        return timer;
    }

    private void updateLevel() {
        String msg = getString(R.string.game_level) + level.levelNum;
        mCurrLevelText.setText( msg );
    }


    @Override
    public void update(Observable observable, Object data) {
        if (observable == gameController) {
            boolean success = gameController.isSuccess();
            if (success) {
                Log.d("GAME","game finished with success:"+success);
                // save in history
                saveRoundInHistory(success);
                // stop round timer
                roundTimer.cancel();
                roundSuccess();
            }
            else {  // clear invalid selection
                Log.d("GAME","invalid selection");
                // toast
                Toast.makeText(this, getString(R.string.msg_invalid_selection), Toast.LENGTH_SHORT);
                // play sound
                sounds.playNo();
            }
        }
    }

    private void roundFailure() {
        isFinished = true;
        // set background red
        // mSymbolsGrid.setBackgroundColor(COLOR_RED);
        // show game over
        mGameOverText.setVisibility(View.VISIBLE);
        // play sound
        sounds.vibrate();
        if (this.gameController.isFinished()) sounds.playNo();
        else sounds.playSnooring();
        // show game over and block playground
        mSymbolsGrid.setEnabled(false);
        buttonGameOver.setText("Close");
        buttonGameOver.setVisibility(View.VISIBLE);
        // calculate game results
        final long points = pointsCalc.getPoints(history);
        BrainIndex brainIndexCalc = new BrainIndex(getApplicationContext());
        final int idx = brainIndexCalc.calculate(history);
        int change = brainIndexCalc.change(idx);
        if (change<0) change = 0;
        // show game result
        String res = String.format(getString(R.string.game_result), idx, change);
        if (change>0) res += " Wow!";
        mResultText.setText(res);
        mResultText.setVisibility(View.VISIBLE);
        // mark solution
        int[] sol = game.solution();
        if (sol != null) {
            Log.d("SOLUTION", "x:" + sol[0] + "y:" + sol[1]);
            adapter.setSolution(sol);
            adapter.notifyDataSetChanged();
            mSymbolsGrid.invalidateViews();
        }
        else Toast.makeText(this, "Ups! Something went wrong! Unresolvable task?!#!", Toast.LENGTH_LONG);

        // TODO: mark valid solution
    }

    private void roundSuccess() {
        isFinished = true;
        // set background green
        mSymbolsGrid.setBackgroundColor(COLOR_GREEN);
        buttonGameOver.setText("Next Level");
        // play sound
        long dur = history.get(history.size()-1).durationMs;
        if (level.goldReward(dur)) {
            Log.d("GOLD REWARD", "level: "+level.levelNum);
            goldReward();
        }
        else if (level.silverReward(dur)) {
            Log.d("SILVER REWARD", "level: "+level.levelNum);
            silverReward();
        }
        // after 300ms start new round
        CountDownTimer timer = new CountDownTimer(300, 100) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                mSymbolsGrid.setBackgroundColor(Color.BLACK);
                startNextLevel();
            }
        }.start();

    }

    private void silverReward() {
        String msg = "Level " + level.levelNum + " SILVER reward!";
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(this, msg, duration).show();
        sounds.playYes();
    }

    private void goldReward() {
        String msg = "Level " + level.levelNum + " GOLD reward!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, msg, duration);
        toast.show();
        sounds.playYes();
    }

    private void startNextLevel() {
        int next = this.calculateNewLevel();
        newGame(next);
    }





    public void onGameOver(View view) {
        // calculare result (again)
        final long points = pointsCalc.getPoints(history);
        BrainIndex brainIndexCalc = new BrainIndex(getApplicationContext());
        final int idx = brainIndexCalc.calculate(history);
        int change = brainIndexCalc.change(idx);
        // finish activity
        Intent ret = new Intent();
        ret.putExtra("POINTS", points);
        ret.putExtra("BRAIN_INDEX", idx);
        GameActivity.this.setResult(RESULT_OK, ret);
        GameActivity.this.finish();
    }

    private int calculateNewLevel() {
        levelSuccesses++;
        if (nextLevelReady()) {
            levelSuccesses=0;
            int next = level.next();
            Log.d("NEXT", "next level:" + next);
            return next;
        }
        else {
            Log.d("NEXT", "level unchanged");
            return level.levelNum;
        }
    }

    private void saveRoundInHistory(boolean success) {
        GameResult h = new GameResult();
        h.durationMs = System.currentTimeMillis() - roundStartTimeMs;
        h.level =level.levelNum;
        h.success = success;
        history.add(h);
    }

    private boolean nextLevelReady() {
        return (levelSuccesses == 2);
    }

}