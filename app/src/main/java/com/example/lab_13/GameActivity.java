package com.example.lab_13;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "wolf_game_prefs";

    private TextView tvScore, tvGameStatus;
    private TextView[] tvLives = new TextView[3];

    private TextView tvWolfTL, tvWolfTR, tvWolfBL, tvWolfBR;
    private TextView tvEggTL, tvEggTR, tvEggBL, tvEggBR;
    private View gameField;

    private int score = 0;
    private int lives = 3;
    private int wolfTrack = 2;
    private boolean isGameRunning = false;
    private boolean isPaused = false;

    private final Handler gameHandler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private int[] eggStep = new int[4];

    private long spawnInterval = 1500;
    private long moveInterval = 400;
    private long lastSpawnTime = 0;
    private long lastMoveTime = 0;
    private static final int TICK_MS = 50;

    private String playerCharacter = "🐺";
    private boolean soundEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initViews();
        loadSettings();
        setupGestures();

        resetGame();
        startGame();
    }

    private void initViews() {
        tvScore = findViewById(R.id.score);
        tvGameStatus = findViewById(R.id.gameStatus);
        tvLives[0] = findViewById(R.id.life1);
        tvLives[1] = findViewById(R.id.life2);
        tvLives[2] = findViewById(R.id.life3);

        tvWolfTL = findViewById(R.id.wolfTopLeft);
        tvWolfTR = findViewById(R.id.wolfTopRight);
        tvWolfBL = findViewById(R.id.wolfBottomLeft);
        tvWolfBR = findViewById(R.id.wolfBottomRight);

        tvEggTL = findViewById(R.id.eggTopLeft);
        tvEggTR = findViewById(R.id.eggTopRight);
        tvEggBL = findViewById(R.id.eggBottomLeft);
        tvEggBR = findViewById(R.id.eggBottomRight);

        gameField = findViewById(R.id.gameField);
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        soundEnabled = prefs.getBoolean("sound_enabled", true);
        playerCharacter = prefs.getString("player_character", "🐺");
        if (playerCharacter == null || playerCharacter.isEmpty()) playerCharacter = "🐺";

        String difficulty = prefs.getString("difficulty", "Средняя");
        switch (difficulty) {
            case "Лёгкая": spawnInterval = 2000; moveInterval = 500; break;
            case "Сложная": spawnInterval = 1000; moveInterval = 300; break;
            default: spawnInterval = 1500; moveInterval = 400; break;
        }
    }

    private void setupGestures() {
        if (gameField == null) return;

        gameField.setOnTouchListener(new TOnSwipeTouchListener(this) {
            @Override public void onSwipeLeft() {
                if (isPaused || !isGameRunning) return;
                moveWolfLogic(0, 1, 2, 3);
                showGestureFeedback("⬅️ Влево");
            }
            @Override public void onSwipeRight() {
                if (isPaused || !isGameRunning) return;
                moveWolfLogic(1, 0, 3, 2);
                showGestureFeedback("➡️ Вправо");
            }
            @Override public void onSwipeTop() {
                if (isPaused || !isGameRunning) return;
                if (wolfTrack == 2) moveWolf(0);
                else if (wolfTrack == 3) moveWolf(1);
                showGestureFeedback("⬆️ Вверх");
            }
            @Override public void onSwipeBottom() {
                if (isPaused || !isGameRunning) return;
                if (wolfTrack == 0) moveWolf(2);
                else if (wolfTrack == 1) moveWolf(3);
                showGestureFeedback("⬇️ Вниз");
            }

            @Override public void onLongPress() {
                resetGame();
                startGame();
                showGestureFeedback("🔄 Новая игра");
            }

            @Override public void onDoubleTap() {
                togglePause();
                showGestureFeedback(isPaused ? "⏸ Пауза" : (isGameRunning ? "▶ Продолжить" : "🎮 Старт"));
            }
        });
    }

    // Вспомогательный метод для свайпов влево/вправо (упрощает логику)
    private void moveWolfLogic(int current, int opposite, int down, int up) {
        if (wolfTrack == current) moveWolf(opposite);
        else if (wolfTrack == down) moveWolf(up);
        else if (wolfTrack == opposite) moveWolf(current);
        else if (wolfTrack == up) moveWolf(down);
    }

    private void showGestureFeedback(String message) {
        tvGameStatus.setText(message);
        tvGameStatus.setVisibility(View.VISIBLE);
        tvGameStatus.setAlpha(1.0f);
        tvGameStatus.animate().alpha(0.0f).setDuration(800)
                .withEndAction(() -> {
                    if (isGameRunning && !isPaused) tvGameStatus.setVisibility(View.GONE);
                }).start();
    }

    private void moveWolf(int newTrack) {
        wolfTrack = newTrack;
        renderWolf();
        checkImmediateCatch();
    }

    private void togglePause() {
        if (!isGameRunning && !isPaused) {
            startGame(); // Если игра ещё не началась → запускаем
            return;
        }
        if (isPaused) resumeGame();
        else pauseGame();
    }

    private void resetGame() {
        score = 0;
        lives = 3;
        wolfTrack = 2;
        isGameRunning = false;
        isPaused = false;
        for (int i = 0; i < 4; i++) eggStep[i] = -1;
        updateUI();
        renderWolf();
        renderEggs();
    }

    private void startGame() {
        if (isGameRunning) return;
        isGameRunning = true;
        isPaused = false;
        lastSpawnTime = SystemClock.uptimeMillis();
        lastMoveTime = SystemClock.uptimeMillis();
        tvGameStatus.setText("🎮 Игра началась!");
        tvGameStatus.setVisibility(View.VISIBLE);
        startGameLoop();
    }

    private void pauseGame() {
        if (!isGameRunning) return;
        isPaused = true;
        tvGameStatus.setText("⏸ Пауза");
        tvGameStatus.setVisibility(View.VISIBLE);
        stopGameLoop();
    }

    private void resumeGame() {
        if (!isGameRunning || !isPaused) return;
        isPaused = false;
        long now = SystemClock.uptimeMillis();
        lastSpawnTime = now;
        lastMoveTime = now;
        tvGameStatus.setText("▶ Продолжаем...");
        startGameLoop();
    }

    private void gameOver() {
        isGameRunning = false;
        stopGameLoop();
        saveHighScore();
        tvGameStatus.setText("💔 Игра окончена!");
        tvGameStatus.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Итог: " + score + " яиц", Toast.LENGTH_LONG).show();
    }

    private void startGameLoop() {
        stopGameLoop();
        gameHandler.post(tickRunnable);
    }

    private void stopGameLoop() {
        gameHandler.removeCallbacks(tickRunnable);
    }

    private final Runnable tickRunnable = new Runnable() {
        @Override public void run() {
            if (!isGameRunning || isPaused) return;
            long now = SystemClock.uptimeMillis();
            if (now - lastSpawnTime >= spawnInterval) { trySpawnEgg(); lastSpawnTime = now; }
            if (now - lastMoveTime >= moveInterval) { moveEggs(); lastMoveTime = now; }
            gameHandler.postDelayed(this, TICK_MS);
        }
    };

    private void trySpawnEgg() {
        List<Integer> freeTracks = new ArrayList<>();
        for (int i = 0; i < 4; i++) if (eggStep[i] == -1) freeTracks.add(i);
        if (freeTracks.isEmpty()) return;
        eggStep[freeTracks.get(random.nextInt(freeTracks.size()))] = 0;
    }

    private void moveEggs() {
        for (int track = 0; track < 4; track++) {
            if (eggStep[track] == -1) continue;
            eggStep[track]++;
            if (eggStep[track] == 3) {
                if (wolfTrack == track) onEggCaught(track);
                else onEggMissed(track);
                eggStep[track] = -1;
            }
        }
        renderEggs();
    }

    private void checkImmediateCatch() {
        if (eggStep[wolfTrack] == 2) {
            onEggCaught(wolfTrack);
            eggStep[wolfTrack] = -1;
            renderEggs();
        }
    }

    private void onEggCaught(int track) {
        score++;
        updateUI();
        if (score % 10 == 0) {
            spawnInterval = Math.max(600, spawnInterval - 150);
            moveInterval = Math.max(200, moveInterval - 50);
            Toast.makeText(this, "🔥 Скорость ↑", Toast.LENGTH_SHORT).show();
        }
    }

    private void onEggMissed(int track) {
        lives--;
        updateUI();
        if (lives <= 0) gameOver();
    }

    private void renderWolf() {
        tvWolfTL.setText(""); tvWolfTR.setText("");
        tvWolfBL.setText(""); tvWolfBR.setText("");
        switch (wolfTrack) {
            case 0: tvWolfTL.setText(playerCharacter); break;
            case 1: tvWolfTR.setText(playerCharacter); break;
            case 2: tvWolfBL.setText(playerCharacter); break;
            case 3: tvWolfBR.setText(playerCharacter); break;
        }
    }

    private void renderEggs() {
        tvEggTL.setText(eggStep[0] >= 0 ? "🥚" : "");
        tvEggTR.setText(eggStep[1] >= 0 ? "🥚" : "");
        tvEggBL.setText(eggStep[2] >= 0 ? "🥚" : "");
        tvEggBR.setText(eggStep[3] >= 0 ? "🥚" : "");
    }

    private void updateUI() {
        tvScore.setText(String.valueOf(score));
        for (int i = 0; i < 3; i++) {
            float alpha = (i < lives) ? 0.5f : 1.0f;
            tvLives[i].setAlpha(alpha);
        }
    }

    private void saveHighScore() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentHigh = prefs.getInt("high_score", 0);
        if (score > currentHigh) {
            prefs.edit().putInt("high_score", score).apply();
            Toast.makeText(this, "🏆 Новый рекорд: " + score, Toast.LENGTH_SHORT).show();
        }
    }
}