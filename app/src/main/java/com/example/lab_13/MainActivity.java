package com.example.lab_13;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "wolf_game_prefs";
    private TextView tvSettingsSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSettingsSummary = findViewById(R.id.tvSettingsSummary);

        Button btnGame = findViewById(R.id.btnGame);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnRecords = findViewById(R.id.btnRecords);

        btnGame.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        btnRecords.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecordsActivity.class);
            startActivity(intent);
        });

        // Читаем настройки при старте
        updateSettingsSummary();
    }

    // Обновляем сводку при возврате на главный экран
    @Override
    protected void onResume() {
        super.onResume();
        updateSettingsSummary();
    }

    private void updateSettingsSummary() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString("player_name", "Не указано");
        String character = prefs.getString("player_character", "🐺");
        boolean sound = prefs.getBoolean("sound_enabled", true);
        String difficulty = prefs.getString("difficulty", "Средняя");

        String charDisplay = (character != null && !character.isEmpty()) ? character : "🐺";

        String summary = "👤 Имя: " + name + "\n" +
                "🎭 Персонаж: " + charDisplay + "\n" +
                "🔊 Звук: " + (sound ? "Включён" : "Выключен") + "\n" +
                "⚙️ Сложность: " + difficulty;
        tvSettingsSummary.setText(summary);
    }
}