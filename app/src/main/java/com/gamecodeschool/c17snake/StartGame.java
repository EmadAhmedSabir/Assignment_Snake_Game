package com.gamecodeschool.c17snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class StartGame {

    public static void drawTapToStartText(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(80);
        textPaint.setTextAlign(Paint.Align.CENTER);

        String tapToStartText = "Tap to Start";
        float textWidth = textPaint.measureText(tapToStartText);
        float canvasWidth = canvas.getWidth();
        float canvasHeight = canvas.getHeight();

        canvas.drawText(tapToStartText, canvasWidth / 2, canvasHeight / 2 + (textPaint.descent() + textPaint.ascent()) / 2, textPaint);
    }
}