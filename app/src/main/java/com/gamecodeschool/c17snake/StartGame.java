package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

public class StartGame {
    public static void pauseGame(Paint mPaint, Canvas mCanvas, Context mContext) {
        Log.d("tag223", "paused");

        // Set the size and color of the mPaint for the text
        mPaint.setColor(Color.parseColor("#000000"));
        mPaint.setTextSize(80);
        mPaint.setTypeface(ResourcesCompat.getFont(mContext, R.font.poppins_regular));

        // Draw the message
        mCanvas.drawText("Tap to Play", 200, 200, mPaint);
    }
}