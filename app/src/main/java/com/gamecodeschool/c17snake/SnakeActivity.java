package com.gamecodeschool.c17snake;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SnakeActivity extends Activity {

    SnakeGame mSnakeGame;
    LinearLayout SnackGameView;
    @SuppressLint("StaticFieldLeak")
    public static TextView btnPauseOrResume;
    private TextView txtScore;
    private TextView txtHighScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SnackGameView = findViewById(R.id.idSnakeGameView);
        btnPauseOrResume = findViewById(R.id.btnPauseOrResume);
        txtScore = findViewById(R.id.txtScore);
        txtHighScore = findViewById(R.id.txtHighScore);

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        // Pass the TextViews to the SnakeGame constructor
        mSnakeGame = new SnakeGame(this, size, txtScore, txtHighScore);

        SnackGameView.addView(mSnakeGame);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSnakeGame.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSnakeGame.pause();
    }
}