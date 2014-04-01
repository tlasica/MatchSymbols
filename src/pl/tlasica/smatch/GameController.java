package pl.tlasica.smatch;

import android.util.Log;

import java.util.Observable;

/**
 * Created by tomek on 22.03.14.
 */
public class GameController extends Observable {

    private Game game;
    int selected1 = -1;
    int selected2 = -1;

    public GameController(Game g) {
        game = g;
    }

    public Game game() {
        return game;
    }

    boolean isSelected(int pos) {
        return pos==selected1 || pos==selected2;
    }

    boolean anySelected() {
        return (selected1>=0) || (selected2>=0);
    }

    boolean select(int pos) {
        if (selected1<0) {
            selected1=pos;
            checkFinished();
            return true;
        }
        else if (selected2<0) {
            selected2=pos;
            checkFinished();
            return true;
        }
        return false;
    }

    void checkFinished() {
        if (isFinished()) {
            Log.d("GAMECONTROLLER", "game is finished");
            setChanged();
            this.notifyObservers();
        }
        else {
            Log.d("GAMECONTROLLER", "game is not finished");
        }
    }

    void unselect(int pos) {
        if (selected1==pos) selected1=-1;
        else if (selected2==pos) selected2=-1;
    }

    boolean isFinished() {
        return (selected1>=0) && (selected2>=0);
    }

    boolean isSuccess() {
        if (isFinished()) {
            return game.cell(selected1).symbol == game.cell(selected2).symbol;
        } else return false;
    }

}
