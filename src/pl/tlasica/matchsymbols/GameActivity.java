package pl.tlasica.matchsymbols;

import android.app.Activity;
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

import java.util.Observable;
import java.util.Observer;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by tomek on 22.03.14.
 */
public class GameActivity extends Activity implements Observer {

    GridView        mSymbolsGrid;
    Game            game;
    GameController  gameController;
    GameGridAdapter adapter;
    long            startTimeMs;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // from http://zrgiu.com/blog/2011/01/making-your-android-app-look-better/
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        setContentView(R.layout.game_activity);

        // create game
        GameGenerator generator = new GameGenerator(5, 4, 1, 3);
        game = generator.generate();
        Log.d("GAME", game.toString());

        // and game controller
        gameController = new GameController(game);
        gameController.addObserver( this );

        // create grid view for symbols
        mSymbolsGrid = (GridView) findViewById(R.id.symbolsGrid);
        mSymbolsGrid.setNumColumns(game.numCols());

        adapter = new GameGridAdapter(this, gameController);
        mSymbolsGrid.setAdapter( adapter );

        // add click action
        /*
        mSymbolsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.d("CLICK", "position:"+position);
                Toast.makeText(GameActivity.this, "" + position, LENGTH_SHORT).show();
            }
        });
        */

        // remember startTime
        startTimeMs = System.currentTimeMillis();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable == gameController) {
            boolean success = gameController.isSuccess();
            Log.d("GAME","game finished with success:"+success);
            // play sound TODO
            // set background green/red
            mSymbolsGrid.setBackgroundColor(success?Color.GREEN:Color.RED);
            // close activity after 300ms
            CountDownTimer timer = new CountDownTimer(300, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    GameActivity.this.finish();
                }
            }.start();

        }
    }
}