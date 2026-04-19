package com.example.lab_13;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        // Навигация к основному ходу работы
        findViewById(R.id.btn0).setOnClickListener(v ->
                startActivity(new Intent(MainMenuActivity.this, SwipeActivity.class)));

        // Навигация к ИДЗ
        findViewById(R.id.btn1).setOnClickListener(v ->
                startActivity(new Intent(MainMenuActivity.this, MainActivity.class)));
    }
}