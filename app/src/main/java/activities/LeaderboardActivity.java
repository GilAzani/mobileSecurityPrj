package activities;

import static activities.GameActivity.KEY_RECORD_HOLDERS;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.racehw1.R;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;

import activities.GameActivity;
import activities.MenuActivity;
import callbacks.OnClickCallback;
import fragments.ListFragment;
import fragments.MapFragment;
import model.RecordHolder;
import utils.SPTool;

public class LeaderboardActivity extends AppCompatActivity {
    private ListFragment listFragment;
    private MapFragment mapFragment;

    private ArrayList<RecordHolder> recordHolders;
    private boolean isFromMenu;
    private boolean prevIsFastMode;
    private boolean prevIsSensorMode;

    private MaterialButton leaderboard_BTN_backToMenu;
    private MaterialButton leaderboard_BTN_retry;

    public static final String SP_KEY_RECORD_HOLDER = "SP_KEY_RECORD_HOLDER";
    public static final String KEY_IS_FROM_MENU = "KEY_IS_FROM_MENU"; //if leaderboard opens from menu retry -> invisible

    private callbacks.OnClickCallback onClickCallback = new OnClickCallback() {
        @Override
        public void focusOnPoint(int index) {
            mapFragment.zoom(index);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_leaderboard);

        initViews();

        listFragment = new ListFragment();

        mapFragment = new MapFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.leaderboard_FRAME_list, listFragment).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.leaderboard_FRAME_map, mapFragment).commit();

        getDataFromIntent();
        
        setBtnViews();
    }

    private void setBtnViews() {
        leaderboard_BTN_backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMainMenu();
            }
        });
        if(isFromMenu){
            leaderboard_BTN_retry.setVisibility(View.GONE);
        }else{
            leaderboard_BTN_retry.setVisibility(View.VISIBLE);
            leaderboard_BTN_retry.setOnClickListener(view -> {backToGame();});
        }
    }

    private void backToGame() {
        Intent startIntent = new Intent(this, GameActivity.class);
        startIntent.putExtra(GameActivity.KEY_MODE,prevIsFastMode);//true - fast mode, else - default
        startIntent.putExtra(GameActivity.KEY_SENSORS,prevIsSensorMode); //true - sensors mode on, else default
        startIntent.putExtra(GameActivity.KEY_RECORD_HOLDERS, recordHolders);
        startActivity(startIntent);
        finish();
    }

    private void initViews() {
        leaderboard_BTN_retry = findViewById(R.id.leaderboard_BTN_retry);

        leaderboard_BTN_backToMenu = findViewById(R.id.leaderboard_BTN_backToMenu);
    }

    private void backToMainMenu() {
        saveRecordHolders();

        Intent startIntent = new Intent(this, MenuActivity.class);
        startActivity(startIntent);
        finish();
    }

    private void saveRecordHolders() {
        String recordHoldersJson = new Gson().toJson(recordHolders);
        SPTool.getInstance().putString(SP_KEY_RECORD_HOLDER, recordHoldersJson);
    }

    @Override
    protected void onResume() {
        super.onResume();

        listFragment.setList(recordHolders);
        mapFragment.setRecordHolders(recordHolders);
        listFragment.setCallback(onClickCallback);
    }

    private void getDataFromIntent() {
        Intent previousIntent = getIntent();
        recordHolders = (ArrayList<RecordHolder>) previousIntent.getSerializableExtra(KEY_RECORD_HOLDERS);
        isFromMenu = previousIntent.getBooleanExtra(KEY_IS_FROM_MENU, true);//from menu as default
        if(!isFromMenu){//if leaderboard opened from game, save user pref for retry option
            prevIsFastMode = previousIntent.getBooleanExtra(GameActivity.KEY_MODE, false);
            prevIsSensorMode = previousIntent.getBooleanExtra(GameActivity.KEY_SENSORS, false);
        }
    }
}
