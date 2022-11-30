package com.example.racehw1;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

public class GameActivity extends AppCompatActivity {

    private ShapeableImageView[] game_IMG_spaceship;
    private ShapeableImageView [] [] game_IMG_asteroid;
    private ShapeableImageView [] hearts;
    private AppCompatImageView game_IMG_background;
    private ExtendedFloatingActionButton game_FAB_right;
    private ExtendedFloatingActionButton game_FAB_left;

    GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        findViews();

        initBackground();

        initGame();
    }


    final Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this,1000); //do it again in a second.
            gameManager.newAsteroidAndUpdate();
            checkCrash();
            updateAsteroidsUI();
        }
    };

    private void updateAsteroidsUI(){
        boolean asteroids[][] = gameManager.getAsteroidsLocation();
        for (int i = 0; i < asteroids.length; i++) {
            for (int j = 0; j < asteroids[i].length; j++) {
                if (asteroids[i][j]) {
                    game_IMG_asteroid[i][j].setVisibility(View.VISIBLE);
                } else {
                    game_IMG_asteroid[i][j].setVisibility(View.INVISIBLE);
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
        Toast
                .makeText(this, "Crash!",Toast.LENGTH_LONG)
                .show();
    }

    private void spaceMove(int mov) {
        //-1 for left, 1 for right
        int spacePosition = gameManager.getSpaceshipLocation();
        int newPosition = mov + spacePosition;
        if(newPosition > 2 || newPosition < 0){//if you try to move beyond the boundaries nothing will happend
            return;
        }
        else{
            game_IMG_spaceship[spacePosition].setVisibility(View.GONE);
            game_IMG_spaceship[newPosition].setVisibility(View.VISIBLE);
            spacePosition = newPosition;
            gameManager.changeSpaceshipLocation(spacePosition);
            checkCrash();
        }
    }

    private void checkCrash() {
        if(gameManager.checkCrash()) {
            int lives = gameManager.getLives();
            game_IMG_asteroid[0][gameManager.getSpaceshipLocation()].setVisibility(View.INVISIBLE);
            toast();
            vibrate();
            if (lives == 0) {
                handler.removeCallbacks(runnable);
                initGame();
            } else {
                hearts[lives].setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initGame() {
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setVisibility(View.VISIBLE);
        }
        game_IMG_spaceship[0].setVisibility(View.GONE);
        game_IMG_spaceship[1].setVisibility(View.VISIBLE);
        game_IMG_spaceship[2].setVisibility(View.GONE);

        gameManager = new GameManager(1, game_IMG_asteroid.length,
                game_IMG_asteroid[0].length, hearts.length);

        updateAsteroidsUI();

        handler.postDelayed(runnable,1000);

        game_FAB_left.setOnClickListener(view -> spaceMove(-1));
        game_FAB_right.setOnClickListener(view -> spaceMove(1));
    }

    private void vibrate() {

        Vibrator v = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);

        }
    }

    private void findViews() {
        game_IMG_background = findViewById(R.id.game_IMG_background);

        game_IMG_spaceship = new ShapeableImageView[]{
                findViewById(R.id.game_IMG_spaceship0),
                findViewById(R.id.game_IMG_spaceship1),
                findViewById(R.id.game_IMG_spaceship2)
        };

        game_IMG_asteroid = new ShapeableImageView[][] {
                {
                        findViewById(R.id.game_IMG_asteroid_0_0),
                        findViewById(R.id.game_IMG_asteroid_0_1),
                        findViewById(R.id.game_IMG_asteroid_0_2)
                },
                {
                        findViewById(R.id.game_IMG_asteroid_1_0),
                        findViewById(R.id.game_IMG_asteroid_1_1),
                        findViewById(R.id.game_IMG_asteroid_1_2)
                },
                {
                        findViewById(R.id.game_IMG_asteroid_2_0),
                        findViewById(R.id.game_IMG_asteroid_2_1),
                        findViewById(R.id.game_IMG_asteroid_2_2)
                },
                {
                        findViewById(R.id.game_IMG_asteroid_3_0),
                        findViewById(R.id.game_IMG_asteroid_3_1),
                        findViewById(R.id.game_IMG_asteroid_3_2)
                },
                {
                        findViewById(R.id.game_IMG_asteroid_4_0),
                        findViewById(R.id.game_IMG_asteroid_4_1),
                        findViewById(R.id.game_IMG_asteroid_4_2)
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