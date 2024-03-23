package com.gamecodeschool.c17snake;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SnakeActivity extends Activity {

    SnakeGame mSnakeGame;
    LinearLayout SnackGameView;
    @SuppressLint("StaticFieldLeak")
    public static TextView btnPauseOrResume;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SnackGameView =  findViewById(R.id.idSnakeGameView);
        btnPauseOrResume =  findViewById(R.id.btnPauseOrResume);

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        mSnakeGame = new SnakeGame(this, size);

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
