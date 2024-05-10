package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import androidx.annotation.DrawableRes;

public class SamGhost extends Ghost {

    private Bitmap[] samGhostImages;
    private Bitmap edibleGhostImage;
    private int currentFrameIndex; // Index of the current frame in the animation sequence

    public SamGhost(Context context, int size, int speed, Rect gameBounds, Point snakePosition, Boolean isEdible) {
        super(context, size, speed, gameBounds, snakePosition, isEdible);
        this.currentFrameIndex = 0;
        // Initialize the non-edible animation frames
        samGhostImages = new Bitmap[] {
                BitmapFactory.decodeResource(context.getResources(), R.drawable.sam1),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.sam2)
        };
        // Initialize the edible image
        edibleGhostImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue);
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isEdible) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFrameTime > FRAME_UPDATE_TIME) {
                currentFrameIndex = (currentFrameIndex + 1) % samGhostImages.length;
                lastFrameTime = currentTime;
            }
            canvas.drawBitmap(samGhostImages[currentFrameIndex], position.x, position.y, paint);
        } else {
            // Draw the single blue ghost image when edible
            canvas.drawBitmap(edibleGhostImage, position.x, position.y, paint);
        }
    }
}
