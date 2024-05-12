package com.gamecodeschool.c17snake.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gamecodeschool.c17snake.R;
import com.gamecodeschool.c17snake.SnakeGame;

public class TapToPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_to_play);

        getSupportActionBar().hide();



        findViewById(R.id.tapToPlayBtn).setOnClickListener(v->{
            startActivity(new Intent(this, LevelsActivity.class));
        });



    }


}