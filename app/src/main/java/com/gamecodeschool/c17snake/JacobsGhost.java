package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class JacobsGhost extends Ghost {

    private final Bitmap[] jacobsGhostImages;
    private final Bitmap[] edibleGhostImage;
    private int currentFrameIndex; // Index of the current frame in the animation sequence

    public JacobsGhost(Context context, int size, int speed, Rect gameBounds, Boolean isEdible) {
        super(context, size, speed, gameBounds, isEdible);
        this.currentFrameIndex = 0;
        // Initialize the non-edible animation frames
        jacobsGhostImages = new Bitmap[] {
                BitmapFactory.decodeResource(context.getResources(), R.drawable.jacob1),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.jacob2)
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
                currentFrameIndex = (currentFrameIndex + 1) % jacobsGhostImages.length;
                lastFrameTime = currentTime;
            }
            canvas.drawBitmap(jacobsGhostImages[currentFrameIndex], position.x, position.y, paint);
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


