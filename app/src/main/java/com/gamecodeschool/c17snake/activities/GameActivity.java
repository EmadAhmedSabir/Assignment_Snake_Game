package com.gamecodeschool.c17snake.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gamecodeschool.c17snake.R;
import com.gamecodeschool.c17snake.SnakeGame;

public class GameActivity extends AppCompatActivity {

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
        getSupportActionBar().hide();


        int value = getIntent().getIntExtra("levelValue",0);


        if(value == 1){
            startGame(2500);
        } else if (value == 2) {
            startGame(1500);
        }else{
            startGame(800);
        }

    }


    private void startGame(int speed){
        SnackGameView = findViewById(R.id.idSnakeGameView);
        btnPauseOrResume = findViewById(R.id.btnPauseOrResume);
        txtScore = findViewById(R.id.txtScore);
        txtHighScore = findViewById(R.id.txtHighScore);

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        // Pass the TextViews to the SnakeGame constructor
        mSnakeGame = new SnakeGame(this,speed, size, txtScore, txtHighScore);

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