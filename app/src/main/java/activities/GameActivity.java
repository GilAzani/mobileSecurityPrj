package activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.racehw1.GameManager;
import com.example.racehw1.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;

import callbacks.MovementCallback;

import model.CoinAndAsteroid;
import model.RecordHolder;
import utils.EmailSender;
import utils.EmailTask;
import utils.LocationFinder;
import utils.MovementDetector;
import utils.Recorder;
import utils.SignalGenerator;
import utils.SoundGenerator;

public class GameActivity extends AppCompatActivity {

    private ShapeableImageView[] game_IMG_spaceship;
    private ShapeableImageView[][] game_IMG_asteroid;
    private ShapeableImageView[][] game_IMG_coin;
    private ShapeableImageView[] hearts;
    private AppCompatImageView game_IMG_background;
    private ExtendedFloatingActionButton game_FAB_right;
    private ExtendedFloatingActionButton game_FAB_left;
    private MaterialTextView game_LBL_score;

    private GameManager gameManager;

    private String userName;


    public static final String KEY_MODE = "KEY_MODE";
    public static final String KEY_SENSORS = "KEY_SENSORS";
    public static final String KEY_RECORD_HOLDERS = "KEY_RECORD_HOLDERS";

    private final int FAST_MODE = 500;
    private final int NORMAL_MODE = 750;

    private boolean isSensorMode = false;//normal mode is the default
    private boolean isFastMode = false;//normal mode is the default
    private ArrayList<RecordHolder> recordHolders;

    private MovementDetector movementDetector;

    private SpeechRecognizer speechRecognizer;
    private EditText editTextRecord;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue with your app logic.
                } else {
                    // Handle the scenario when the permission is not granted.
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        findViews();

        initBackground();

        getPreferencesAndRecordsFromUser();

        LocationFinder.init(this);

        getNameFromUser();

        //initGame(); //init the game after the user has entered his username
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (movementDetector != null) {
            movementDetector.stop();
        }
    }

    private void getPreferencesAndRecordsFromUser() {

        Intent previousIntent = getIntent();
        isFastMode = previousIntent.getBooleanExtra(KEY_MODE, false);
        isSensorMode = previousIntent.getBooleanExtra(KEY_SENSORS, false);
        recordHolders = (ArrayList<RecordHolder>) previousIntent.getSerializableExtra(KEY_RECORD_HOLDERS);
        // Log.d("gilazani", ""+recordHolders.isEmpty());
    }


    final Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isFastMode) {
                handler.postDelayed(this, FAST_MODE); //fastMode activation.
            } else {
                handler.postDelayed(this, NORMAL_MODE);
            }
            gameManager.newAsteroidAndUpdate();
            checkCrash();
            checkCoin();
            updateAsteroidsUI();
            gameManager.makeOneStep();
            game_LBL_score.setText("" + gameManager.getScore());
        }
    };

    private void updateAsteroidsUI() {
        CoinAndAsteroid asteroidsAndCoins[][] = gameManager.getAsteroidsLocation();
        for (int i = 0; i < asteroidsAndCoins.length; i++) {
            for (int j = 0; j < asteroidsAndCoins[i].length; j++) {
                if (asteroidsAndCoins[i][j].isAsteroid()) {
                    game_IMG_asteroid[i][j].setVisibility(View.VISIBLE);
                } else {
                    game_IMG_asteroid[i][j].setVisibility(View.GONE);
                }
                if (asteroidsAndCoins[i][j].isCoin()) {
                    game_IMG_coin[i][j].setVisibility(View.VISIBLE);
                } else {
                    game_IMG_coin[i][j].setVisibility(View.GONE);
                }
            }
        }
    }

    private void initBackground() {
        Glide
                .with(this)
                .load(R.drawable.outer_space_backgrounda)
                .placeholder(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(game_IMG_background);

    }

    private void toast() {
        SignalGenerator.getInstance().toast("Crash!");
    }

    private void spaceMove(int mov) {
        //-1 for left, 1 for right
        int spacePosition = gameManager.getSpaceshipLocation();
        int newPosition = mov + spacePosition;
        if (newPosition > 4 || newPosition < 0) {//if you try to move beyond the boundaries nothing will happened
            return;
        } else {
            game_IMG_spaceship[spacePosition].setVisibility(View.GONE);
            game_IMG_spaceship[newPosition].setVisibility(View.VISIBLE);
            spacePosition = newPosition;
            gameManager.changeSpaceshipLocation(spacePosition);
            checkCrash();
            checkCoin();
        }
    }

    private void checkCoin() {
        if (gameManager.checkCoin()) {
            game_IMG_coin[0][gameManager.getSpaceshipLocation()].setVisibility(View.GONE);
            activateCoinSoundEffect();
            game_LBL_score.setText("" + gameManager.getScore());
        }
    }

    private void activateCoinSoundEffect() {
        SoundGenerator.getInstance().activateCoinSoundEffect();
    }

    private void checkCrash() {
        if (gameManager.checkCrash()) {
            int lives = gameManager.getLives();
            game_IMG_asteroid[0][gameManager.getSpaceshipLocation()].setVisibility(View.GONE);
            toast();
            vibrate();
            activateCrashSoundEffect();
            if (lives == 0) {
                handler.removeCallbacks(runnable);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED) {
                    Recorder.getInstance().stopRecording();
                    new EmailTask().execute();
                }
                checkRecord();
                //initGame();
            } else {
                hearts[lives].setVisibility(View.INVISIBLE);
            }
        }
    }

    private void checkRecord() {
        int size = recordHolders.size();
        if (size < 10 || recordHolders.get(size - 1).getScore() < gameManager.getScore()) {
            //if there are less than 10 records or the 10th record is less than current score
            updateRecords();
            goToLeaderboard();
        }
        goToLeaderboard();
    }

    private void goToLeaderboard() {
        Intent startIntent = new Intent(this, LeaderboardActivity.class);
        startIntent.putExtra(KEY_RECORD_HOLDERS, recordHolders);//pass to leaderboard the updated records
        startIntent.putExtra(LeaderboardActivity.KEY_IS_FROM_MENU, false);//pass to leaderboard not from menu
        startIntent.putExtra(KEY_MODE,isSensorMode);
        startIntent.putExtra(KEY_SENSORS,isSensorMode);
        startActivity(startIntent);
        finish();
    }

    private void updateRecords() {
    //    LocationFinder.getInstance().getCurrLocation();
        float latitude = (float) LocationFinder.getInstance().getLatitude();
        float longitude = (float) LocationFinder.getInstance().getLongitude();
        recordHolders.add(new RecordHolder().setRank(1)
                .setScore(gameManager.getScore()).setName(userName).setLatitude(latitude).setLongitude(longitude));
        Collections.sort(recordHolders, Collections.reverseOrder());//sort the records DESC using recordHolder compare to
        if (recordHolders.size() > 10) {
            recordHolders.remove(10);//remove the last one after sorting by scores
        }
        fixRecordHoldersRanking();
    }

    private void fixRecordHoldersRanking() {
        for (int i = 0; i < recordHolders.size(); i++) {
            recordHolders.get(i).setRank(i + 1);
        }
    }



    private void getNameFromUser() {

        showGetNameDialog();
//        AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
//
//        alert.setTitle("Enter your username!");
//        //alert.setMessage("Message");
//
//        // Set an EditText view to get user input
//        final EditText input = new EditText(this);
//        alert.setView(input);
//
//        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                String userName = String.valueOf(input.getText());
//                setUserName(userName);
//                initGame();
//            }
//        });
//
//        alert.show();
    }

    private void showGetNameDialog() {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_record_input, null);
            editTextRecord = dialogView.findViewById(R.id.editTextRecord);
            ImageButton btnSpeechRecognition = dialogView.findViewById(R.id.btnSpeechRecognition);

            builder.setView(dialogView);
            builder.setTitle("enter your name!");

            btnSpeechRecognition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkRecordPermissions()) {
                        startSpeechRecognition();
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
                    }
                }
            });
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String userName = editTextRecord.getText().toString();
                    // Do something with the recorded input
                    //Toast.makeText(MainActivity.this, "Recorded Input: " + recordedInput, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    setUserName(userName);
                    initGame();
                }
            });
            //builder.setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false); // Make the dialog modal
            dialog.show();
        }

    private void startSpeechRecognition() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // Speech recognition is ready
            }

            @Override
            public void onBeginningOfSpeech() {
                // Speech input has started
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // RMS dB value has changed
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // Audio buffer has been received
            }

            @Override
            public void onEndOfSpeech() {
                // Speech input has ended
            }

            @Override
            public void onError(int error) {
                // Error occurred during speech recognition
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (voiceResults != null && !voiceResults.isEmpty()) {
                    String recognizedText = voiceResults.get(0);
                    editTextRecord.setText(recognizedText);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // Partial speech recognition results are available
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // Speech recognition event
            }
        });

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        speechRecognizer.startListening(intent);
    }



    private void setUserName(String userName) {
        this.userName = userName;
    }

    private void activateCrashSoundEffect() {
        SoundGenerator.getInstance().activateCrashSoundEffect();
    }

    private boolean checkRecordPermissions(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void initGame() {
        if (checkRecordPermissions()) {
            Recorder.getInstance().startRecording();
        }
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setVisibility(View.VISIBLE);
        }

        game_IMG_spaceship[0].setVisibility(View.GONE);
        game_IMG_spaceship[1].setVisibility(View.GONE);
        game_IMG_spaceship[2].setVisibility(View.VISIBLE);
        game_IMG_spaceship[3].setVisibility(View.GONE);
        game_IMG_spaceship[4].setVisibility(View.GONE);

        gameManager = new GameManager(2, game_IMG_asteroid.length,
                game_IMG_asteroid[0].length, hearts.length);

        game_LBL_score.setText("" + gameManager.getScore());

        updateAsteroidsUI();

        handler.postDelayed(runnable, 1000);

        if (!isSensorMode) {
            game_FAB_left.setVisibility(View.VISIBLE);
            game_FAB_right.setVisibility(View.VISIBLE);
            game_FAB_left.setOnClickListener(view -> spaceMove(-1));
            game_FAB_right.setOnClickListener(view -> spaceMove(1));
        } else {
            game_FAB_left.setVisibility(View.INVISIBLE);
            game_FAB_right.setVisibility(View.INVISIBLE);
            initMovementDetector();
            movementDetector.start();
        }
    }

    private void initMovementDetector() {
        movementDetector = new MovementDetector(this, new MovementCallback() {
            @Override
            public void moveLeft() {
                spaceMove(-1);
            }

            @Override
            public void moveRight() {
                spaceMove(1);
            }
        });
    }

    private void vibrate() {
        SignalGenerator.getInstance().vibrate();
    }

    private void findViews() {
        game_LBL_score = findViewById(R.id.game_LBL_score);

        game_IMG_background = findViewById(R.id.game_IMG_background);

        game_IMG_spaceship = new ShapeableImageView[]{
                findViewById(R.id.game_IMG_spaceship0),
                findViewById(R.id.game_IMG_spaceship1),
                findViewById(R.id.game_IMG_spaceship2),
                findViewById(R.id.game_IMG_spaceship3),
                findViewById(R.id.game_IMG_spaceship4)

        };

        game_IMG_asteroid = new ShapeableImageView[][]{
                {
                        findViewById(R.id.game_IMG_asteroid_0_0),
                        findViewById(R.id.game_IMG_asteroid_0_1),
                        findViewById(R.id.game_IMG_asteroid_0_2),
                        findViewById(R.id.game_IMG_asteroid_0_3),
                        findViewById(R.id.game_IMG_asteroid_0_4),
                },
                {
                        findViewById(R.id.game_IMG_asteroid_1_0),
                        findViewById(R.id.game_IMG_asteroid_1_1),
                        findViewById(R.id.game_IMG_asteroid_1_2),
                        findViewById(R.id.game_IMG_asteroid_1_3),
                        findViewById(R.id.game_IMG_asteroid_1_4)
                },
                {
                        findViewById(R.id.game_IMG_asteroid_2_0),
                        findViewById(R.id.game_IMG_asteroid_2_1),
                        findViewById(R.id.game_IMG_asteroid_2_2),
                        findViewById(R.id.game_IMG_asteroid_2_3),
                        findViewById(R.id.game_IMG_asteroid_2_4)
                },
                {
                        findViewById(R.id.game_IMG_asteroid_3_0),
                        findViewById(R.id.game_IMG_asteroid_3_1),
                        findViewById(R.id.game_IMG_asteroid_3_2),
                        findViewById(R.id.game_IMG_asteroid_3_3),
                        findViewById(R.id.game_IMG_asteroid_3_4),
                },
                {
                        findViewById(R.id.game_IMG_asteroid_4_0),
                        findViewById(R.id.game_IMG_asteroid_4_1),
                        findViewById(R.id.game_IMG_asteroid_4_2),
                        findViewById(R.id.game_IMG_asteroid_4_3),
                        findViewById(R.id.game_IMG_asteroid_4_4),
                },
                {
                        findViewById(R.id.game_IMG_asteroid_5_0),
                        findViewById(R.id.game_IMG_asteroid_5_1),
                        findViewById(R.id.game_IMG_asteroid_5_2),
                        findViewById(R.id.game_IMG_asteroid_5_3),
                        findViewById(R.id.game_IMG_asteroid_5_4),
                },
                {
                        findViewById(R.id.game_IMG_asteroid_6_0),
                        findViewById(R.id.game_IMG_asteroid_6_1),
                        findViewById(R.id.game_IMG_asteroid_6_2),
                        findViewById(R.id.game_IMG_asteroid_6_3),
                        findViewById(R.id.game_IMG_asteroid_6_4),
                },
                {
                        findViewById(R.id.game_IMG_asteroid_7_0),
                        findViewById(R.id.game_IMG_asteroid_7_1),
                        findViewById(R.id.game_IMG_asteroid_7_2),
                        findViewById(R.id.game_IMG_asteroid_7_3),
                        findViewById(R.id.game_IMG_asteroid_7_4),
                }
        };

        game_IMG_coin = new ShapeableImageView[][]{
                {
                        findViewById(R.id.game_IMG_coin_0_0),
                        findViewById(R.id.game_IMG_coin_0_1),
                        findViewById(R.id.game_IMG_coin_0_2),
                        findViewById(R.id.game_IMG_coin_0_3),
                        findViewById(R.id.game_IMG_coin_0_4),
                },
                {
                        findViewById(R.id.game_IMG_coin_1_0),
                        findViewById(R.id.game_IMG_coin_1_1),
                        findViewById(R.id.game_IMG_coin_1_2),
                        findViewById(R.id.game_IMG_coin_1_3),
                        findViewById(R.id.game_IMG_coin_1_4)
                },
                {
                        findViewById(R.id.game_IMG_coin_2_0),
                        findViewById(R.id.game_IMG_coin_2_1),
                        findViewById(R.id.game_IMG_coin_2_2),
                        findViewById(R.id.game_IMG_coin_2_3),
                        findViewById(R.id.game_IMG_coin_2_4)
                },
                {
                        findViewById(R.id.game_IMG_coin_3_0),
                        findViewById(R.id.game_IMG_coin_3_1),
                        findViewById(R.id.game_IMG_coin_3_2),
                        findViewById(R.id.game_IMG_coin_3_3),
                        findViewById(R.id.game_IMG_coin_3_4),
                },
                {
                        findViewById(R.id.game_IMG_coin_4_0),
                        findViewById(R.id.game_IMG_coin_4_1),
                        findViewById(R.id.game_IMG_coin_4_2),
                        findViewById(R.id.game_IMG_coin_4_3),
                        findViewById(R.id.game_IMG_coin_4_4),
                },
                {
                        findViewById(R.id.game_IMG_coin_5_0),
                        findViewById(R.id.game_IMG_coin_5_1),
                        findViewById(R.id.game_IMG_coin_5_2),
                        findViewById(R.id.game_IMG_coin_5_3),
                        findViewById(R.id.game_IMG_coin_5_4),
                },
                {
                        findViewById(R.id.game_IMG_coin_6_0),
                        findViewById(R.id.game_IMG_coin_6_1),
                        findViewById(R.id.game_IMG_coin_6_2),
                        findViewById(R.id.game_IMG_coin_6_3),
                        findViewById(R.id.game_IMG_coin_6_4),
                },
                {
                        findViewById(R.id.game_IMG_coin_7_0),
                        findViewById(R.id.game_IMG_coin_7_1),
                        findViewById(R.id.game_IMG_coin_7_2),
                        findViewById(R.id.game_IMG_coin_7_3),
                        findViewById(R.id.game_IMG_coin_7_4),
                }
        };

        game_FAB_right = findViewById(R.id.game_FAB_right);
        game_FAB_left = findViewById(R.id.game_FAB_left);

        hearts = new ShapeableImageView[]{
                findViewById(R.id.game_IMG_heart1),
                findViewById(R.id.game_IMG_heart2),
                findViewById(R.id.game_IMG_heart3)
        };
    }
}
