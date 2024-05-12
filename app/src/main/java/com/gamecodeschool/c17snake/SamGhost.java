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
    private Bitmap[] edibleGhostImage;
    private int currentFrameIndex; // Index of the current frame in the animation sequence

    public SamGhost(Context context, int size, int speed, Rect gameBounds, Boolean isEdible) {
        super(context, size, speed, gameBounds, isEdible);
        this.currentFrameIndex = 0;
        // Initialize the non-edible animation frames
        samGhostImages = new Bitmap[] {
                BitmapFactory.decodeResource(context.getResources(), R.drawable.sam1),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.sam2)
        };
        // Initialize the edible image
        edibleGhostImage= new Bitmap[]{
                BitmapFactory.decodeResource(context.getResources(), R.drawable.blue),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.blue2)
        };
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
            canvas.drawBitmap(edibleGhostImage[currentFrameIndex], position.x, position.y, paint);
        }
    }

    @Override
    public void resetPosition() {
        position.x = 150;
        position.y = 150;
        downMovementCount = 28;
        currentFrameIndex = 0; // Reset animation frame
        isEdible = false; // Ensure the ghost starts as not edible
        maxDownMovementCount = 28;
    }


}


