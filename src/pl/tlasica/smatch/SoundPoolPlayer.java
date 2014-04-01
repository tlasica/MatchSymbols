package pl.tlasica.smatch;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

import java.util.HashMap;

/**
 * Created by tomek on 23.03.14.
 */
public class SoundPoolPlayer {
    
    private SoundPool mShortPlayer= null;
    private HashMap<String,Integer> mSounds = new HashMap<String,Integer>();
    private Settings    settings;
    private Context     context;

    public SoundPoolPlayer(Context pContext)
    {
        context = pContext;
        settings = new Settings(pContext);

        // setup Soundpool
        this.mShortPlayer = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);

        mSounds.put("yes", this.mShortPlayer.load(pContext, R.raw.yes, 1));
        mSounds.put("rrrou", this.mShortPlayer.load(pContext, R.raw.rrrou, 1));
        mSounds.put("no", this.mShortPlayer.load(pContext, R.raw.no, 1));
        mSounds.put("snooring", this.mShortPlayer.load(pContext, R.raw.snoring_2, 1));
    }

    public void playSnooring() {
        playShortResource("snooring");
    }

    public void playYes() {
        playShortResource("yes");
    }

    public void playNo() {
        playShortResource("no");
    }

    public void playRrrou() {
        playShortResource("rrrou");
    }

    private void playShortResource(String name) {
        if (settings.sound()) {
            int iSoundId = (Integer) mSounds.get(name);
            this.mShortPlayer.play(iSoundId, 0.99f, 0.99f, 0, 0, 1);
        }
    }

    // Cleanup
    public void release() {
        // Cleanup
        this.mShortPlayer.release();
        this.mShortPlayer = null;
    }

    public void vibrate() {
        if (settings.sound()) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
        }
    }
}