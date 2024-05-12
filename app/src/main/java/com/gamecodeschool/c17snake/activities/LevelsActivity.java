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

public class LevelsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        setTitle("Select Level");

        findViewById(R.id.btnEasy).setOnClickListener(v->{
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("levelValue",1);
            startActivity(intent);
        });


        findViewById(R.id.btnMedium).setOnClickListener(v->{
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("levelValue",2);
            startActivity(intent);
        });


        findViewById(R.id.btnHard).setOnClickListener(v->{
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("levelValue",3);
            startActivity(intent);
        });



    }


}