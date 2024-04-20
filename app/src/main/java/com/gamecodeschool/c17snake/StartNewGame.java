package com.gamecodeschool.c17snake;

//import static android.view.View.VISIBLE;

//import android.content.Context;
//import android.widget.TextView;

import android.graphics.Color;

public class StartNewGame {



    public static void newGame(Snake mSnake, int mScore, boolean playOrNot, int NUM_BLOCKS_WIDE, int mNumBlocksHigh, Apple mApple, long mNextFrameTime) {

        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        // Get the apple ready for dinner
        mApple.spawn();
        // Reset the mScore

        mScore = 0;


        // Setup mNextFrameTime so an update can triggered
        mNextFrameTime = System.currentTimeMillis();
    }

}
