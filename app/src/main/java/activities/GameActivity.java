package activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

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
import utils.LocationFinder;
import utils.MovementDetector;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        findViews();

        initBackground();

        getPreferencesAndRecordsFromUser();

        LocationFinder.init(this);

        getNameFromUser();

        //initGame(); init the game after the user has entered his username
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
        AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);

        alert.setTitle("Enter your username!");
        //alert.setMessage("Message");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String userName = String.valueOf(input.getText());
                setUserName(userName);
                initGame();
            }
        });

        alert.show();
    }

    private void setUserName(String userName) {
        this.userName = userName;
    }

    private void activateCrashSoundEffect() {
        SoundGenerator.getInstance().activateCrashSoundEffect();
    }

    private void initGame() {
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
