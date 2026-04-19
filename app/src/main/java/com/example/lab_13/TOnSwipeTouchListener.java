package com.example.lab_13;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class TOnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private boolean isLongPressHandled = false;

    public TOnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 50;
        private static final int SWIPE_VELOCITY_THRESHOLD = 50;

        @Override
        public boolean onDown(MotionEvent e) {
            isLongPressHandled = false;
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            isLongPressHandled = true;
            TOnSwipeTouchListener.this.onLongPress();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            TOnSwipeTouchListener.this.onDoubleTap();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (isLongPressHandled) return false;

            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    // Горизонтальный свайп
                    if (Math.abs(diffX) > SWIPE_THRESHOLD &&
                            Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            TOnSwipeTouchListener.this.onSwipeRight();
                        } else {
                            TOnSwipeTouchListener.this.onSwipeLeft();
                        }
                        result = true;
                    }
                } else {
                    // Вертикальный свайп
                    if (Math.abs(diffY) > SWIPE_THRESHOLD &&
                            Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            TOnSwipeTouchListener.this.onSwipeBottom();
                        } else {
                            TOnSwipeTouchListener.this.onSwipeTop();
                        }
                        result = true;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {}
    public void onSwipeLeft() {}
    public void onSwipeTop() {}
    public void onSwipeBottom() {}
    public void onLongPress() {}
    public void onDoubleTap() {}
}