package com.example.impossiblegame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    private static final String THEME_PREF = "theme_pref";
    private static final String STATS_PREF = "stats_pref";
    private SharedPreferences themeSettings;
    private SharedPreferences.Editor settingsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeSettings = getSharedPreferences(THEME_PREF, MODE_PRIVATE);

        if (!themeSettings.contains("MODE_NIGHT_ON")) {
            settingsEditor = themeSettings.edit();
            settingsEditor.putBoolean("MODE_NIGHT_ON", false);
            settingsEditor.apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            setCurrentTheme();
        }

        setContentView(R.layout.activity_main);

        Button btnPlayWithBot = findViewById(R.id.btn_play_with_bot);
        Button btnPlayWithPlayer = findViewById(R.id.btn_play_with_player);
        Button btnChangeTheme = findViewById(R.id.btn_change_theme);

        btnPlayWithBot.setOnClickListener(v -> startGame(true));
        btnPlayWithPlayer.setOnClickListener(v -> startGame(false));
        btnChangeTheme.setOnClickListener(v -> changeTheme());
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetStats();
    }

    private void startGame(boolean isBot) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("isBot", isBot);
        startActivity(intent);
    }

    private void changeTheme() {
        settingsEditor = themeSettings.edit();
        boolean isNightModeOn = themeSettings.getBoolean("MODE_NIGHT_ON", false);
        settingsEditor.putBoolean("MODE_NIGHT_ON", !isNightModeOn);
        settingsEditor.apply();

        if (!isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        recreate();
    }

    private void setCurrentTheme() {
        boolean isNightModeOn = themeSettings.getBoolean("MODE_NIGHT_ON", false);
        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void resetStats() {
        SharedPreferences preferences = getSharedPreferences(STATS_PREF, MODE_PRIVATE);
        preferences.edit().putInt("playerWins", 0).apply();
        preferences.edit().putInt("botWins", 0).apply();
        preferences.edit().putInt("draws", 0).apply();
    }
}
