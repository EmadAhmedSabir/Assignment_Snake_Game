package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.core.content.res.ResourcesCompat;

public class StartGame {
    public static void draw(SurfaceHolder mSurfaceHolder, Canvas mCanvas, int mScore, Paint mPaint, Apple mApple, Snake mSnake, boolean mPaused, Context mContext) {
        // Get a lock on the mCanvas
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();

            // Fill the screen with a color
            mCanvas.drawColor(Color.parseColor("#f3953d"));

            // Set the size and color of the mPaint for the text
            mPaint.setColor(Color.parseColor("#000000"));

            mPaint.setTextSize(80);

            // Draw the score
            mCanvas.drawText("" + mScore, 70, 80, mPaint);

            // Draw the apple and the snake
            mApple.draw(mCanvas, mPaint);
            mSnake.draw(mCanvas, mPaint);

            // Draw some text while paused
            if(mPaused){


                Log.d("tag223","paused");

                // Set the size and color of the mPaint for the text
                mPaint.setColor(Color.parseColor("#000000"));
                mPaint.setTextSize(80);
                mPaint.setTypeface(ResourcesCompat.getFont(mContext, R.font.poppins_regular));

                // Draw the message
                // We will give this an international upgrade soon
                //mCanvas.drawText("Tap To Play!", 200, 700, mPaint);
                mCanvas.drawText("Tap to Play",
                        200, 200, mPaint);
            }



            // Unlock the mCanvas and reveal the graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

}
