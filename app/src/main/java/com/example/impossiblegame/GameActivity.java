package com.example.impossiblegame;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

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
                buttons[index].setText("X");
                if (checkForWin("X")) {
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
                buttons[index].setText("O");
                if (checkForWin("O")) {
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
        Move bestMove = minimax(buttons, 5, false);
        buttons[bestMove.index].setText("O");
        if (checkForWin("O")) {
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

    private Move minimax(Button[] currentBoard, int depth, boolean isMaximizing) {
        if (checkForWin("O")) return new Move(-10 + depth, -1);
        if (checkForWin("X")) return new Move(10 - depth, -1);
        if (isBoardFull()) return new Move(0, -1);

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            int bestMove = -1;
            for (int i = 0; i < 9; i++) {
                if (currentBoard[i].getText().toString().isEmpty()) {
                    currentBoard[i].setText("O");
                    Move score = minimax(currentBoard, depth - 1, false);
                    currentBoard[i].setText("");
                    if (score.score > bestScore) {
                        bestScore = score.score;
                        bestMove = i;
                    }
                }
            }
            return new Move(bestScore, bestMove);
        } else {
            int bestScore = Integer.MAX_VALUE;
            int bestMove = -1;
            for (int i = 0; i < 9; i++) {
                if (currentBoard[i].getText().toString().isEmpty()) {
                    currentBoard[i].setText("X");
                    Move score = minimax(currentBoard, depth - 1, true);
                    currentBoard[i].setText("");
                    if (score.score < bestScore) {
                        bestScore = score.score;
                        bestMove = i;
                    }
                }
            }
            return new Move(bestScore, bestMove);
        }
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
        boolean isDarkTheme = preferences.getBoolean("isDarkTheme", false);
        if (isDarkTheme) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme);
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
