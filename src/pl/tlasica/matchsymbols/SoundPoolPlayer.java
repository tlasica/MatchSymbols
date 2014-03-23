package pl.tlasica.matchsymbols;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

/**
 * Created by tomek on 23.03.14.
 */
public class SoundPoolPlayer {
    
    private SoundPool mShortPlayer= null;
    private HashMap<String,Integer> mSounds = new HashMap<String,Integer>();

    public SoundPoolPlayer(Context pContext)
    {
        // setup Soundpool
        this.mShortPlayer = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);


        mSounds.put("yes", this.mShortPlayer.load(pContext, R.raw.yes, 1));
        mSounds.put("rrrou", this.mShortPlayer.load(pContext, R.raw.rrrou, 1));
        mSounds.put("no", this.mShortPlayer.load(pContext, R.raw.no, 1));
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
        int iSoundId = (Integer) mSounds.get(name);
        this.mShortPlayer.play(iSoundId, 0.99f, 0.99f, 0, 0, 1);
    }

    // Cleanup
    public void release() {
        // Cleanup
        this.mShortPlayer.release();
        this.mShortPlayer = null;
    }
}