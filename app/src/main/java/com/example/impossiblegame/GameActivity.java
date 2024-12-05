package com.example.impossiblegame;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class GameActivity extends AppCompatActivity {

    private static final String STATS_PREF = "stats_pref";
    private static final String THEME_PREF = "theme_pref";
    private boolean isBot;
    private Button[] buttons = new Button[9];
    private boolean isPlayerTurn = true;
    private int playerWins, botWins, draws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTheme();
        setContentView(R.layout.activity_game);

        isBot = getIntent().getBooleanExtra("isBot", false);
        GridLayout gridLayout = findViewById(R.id.grid_layout);
        TextView txtStats = findViewById(R.id.txt_stats);

        for (int i = 0; i < 9; i++) {
            final int index = i;
            buttons[i] = (Button) gridLayout.getChildAt(i);
            buttons[i].setOnClickListener(v -> onButtonClick(index));
        }

        loadStats();
        updateStatsText(txtStats);
    }

    private void onButtonClick(int index) {
        if (buttons[index].getText().toString().isEmpty()) {
            if (isPlayerTurn) {
                buttons[index].setText("O");
                if (checkForWin("O")) {
                    playerWins++;
                    updateStats();
                    resetGame();
                } else if (isBoardFull()) {
                    draws++;
                    updateStats();
                    resetGame();
                } else {
                    isPlayerTurn = false;
                    if (isBot) {
                        botMove();
                    }
                }
            } else {
                buttons[index].setText("X");
                if (checkForWin("X")) {
                    botWins++;
                    updateStats();
                    resetGame();
                } else if (isBoardFull()) {
                    draws++;
                    updateStats();
                    resetGame();
                } else {
                    isPlayerTurn = true;
                }
            }
        }
    }

    private void botMove() {
        Move bestMove = findBestMove();
        buttons[bestMove.index].setText("X");
        if (checkForWin("X")) {
            botWins++;
            updateStats();
            resetGame();
        } else if (isBoardFull()) {
            draws++;
            updateStats();
            resetGame();
        } else {
            isPlayerTurn = true;
        }
    }

    private Move findBestMove() {
        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().toString().isEmpty()) {
                return new Move(0, i);
            }
        }
        return new Move(0, -1);
    }

    private boolean checkForWin(String player) {
        for (int[] winPosition : WIN_POSITIONS) {
            if (buttons[winPosition[0]].getText().toString().equals(player) &&
                    buttons[winPosition[1]].getText().toString().equals(player) &&
                    buttons[winPosition[2]].getText().toString().equals(player)) {
                return true;
            }
        }
        return false;
    }

    private boolean isBoardFull() {
        for (Button button : buttons) {
            if (button.getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void resetGame() {
        for (Button button : buttons) {
            button.setText("");
        }
        isPlayerTurn = true;
    }

    private void loadStats() {
        SharedPreferences preferences = getSharedPreferences(STATS_PREF, MODE_PRIVATE);
        playerWins = preferences.getInt("playerWins", 0);
        botWins = preferences.getInt("botWins", 0);
        draws = preferences.getInt("draws", 0);
    }

    private void updateStats() {
        SharedPreferences preferences = getSharedPreferences(STATS_PREF, MODE_PRIVATE);
        preferences.edit().putInt("playerWins", playerWins).apply();
        preferences.edit().putInt("botWins", botWins).apply();
        preferences.edit().putInt("draws", draws).apply();
        updateStatsText(findViewById(R.id.txt_stats));
    }

    @SuppressLint("DefaultLocale")
    private void updateStatsText(TextView txtStats) {
        txtStats.setText(String.format("Wins: %d, Losses: %d, Draws: %d", playerWins, botWins, draws));
    }

    private void applyTheme() {
        SharedPreferences preferences = getSharedPreferences(THEME_PREF, MODE_PRIVATE);
        boolean isNightModeOn = preferences.getBoolean("MODE_NIGHT_ON", false);
        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private static final int[][] WIN_POSITIONS = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
    };

    private static class Move {
        int score;
        int index;

        Move(int score, int index) {
            this.score = score;
            this.index = index;
        }
    }
}
