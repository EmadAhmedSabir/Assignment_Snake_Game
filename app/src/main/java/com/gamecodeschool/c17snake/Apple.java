package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import java.util.Random;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

@SuppressWarnings("all")

class Apple implements Drawable {
    private AppleDecorator appleDecorator;

    private Apple(Context context, Point sr, int s) {
        // Initialize the basic Apple object
        appleDecorator = new BasicApple(context, sr, s);
    }

    public static Apple getInstance(Context context, Point sr, int s) {
        return new Apple(context, sr, s);
    }

    void spawn() {
        appleDecorator.spawn();
    }

    void spawn(int s) {
        appleDecorator.spawn(s);
    }

    void changeColor(int s) {
        appleDecorator.changeColor(s);
    }

    Point getLocation() {
        return appleDecorator.getLocation();
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        appleDecorator.draw(canvas, paint);
    }

    // Decorator interface
    private interface AppleDecorator extends Drawable {
        void spawn();
        void spawn(int s);
        void changeColor(int s);
        Point getLocation();
    }

    private class BasicApple implements AppleDecorator {
        private final Point location = new Point();
        private final Point mSpawnRange;
        private int mSize;

        private Bitmap mOriginalBitmap;
        private Bitmap mBitmapApple;

        private final Context context;
        private static final int initialX = -10;

        private BasicApple(Context context, Point sr, int s) {
            this.context = context;
            mSpawnRange = sr;
            mSize = s;
            location.x = initialX;

            mOriginalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);
            mBitmapApple = Bitmap.createScaledBitmap(mOriginalBitmap, s, s, false);
        }

        @Override
        public void spawn() {
            Random random = new Random();
            location.x = random.nextInt(mSpawnRange.x - 1) + 1;
            location.y = random.nextInt(mSpawnRange.y - 1) + 1;
        }

        @Override
        public void spawn(int s) {
            Random random = new Random();
            location.x = random.nextInt(mSpawnRange.x - 1) + 1;
            location.y = random.nextInt(mSpawnRange.y - 1) + 1;
            mBitmapApple = Bitmap.createScaledBitmap(mOriginalBitmap, mSize, mSize, false);
            changeColor(s);
        }

        @Override
        public void changeColor(int s) {
            Bitmap tempBitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(tempBitmap);
            Paint paint = new Paint();
            int newColor = s;
            PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
            paint.setColorFilter(colorFilter);

            canvas.drawBitmap(mBitmapApple, 0, 0, paint);
            mBitmapApple = tempBitmap;
        }

        @Override
        public Point getLocation() {
            return location;
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            canvas.drawBitmap(mBitmapApple, location.x * mSize, location.y * mSize, paint);
        }
    }
}