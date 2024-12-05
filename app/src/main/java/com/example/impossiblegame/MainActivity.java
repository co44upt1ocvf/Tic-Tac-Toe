package com.example.impossiblegame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String THEME_PREF = "theme_pref";
    private static final String STATS_PREF = "stats_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTheme();
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
        SharedPreferences preferences = getSharedPreferences(THEME_PREF, MODE_PRIVATE);
        boolean isDarkTheme = preferences.getBoolean("isDarkTheme", false);
        preferences.edit().putBoolean("isDarkTheme", !isDarkTheme).apply();
        recreate();
    }

    private void applyTheme() {
        SharedPreferences preferences = getSharedPreferences(THEME_PREF, MODE_PRIVATE);
        boolean isDarkTheme = preferences.getBoolean("isDarkTheme", false);
        if (isDarkTheme) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme);
        }
    }

    private void resetStats() {
        SharedPreferences preferences = getSharedPreferences(STATS_PREF, MODE_PRIVATE);
        preferences.edit().putInt("playerWins", 0).apply();
        preferences.edit().putInt("botWins", 0).apply();
        preferences.edit().putInt("draws", 0).apply();
    }
}
