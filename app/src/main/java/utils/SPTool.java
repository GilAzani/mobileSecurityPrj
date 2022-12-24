package utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import model.RecordHolder;

public class SPTool {
    private static final String DB_FILE = "DB_FILE";

    private static SPTool instance = null;
    private SharedPreferences preferences;

    private SPTool(Context context){
        preferences = context.getSharedPreferences(DB_FILE,Context.MODE_PRIVATE);
    }

    public static void init(Context context){
        if (instance == null)
            instance = new SPTool(context);
    }

    public static SPTool getInstance() {
        return instance;
    }

    public void putInt(String key, int value) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defValue) {
        return preferences.getInt(key, defValue);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defValue) {
        return preferences.getString(key, defValue);
    }

    public void putFloat(String key, float value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloat(String key, float defValue){
        return preferences.getFloat(key, defValue);
    }
}
