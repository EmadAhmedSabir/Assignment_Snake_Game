package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import java.util.Random;

public class Ghost {
    private final int SIZE;
    private final int SPEED;
    private Point position;
    private final Point snakePosition;
    private final Rect bounds;
    private final Paint paint;
    private boolean isFollowing;
    private int currentColor;

    public Ghost(Context context, int size, int speed, Rect gameBounds, Point snakePosition) {
        SIZE = size;
        SPEED = speed;
        bounds = gameBounds;
        position = new Point();
        this.snakePosition = snakePosition;
        paint = new Paint();
        resetPosition();
        setRandomColor();
    }

    private void setRandomColor() {
        currentColor = Color.rgb(
                (int)(Math.random() * 256),
                (int)(Math.random() * 256),
                (int)(Math.random() * 256)
        );
        paint.setColor(currentColor);
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(position.x, position.y, position.x + SIZE, position.y + SIZE, paint);
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
            // Move the ghost randomly
            int dx = new Random().nextInt(3) - 1;
            int dy = new Random().nextInt(3) - 1;
            position.x += dx * SPEED;
            position.y += dy * SPEED;

            // Check if the ghost is close enough to the snake to start following
            if (Math.abs(position.x - snakePosition.x) <= 3 * SIZE && Math.abs(position.y - snakePosition.y) <= 3 * SIZE) {
                isFollowing = true;
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

    public Point getPosition() {
        return position;
    }

    public void resetPosition() {
        position.x = new Random().nextInt(bounds.right - SIZE);
        position.y = new Random().nextInt(bounds.bottom - SIZE);
        isFollowing = false;
    }

    public boolean detectCollision(Rect snakeRect) {
        return getBounds().intersect(snakeRect);
    }

    public void onAppleEaten() {
        setRandomColor();
    }
}