package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import androidx.annotation.DrawableRes;

public class SamGhost extends Ghost {
    private final int[] animationSequence; // Array of drawable resources for animation
    private int currentFrameIndex; // Index of the current frame in the animation sequence

    public SamGhost(Context context, int size, int speed, Rect gameBounds, Point snakePosition, @DrawableRes int[] animationSequence) {
        super(context, size, speed, gameBounds, snakePosition);
        this.animationSequence = animationSequence;
        currentFrameIndex = 0;
    }

    @Override
    public void draw(Canvas canvas) {
        @DrawableRes int currentFrameResource = animationSequence[currentFrameIndex];
        currentFrameIndex = (currentFrameIndex + 1) % animationSequence.length;
    }
}
