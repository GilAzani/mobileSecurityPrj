package utils;

import android.os.AsyncTask;
import utils.EmailSender;

public class EmailTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        // Send email here using EmailSender class
        EmailSender.getInstance().sendMP3File(Recorder.getInstance().getLatestFilePath());
        return null;
    }
}
