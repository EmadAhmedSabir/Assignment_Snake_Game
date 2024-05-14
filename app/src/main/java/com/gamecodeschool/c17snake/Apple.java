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
    private static Apple instance = null;

    private final Point location = new Point();
    private final Point mSpawnRange;
    private int mSize;

    private Bitmap mOriginalBitmap;
    private Bitmap mBitmapApple;

    private final Context context;
    private static final int initialX = -10;

    private Apple(Context context, Point sr, int s) {
        this.context = context;
        mSpawnRange = sr;
        mSize = s;
        location.x = initialX;

        mOriginalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);
        mBitmapApple = Bitmap.createScaledBitmap(mOriginalBitmap, s, s, false);
    }

    public static synchronized Apple getInstance(Context context, Point sr, int s) {
        if (instance == null) {
            instance = new Apple(context, sr, s);
        }
        return instance;
    }

    void spawn() {
        Random random = new Random();
        location.x = random.nextInt(mSpawnRange.x - 1) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    void spawn(int s) {
        Random random = new Random();
        location.x = random.nextInt(mSpawnRange.x - 1) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
        mBitmapApple = Bitmap.createScaledBitmap(mOriginalBitmap, mSize, mSize, false);
        changeColor(s);
    }

    void changeColor(int s) {
        Bitmap tempBitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tempBitmap);
        Paint paint = new Paint();
        int newColor = s;
        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
        paint.setColorFilter(colorFilter);

        canvas.drawBitmap(mBitmapApple, 0, 0, paint);
        mBitmapApple = tempBitmap;
    }

    Point getLocation() {
        return location;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapApple, location.x * mSize, location.y * mSize, paint);
    }
}