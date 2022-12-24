package soundEffects;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.example.racehw1.R;

public class CrashSoundEffect extends AsyncTask<Void, Void, Void> {
    private Context context;

    public CrashSoundEffect(Context context) {
        this.context = context;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        MediaPlayer player = MediaPlayer.create(this.context, R.raw.crash_sound_effect);
        player.setLooping(false);
        player.setVolume(1.0f, 1.0f);
        player.start();

        return null;
    }
}
