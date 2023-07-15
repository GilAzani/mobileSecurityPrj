package activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.example.racehw1.R;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import model.RecordHolder;
import utils.SPTool;

public class MenuActivity extends AppCompatActivity {

    private Switch menu_switch_fastMode;
    private Switch menu_switch_sensorsMode;
    private MaterialButton menu_BTN_start;
    private MaterialButton menu_BTN_leaderboard;
    private AppCompatImageView menu_IMG_background;
    private ArrayList<RecordHolder> recordHolders;
    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissionsResult -> {
                boolean allPermissionsGranted = true;
                for (Boolean isGranted : permissionsResult.values()) {
                    if (!isGranted) {
                        allPermissionsGranted = false;
                        break;
                    }
                }

                if (allPermissionsGranted) {
                    // All requested permissions are granted. Continue with your app logic.
                } else {
                    // Handle the scenario when not all permissions are granted.
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        checkHarmfulMicrophonePermissionAndLocation();

        findViews();

        initBackground();

        //checkFineLocationPermission();

        getRecordHolder();

        menu_BTN_start.setOnClickListener(view -> {startGame();});

        menu_BTN_leaderboard.setOnClickListener(view -> {goToLeaderboard();});
    }

    private void getRecordHolder() {
        String recordHolderAsJsonStringFromSP =
                SPTool.getInstance().getString(LeaderboardActivity.SP_KEY_RECORD_HOLDER, "");
        if (recordHolderAsJsonStringFromSP.equals("")) {
            recordHolders = new ArrayList<>();//empty record as default
        } else {
            Type type = new TypeToken<ArrayList<RecordHolder>>() {}.getType();
            recordHolders = new Gson().fromJson(recordHolderAsJsonStringFromSP, type);
        }
    }

    private void checkHarmfulMicrophonePermissionAndLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Activate Voice Input");
        builder.setMessage("To enable voice input for name activation, please activate the microphone.");

        // Add OK button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] permissions = {
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_FINE_LOCATION
                };
                dialog.dismiss(); // Close the dialog
                requestPermissionLauncher.launch(permissions);
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false); // Make the dialog modal

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            dialog.show();
        }
    }

    private void startGame() {

        Intent startIntent = new Intent(this, GameActivity.class);
        startIntent.putExtra(GameActivity.KEY_MODE,menu_switch_fastMode.isChecked());//true - fast mode, else - default
        startIntent.putExtra(GameActivity.KEY_SENSORS,menu_switch_sensorsMode.isChecked()); //true - sensors mode on, else default
        startIntent.putExtra(GameActivity.KEY_RECORD_HOLDERS, recordHolders);
        startActivity(startIntent);
        finish();
    }

    private void goToLeaderboard() {
        Intent startIntent = new Intent(this, LeaderboardActivity.class);
        startIntent.putExtra(GameActivity.KEY_RECORD_HOLDERS, recordHolders);//pass to leaderboard the updated records
        startIntent.putExtra(LeaderboardActivity.KEY_IS_FROM_MENU, true);
        startActivity(startIntent);
        finish();
    }

//    private void checkFineLocationPermission() {
////        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
////                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
////                != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
////        }
//        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
//    }


    private void initBackground() {
        Glide
                .with(this)
                .load(R.drawable.outer_space_backgrounda)
                .placeholder(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(menu_IMG_background);
    }

    private void findViews() {
        menu_switch_fastMode = findViewById(R.id.menu_switch_fastMode);
        menu_switch_sensorsMode = findViewById(R.id.menu_switch_sensorsMode);
        menu_BTN_start = findViewById(R.id.menu_BTN_start);
        menu_BTN_leaderboard = findViewById(R.id.menu_BTN_leaderboard);
        menu_IMG_background = findViewById(R.id.menu_IMG_background);
    }
}