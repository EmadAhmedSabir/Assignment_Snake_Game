package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

import java.util.ArrayList;

class Snake implements Drawable, Movable {

    private final ArrayList<Point> segmentLocations;
    private final int mSegmentSize;
    private final Point mMoveRange;
    private int halfWayPoint;
    private enum Heading {
        UP, RIGHT, DOWN, LEFT
    }

    private Heading heading = Heading.RIGHT;
    private Bitmap mBitmapHeadRight;
    private Bitmap mBitmapHeadLeft;
    private Bitmap mBitmapHeadUp;
    private Bitmap mBitmapHeadDown;
    private Bitmap mBitmapBody;

    Snake(Context context, Point mr, int ss) {

        segmentLocations = new ArrayList<>();

        mSegmentSize = ss;
        mMoveRange = mr;
        Bitmap mBitmapHead = BitmapFactory.decodeResource(context.getResources(), R.drawable.head);
        headBitmaps(mBitmapHead, ss);
        bodyBitmap(context, ss);
        calculateHalfWayPoint(mr, ss);
    }

    private Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.preRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void headBitmaps(Bitmap mBitmapHead, int ss) {
        mBitmapHeadRight = Bitmap.createScaledBitmap(mBitmapHead, ss, ss, false);
        mBitmapHeadLeft = BitmapHorizontally(mBitmapHeadRight);
        mBitmapHeadUp = rotateBitmap(mBitmapHeadRight, -90);
        mBitmapHeadDown = rotateBitmap(mBitmapHeadRight, 180);
    }

    private void bodyBitmap(Context context, int ss) {
        mBitmapBody = BitmapFactory.decodeResource(context.getResources(), R.drawable.body);
        mBitmapBody = Bitmap.createScaledBitmap(mBitmapBody, ss, ss, false);
    }

    private Bitmap BitmapHorizontally(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void calculateHalfWayPoint(Point mr, int ss) {
        halfWayPoint = mr.x * ss / 2;
    }

    void reset(int w, int h) {

        heading = Heading.RIGHT;

        segmentLocations.clear();

        segmentLocations.add(new Point(w / 2, h / 2));
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (!segmentLocations.isEmpty()) {
            Bitmap headBitmap = mBitmapHeadRight;
            switch (heading) {
                case RIGHT:
                    headBitmap = mBitmapHeadRight;
                    break;
                case LEFT:
                    headBitmap = mBitmapHeadLeft;
                    break;
                case UP:
                    headBitmap = mBitmapHeadUp;
                    break;
                case DOWN:
                    headBitmap = mBitmapHeadDown;
                    break;
            }

            drawingHead(canvas, paint, headBitmap, 0);

            for (int i = 1; i < segmentLocations.size(); i++) {
                canvas.drawBitmap(mBitmapBody,
                        segmentLocations.get(i).x * mSegmentSize,
                        segmentLocations.get(i).y * mSegmentSize, paint);
            }
        }
    }

    private void drawingHead(Canvas canvas, Paint paint, Bitmap bitmap, int segmentIndex) {
        if (segmentIndex < segmentLocations.size()) {
            Point segment = segmentLocations.get(segmentIndex);
            int x = segment.x * mSegmentSize;
            int y = segment.y * mSegmentSize;
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
    }

    boolean detectDeath() {
        if (segmentLocations.get(0).x == -1 || segmentLocations.get(0).x > mMoveRange.x ||
                segmentLocations.get(0).y == -1 || segmentLocations.get(0).y > mMoveRange.y) {
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

    boolean checkDinner(Point l) {
        if (segmentLocations.get(0).x == l.x &&
                segmentLocations.get(0).y == l.y) {
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
}
