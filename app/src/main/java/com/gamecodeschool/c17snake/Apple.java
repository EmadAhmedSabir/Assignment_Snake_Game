package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import java.util.Random;

class Apple implements Drawable {

    private final Point location = new Point();
    private final Point mSpawnRange;
    private final int mSize;

    private Bitmap mBitmapApple;
    private static final int initialX = -10;

    Apple(Context context, Point sr, int s){

        mSpawnRange = sr;
        mSize = s;
        location.x = initialX;

        mBitmapApple = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);

        mBitmapApple = Bitmap.createScaledBitmap(mBitmapApple, s, s, false);
    }

    void spawn(){
        Random random = new Random();
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    Point getLocation(){
        return location;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapApple,
                location.x * mSize, location.y * mSize, paint);

    }
}
