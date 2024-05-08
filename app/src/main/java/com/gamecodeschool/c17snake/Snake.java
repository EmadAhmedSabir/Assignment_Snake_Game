package com.gamecodeschool.c17snake;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import java.util.ArrayList;
import android.os.Handler;

class Snake implements Drawable, Movable {

    private final ArrayList<Point> segmentLocations;
    private final int segmentSize;
    private final Point moveRange;
    private int halfWayPoint;
    private enum Heading {
        UP, RIGHT, DOWN, LEFT
    }

    private Heading heading = Heading.RIGHT;
    private Bitmap headBitmapRight;
    private Bitmap headBitmapLeft;
    private Bitmap headBitmapUp;
    private Bitmap headBitmapDown;
    private Bitmap bodyBitmap;
    private final int[] animationSequence = {R.drawable.pacman1, R.drawable.pacman2, R.drawable.pacman3};
    private int currentAnimationFrame = 0; // Index of the current frame in the animation sequence
    private Context context; // Context reference for resource loading
    private static final long ANIMATION_DELAY_MS = 400; //delay? doesn't do anything
    private Handler animationHandler;
    Snake(Context context, Point moveRange, int segmentSize) {
        segmentLocations = new ArrayList<>();
        this.segmentSize = segmentSize;
        this.moveRange = moveRange;
        this.context = context;

        // Load the head bitmaps
        loadHeadBitmap(animationSequence[currentAnimationFrame]);

        // Load and process the body bitmap
        bodyBitmap(context, segmentSize);

        // Calculate the halfway point
        calculateHalfWayPoint(moveRange, segmentSize);
        animationHandler = new Handler();
        startAnimation();
    }

    private void loadHeadBitmap(int resId) {
        Bitmap headBitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        // Process the bitmap as needed (e.g., resize, rotate, change color)
        headBitmap = processHeadBitmap(headBitmap, segmentSize);
        // Update all head bitmaps with the loaded bitmap
        headBitmapRight = headBitmap;
        headBitmapLeft = flipBitmap(headBitmap);
        headBitmapUp = rotateBitmap(headBitmap, -90);
        headBitmapDown = rotateBitmap(headBitmap, 90);
    }

    private Bitmap processHeadBitmap(Bitmap bitmap, int segmentSize) {
        return Bitmap.createScaledBitmap(bitmap, segmentSize, segmentSize, false);
    }

    private Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.preRotate(degrees); // Rotate by the specified degrees
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    private void startAnimation() {
        // Schedule the animation to update at a fixed delay
        animationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Update the head bitmap for animation
                updateHeadBitmap();
                // Restart the animation loop
                startAnimation();
            }
        }, ANIMATION_DELAY_MS);
    }


    private Bitmap flipBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void bodyBitmap(Context context, int segmentSize) {
        // Create a circular bitmap with a yellow color for the body
        bodyBitmap = Bitmap.createBitmap(segmentSize, segmentSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bodyBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        canvas.drawCircle(segmentSize / 2f, segmentSize / 2f, segmentSize / 2f, paint);
    }

    private void calculateHalfWayPoint(Point moveRange, int segmentSize) {
        halfWayPoint = moveRange.x * segmentSize / 2;
    }

    void reset(int width, int height) {
        heading = Heading.RIGHT;
        segmentLocations.clear();
        segmentLocations.add(new Point(width / 2, height / 2));
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (!segmentLocations.isEmpty()) {
            Bitmap headBitmap = headBitmapRight;
            switch (heading) {
                case RIGHT:
                    headBitmap = headBitmapRight;
                    break;
                case LEFT:
                    headBitmap = headBitmapLeft;
                    break;
                case UP:
                    headBitmap = headBitmapUp;
                    break;
                case DOWN:
                    headBitmap = headBitmapDown;
                    break;
            }

            drawHead(canvas, paint, headBitmap, 0);

            for (int i = 1; i < segmentLocations.size(); i++) {
                // Draw the circular yellow body bitmap
                canvas.drawBitmap(bodyBitmap,
                        segmentLocations.get(i).x * segmentSize,
                        segmentLocations.get(i).y * segmentSize, paint);
            }
        }
    }

    private void drawHead(Canvas canvas, Paint paint, Bitmap bitmap, int segmentIndex) {
        if (segmentIndex < segmentLocations.size()) {
            Point segment = segmentLocations.get(segmentIndex);
            int x = segment.x * segmentSize;
            int y = segment.y * segmentSize;
            canvas.drawBitmap(bitmap, x, y, paint);
        }
    }

    @Override
    public void move() {
        for (int i = segmentLocations.size() - 1; i > 0; i--) {
            segmentLocations.get(i).x = segmentLocations.get(i - 1).x;
            segmentLocations.get(i).y = segmentLocations.get(i - 1).y;
        }
        Point p = segmentLocations.get(0);
        switch (heading) {
            case UP:
                p.y--;
                break;
            case RIGHT:
                p.x++;
                break;
            case DOWN:
                p.y++;
                break;
            case LEFT:
                p.x--;
                break;
        }

        // Update the head bitmap for animation
        updateHeadBitmap();
    }

    private void updateHeadBitmap() {
        currentAnimationFrame = (currentAnimationFrame + 1) % animationSequence.length;
        loadHeadBitmap(animationSequence[currentAnimationFrame]);
    }

    boolean detectDeath() {
        if (segmentLocations.get(0).x < 0 || segmentLocations.get(0).x >= moveRange.x ||
                segmentLocations.get(0).y < 0 || segmentLocations.get(0).y >= moveRange.y) {
            return true;
        }

        Point head = segmentLocations.get(0);
        for (int i = 1; i < segmentLocations.size(); i++) {
            if (head.equals(segmentLocations.get(i))) {
                return true;
            }
        }
        return false;
    }

    boolean checkDinner(Point location) {
        if (segmentLocations.get(0).equals(location)) {
            segmentLocations.add(new Point(-10, -10));
            return true;
        }
        return false;
    }

    void switchHeading(MotionEvent motionEvent) {
        if (motionEvent.getX() >= halfWayPoint) {
            clockwise();
        } else {
            counterClockwise();
        }
    }

    private void clockwise() {
        switch (heading) {
            case UP:
                heading = Heading.RIGHT;
                break;
            case RIGHT:
                heading = Heading.DOWN;
                break;
            case DOWN:
                heading = Heading.LEFT;
                break;
            case LEFT:
                heading = Heading.UP;
                break;
        }
    }

    private void counterClockwise() {
        switch (heading) {
            case UP:
                heading = Heading.LEFT;
                break;
            case LEFT:
                heading = Heading.DOWN;
                break;
            case DOWN:
                heading = Heading.RIGHT;
                break;
            case RIGHT:
                heading = Heading.UP;
                break;
        }
    }

    Rect getHeadBounds() {
        // Calculate the bounds of the snake's head based on its position
        int left = segmentLocations.get(0).x * segmentSize;
        int top = segmentLocations.get(0).y * segmentSize;
        int right = left + segmentSize;
        int bottom = top + segmentSize;
        return new Rect(left, top, right, bottom);
    }
}
