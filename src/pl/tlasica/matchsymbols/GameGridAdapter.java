package pl.tlasica.matchsymbols;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

/**
 * Created by tomek on 22.03.14.
 */
public class GameGridAdapter extends BaseAdapter {


    private Context         mContext;
    private Game            game;
    private GameController  controller;

    private int[] colors = {Color.WHITE, Color.CYAN, Color.YELLOW, Color.BLUE, Color.GREEN};


    public GameGridAdapter(Context c, GameController gc) {
        mContext = c;
        game = gc.game();
        controller = gc;
    }

    public int getCount() {
        return game.numCols * game.numRows;
    }

    public Object getItem(int position) {
        return  null; //TODO
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Button button;
        if (convertView == null) {
            button = new Button(mContext);
            FontManager.setTypeface(button, Typeface.BOLD);
            button.setBackgroundResource(R.drawable.button);
            button.setTextColor(Color.WHITE);
        }
        else button = (Button)convertView;

        Game.Cell cell = game.cell(position);

        String text = String.valueOf(cell.symbol);
        button.setText(text);
        button.setTextColor(textColor(cell.colorIndex));
        button.setId(position);
        button.setOnClickListener(new MyOnClickListener(controller, position));

        return button;

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
            //v.setBackgroundResource(R.drawable.button_gray_pressed);
        }
    }


}
