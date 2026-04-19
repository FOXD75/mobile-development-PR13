package com.example.lab_13;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SwipeActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        imageView = findViewById(R.id.rect);

        // Устанавливаем обработчик свайпов
        imageView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() {
                Toast.makeText(SwipeActivity.this, "Свайп вправо", Toast.LENGTH_SHORT).show();
                // Например, перемещаем объект вправо
                imageView.setX(imageView.getX() + 100);
            }

            @Override
            public void onSwipeLeft() {
                Toast.makeText(SwipeActivity.this, "Свайп влево", Toast.LENGTH_SHORT).show();
                imageView.setX(imageView.getX() - 100);
            }

            @Override
            public void onSwipeTop() {
                Toast.makeText(SwipeActivity.this, "Свайп вверх", Toast.LENGTH_SHORT).show();
                imageView.setY(imageView.getY() - 100);
            }

            @Override
            public void onSwipeBottom() {
                Toast.makeText(SwipeActivity.this, "Свайп вниз", Toast.LENGTH_SHORT).show();
                imageView.setY(imageView.getY() + 100);
            }
        });
    }
}