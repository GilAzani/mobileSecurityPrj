package utils;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import android.os.Environment;

public class Recorder {

    private Context context;
    private static Recorder recorder = null;

    private static MediaRecorder mediaRecorder;

    private static String latestFilePath = "";

    private Recorder(Context context) {
        this.context = context;
    }

    public static void init(Context context) {
        if (recorder == null) {
            recorder = new Recorder(context);
        }
    }

    public static Recorder getInstance() {
        return recorder;
    }


    public void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //"app/recordings/game_record.mp3"
        mediaRecorder.setOutputFile(getFilePath());
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            Log.d("gilazaniTest", "startRecording: ");
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    public String getLatestFilePath(){
        return latestFilePath;
    }

    private String getFilePath() {
        // Get the app's private internal storage directory
        File directory = context.getExternalFilesDir(null);
        if (directory != null) {
            // Create a file object with a unique name in the directory
            String fileName = "game_record.mp3";
            File file = new File(directory, fileName);

            Log.d("gilazanifile", file.getAbsolutePath());
            // Return the absolute file path

            latestFilePath = file.getAbsolutePath();
            return file.getAbsolutePath();
        } else {
            // Handle the scenario when the directory is null (unable to access internal storage)
            return null;
        }
    }
}
