package utils;

import android.content.Context;

import soundEffects.CoinSoundEffect;
import soundEffects.CrashSoundEffect;

public class SoundGenerator {

    private static SoundGenerator soundGenerator = null;

    private Context context;
    private static CoinSoundEffect coinSoundEffect;
    private static CrashSoundEffect crashSoundEffect;

    private SoundGenerator(Context context) {
        this.context = context;
    }

    public static void init(Context context) {
        if (soundGenerator == null) {
            soundGenerator = new SoundGenerator(context);
        }
    }

    public static SoundGenerator getInstance() {
        return soundGenerator;
    }

    public void activateCoinSoundEffect() {
        coinSoundEffect = new CoinSoundEffect(context);//task can be exe only once -> new one every coin
        coinSoundEffect.execute();
    }

    public void activateCrashSoundEffect() {
        crashSoundEffect = new CrashSoundEffect(context);//task can be exe only once -> new one every crash
        crashSoundEffect.execute();
    }
}
