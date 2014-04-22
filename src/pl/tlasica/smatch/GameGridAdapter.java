package pl.tlasica.smatch;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

/**
 * Created by tomek on 22.03.14.
 */
public class GameGridAdapter extends BaseAdapter {


    private Context         mContext;
    private Game            game;
    private GameController  controller;
    private Button[]        items;
    private int[]           solution;

    private int[] colors = {
            Color.parseColor("#0099CC"),        // blue
            Color.parseColor("#9933CC"),        // violet
            Color.parseColor("#669900"),        // green
            Color.parseColor("#FF8800"),        // orange
            Color.parseColor("#CC0000") };      // red


    public GameGridAdapter(Context c, GameController gc) {
        mContext = c;
        game = gc.game();
        controller = gc;
        items = new Button[game.numCols*game.numRows];
    }

    public int getCount() {
        return game.numCols * game.numRows;
    }

    public Object getItem(int position) {
        return items[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Button button;
        if (convertView == null) {
            button = new Button(mContext);
            FontManager.setSymbolsFont(button, Typeface.BOLD);
            button.setBackgroundResource(R.drawable.button);
            button.setTextColor(Color.WHITE);
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
            button.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, GridView.AUTO_FIT));
            items[position] = button;
            Log.d("getView()", "pos:"+position);
        }
        else button = (Button)convertView;

        Game.Cell cell = game.cell(position);

        String text = String.valueOf(cell.symbol);
        button.setText(text);
        button.setTextColor(textColor(cell.colorIndex));
        button.setId(position);
        button.setOnClickListener(new MyOnClickListener(controller, position));

        // if solution....
        if (solution != null) {
            if (position==solution[0] || position==solution[1]) {
                button.setBackgroundResource(R.drawable.button_selected);
            }
            else button.setBackgroundResource(R.drawable.button);
        }

        return button;

    }

    public void setSolution(int[] sol) {
        solution = sol;
    }

    private int textColor(int colorIndex) {
        if (colorIndex < colors.length) return colors[colorIndex];
        else return Color.WHITE;
    }

    private class MyOnClickListener implements View.OnClickListener {
        private final int position;
        private final GameController gameController;

        public MyOnClickListener(GameController gc, int p) {
            gameController = gc;
            position = p;
        }

        public void onClick(View v) {
            Log.d("BUTTON CLICKED", "position:" + position);
            if (gameController.isSelected(position)) {
                gameController.unselect(position);
                v.setBackgroundResource(R.drawable.button);
            }
            else {
                boolean selected = gameController.select(position);
                if (selected) v.setBackgroundResource(R.drawable.button_selected);
            }
        }
    }


}
