package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Color;


public class Ghost {
    private final int SIZE;
    private int SPEED;
    protected Point position;
    private final Rect bounds;
    protected final Paint paint;
    protected boolean isEdible;
    protected int downMovementCount;
    private final Bitmap[] edibleGhostImages;
    protected int currentFrameIndex;
    protected long lastFrameTime;
    protected static final long FRAME_UPDATE_TIME = 500;
    protected boolean isEaten;
    protected int maxDownMovementCount;

    public boolean isEaten() {
        return isEaten;
    }


    public Ghost(Context context, int size, int speed, Rect gameBounds , Boolean isEdible) {
        SIZE = size;
        SPEED = speed;
        bounds = gameBounds;
        position = new Point();
        paint = new Paint();
        paint.setColor(Color.RED);
        resetPosition();
        this.isEdible = isEdible;
        edibleGhostImages = new Bitmap[] {
                BitmapFactory.decodeResource(context.getResources(), R.drawable.blue),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.blue2)
        };
        lastFrameTime = System.currentTimeMillis();

    }
    public boolean isEdible(){
        return isEdible;
    }
    public void setIsEdible(boolean edible) {
        isEdible = edible;
    }

    public void draw(Canvas canvas) {
        if (isEdible) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFrameTime > FRAME_UPDATE_TIME) {
                currentFrameIndex = (currentFrameIndex + 1) % edibleGhostImages.length;
                lastFrameTime = currentTime;
            }
            canvas.drawBitmap(edibleGhostImages[currentFrameIndex], position.x, position.y, paint);
        } else {
            // Draw the ghost as a simple black square when not edible
            canvas.drawRect(position.x, position.y, position.x + SIZE, position.y + SIZE, paint);
        }
    }


    public void update() {
        if (!isEdible) { // Only update movement if not edible
            move();
        }
        checkBoundaries();
    }
    public Point getGhostPosition() {
        return new Point(position);
    }
    private void move() {
        maxDownMovementCount = 12;
        if (downMovementCount < maxDownMovementCount) {
            // Move the ghost downwards for a randomly determined number of steps (8 to 12)
            position.y += SPEED;
            downMovementCount++;
        } else {
            // Check horizontal boundaries to change direction
            if (position.x <= bounds.left || position.x + SIZE >= bounds.right) {
                SPEED = -SPEED;  // Reverse the direction of movement
            }

            // Move the ghost horizontally
            position.x += SPEED;

            // After moving downwards the required number of times, adjust the direction horizontally
            if (downMovementCount == maxDownMovementCount) {
                if (position.x < bounds.width() / 2) {
                    SPEED = Math.abs(SPEED);  // Ensure speed is positive, moving right
                } else {
                    SPEED = -Math.abs(SPEED);  // Ensure speed is negative, moving left
                }
                downMovementCount++;  // Increment to prevent reinitializing SPEED
            }
        }
    }


    private void checkBoundaries() {
        if (position.x < bounds.left) {
            position.x = bounds.left;
        } else if (position.x + SIZE > bounds.right) {
            position.x = bounds.right - SIZE;
        }

        if (position.y < bounds.top) {
            position.y = bounds.top;
        } else if (position.y + SIZE > bounds.bottom) {
            position.y = bounds.bottom - SIZE;
        }
    }

    public Rect getBounds() {
        return new Rect(position.x, position.y, position.x + SIZE, position.y + SIZE);
    }

    public void resetPosition() {
        position.x = 150;
        position.y = 150;
        downMovementCount = 0;
        currentFrameIndex = 0; // Reset animation frame
        isEdible = false; // Ensure the ghost starts as not edible
    }



    public boolean detectCollision(Rect snakeRect) {
        // Detect collision based on bounding rectangles only
        return getBounds().intersect(snakeRect);
    }

    public void onAppleEaten() {
        isEdible = true;
        currentFrameIndex = 0; // Start animation from the first frame

    }
    public void eat() {
        isEaten = true;  // Set the ghost as eaten
        isEdible = true;  // Once eaten, it should no longer be edible
    }
}

