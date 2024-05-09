package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import java.util.Random;
import android.graphics.Color;


public class Ghost {
    private final int SIZE;
    private final int SPEED;
    private Point position;
    private final Point snakePosition;
    private final Rect bounds;
    private final Paint paint;
    private boolean isFollowing;
    private boolean isEdible;
    private int downMovementCount;
    private boolean hasExitedBox;
    private Bitmap[] edibleGhostImages;
    private int currentFrameIndex;
    private long lastFrameTime;
    private static final long FRAME_UPDATE_TIME = 500;


    public Ghost(Context context, int size, int speed, Rect gameBounds, Point snakePosition) {
        SIZE = size;
        SPEED = speed;
        bounds = gameBounds;
        position = new Point();
        this.snakePosition = snakePosition;
        paint = new Paint();
        paint.setColor(Color.RED);
        resetPosition();
        isEdible = false;
        edibleGhostImages = new Bitmap[] {
                BitmapFactory.decodeResource(context.getResources(), R.drawable.blue),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.blue2)
        };
        lastFrameTime = System.currentTimeMillis();

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
        move();
        checkBoundaries();
    }

    private void move() {
        if (isFollowing) {
            // Calculate the direction to move towards the snake
            int dx = Integer.compare(snakePosition.x, position.x);
            int dy = Integer.compare(snakePosition.y, position.y);

            // Move the ghost towards the snake
            position.x += dx * SPEED;
            position.y += dy * SPEED;
        } else {
            if (downMovementCount < 12) {
                // Move the ghost downwards
                position.y += SPEED;
                downMovementCount++;
            } else {
                // Move the ghost randomly
                int dx = new Random().nextInt(3) - 1;
                int dy = new Random().nextInt(3) - 1;
                position.x += dx * SPEED;
                position.y += dy * SPEED;

                // Check if the ghost is close enough to the snake to start following
                if (Math.abs(position.x - snakePosition.x) <= 3 * SIZE && Math.abs(position.y - snakePosition.y) <= 3 * SIZE) {
                    isFollowing = true;
                    hasExitedBox = true;
                }
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
        isFollowing = false;
        downMovementCount = 0;
        hasExitedBox = false;
        currentFrameIndex = 0; // Reset animation frame
        isEdible = false; // Ensure the ghost starts as not edible
    }

    public boolean detectCollision(Rect snakeRect) {
        return getBounds().intersect(snakeRect);
    }

    public void onAppleEaten() {
        isEdible = true;
        currentFrameIndex = 0; // Start animation from the first frame
    }
}
