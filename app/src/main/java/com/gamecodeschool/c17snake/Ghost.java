
package com.gamecodeschool.c17snake;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import java.util.Random;
import android.graphics.Rect;


public class Ghost {

    private final int SIZE;
    private final int SPEED;
    private final Point position;
    private final Random random;
    private final Rect bounds;
    private final Paint paint;

    public Ghost(Context context, int size, int speed, Rect gameBounds) {
        SIZE = size;
        SPEED = speed;
        bounds = gameBounds;
        position = new Point();
        paint = new Paint();
        paint.setColor(Color.RED);
        random = new Random();
        resetPosition();
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(position.x, position.y, position.x + SIZE, position.y + SIZE, paint);
    }

    public void update() {
        move();
        checkBoundaries();
    }

    private void move() {
        int dx = random.nextInt(3) - 1;
        int dy = random.nextInt(3) - 1;

        position.x += dx * SPEED;
        position.y += dy * SPEED;
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
        position.x = random.nextInt(bounds.right - SIZE);
        position.y = random.nextInt(bounds.bottom - SIZE);
    }
}
