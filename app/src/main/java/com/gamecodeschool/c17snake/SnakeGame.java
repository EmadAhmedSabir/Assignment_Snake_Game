package com.gamecodeschool.c17snake;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.io.IOException;

class SnakeGame extends SurfaceView implements Runnable {

    // Objects for the game loop/thread
    private static Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;
    // Is the game currently playing and or paused?
    private static volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;

    // for playing sound effects
    private final SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 15;
    private final int mNumBlocksHigh;
    private int speed = 30;
    private final int blockSize;

    // How many points does the player have
    private int mScore;
    private int mHighScore;

    // Objects for drawing
    private Canvas mCanvas;
    private final SurfaceHolder mSurfaceHolder;
    private final Paint mPaint;

    // Game objects
    private final Snake mSnake;
    private final Apple mApple;
    private final Ghost mGhost;

    private final Context mContext;

    TextView SnakeActivity_btnPauseOrResume;

    private TextView mTxtScore;
    private TextView mTxtHighScore;

    // Background image
    private Bitmap mBackgroundBitmap;

    public SnakeGame(Context context, Point size, TextView txtScore, TextView txtHighScore) {
        super(context);

        this.mContext = context;
        mTxtScore = txtScore;
        mTxtHighScore = txtHighScore;

        // Work out how many pixels each block is
        this.blockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        this.mNumBlocksHigh = size.y / this.blockSize;

        // Adjust the game area to be slightly smaller than the actual size of the game area
        int adjustedLeft = (int)(NUM_BLOCKS_WIDE * 0.10);
        int adjustedTop = (int)(mNumBlocksHigh * 0.17);
        int adjustedRight = (int)(NUM_BLOCKS_WIDE * 0.945);
        int adjustedBottom = (int)(mNumBlocksHigh * 0.95);
        Point adjustedMoveRange = new Point(adjustedRight - adjustedLeft, adjustedBottom - adjustedTop);

        // Initialize the SoundPool
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        mSP = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the sounds in memory
            descriptor = assetManager.openFd("get_apple.ogg");
            mEat_ID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSP.load(descriptor, 0);

        } catch (IOException e) {
            // Error
        }

        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        // Initialize game objects
        mApple = new Apple(context,
                adjustedMoveRange,
                blockSize);

        mSnake = new Snake(context,
                adjustedMoveRange,
                blockSize);
        mSnake.reset(size.x, size.y); // Call reset() before accessing getHeadPosition()

        // Pass the snake's head position to the Ghost constructor
        mGhost = new Ghost(context, blockSize, speed, new Rect(0, 0, size.x, size.y), mSnake.getHeadPosition());

        // Load the background image
        try {
            Bitmap originalBitmap = BitmapFactory.decodeStream(context.getAssets().open("background.png"));
            mBackgroundBitmap = getScaledBitmap(originalBitmap, size.x, size.y);
        } catch (IOException e) {
            // Handle the exception
            e.printStackTrace();
        }
    }

    private Bitmap getScaledBitmap(Bitmap originalBitmap, int newWidth, int newHeight) {
        float originalAspectRatio = (float) originalBitmap.getWidth() / originalBitmap.getHeight();
        float newAspectRatio = (float) newWidth / newHeight;

        int scaledWidth, scaledHeight;

        if (newAspectRatio > originalAspectRatio) {
            // Stretch the image to fit the height of the game area
            scaledHeight = newHeight;
            scaledWidth = (int) (newHeight * originalAspectRatio);
        } else {
            // Stretch the image to fit the width of the game area
            scaledWidth = newWidth;
            scaledHeight = (int) (newWidth / originalAspectRatio);
            // Adjust the scaledHeight to be slightly smaller
            scaledHeight = (int) (scaledHeight * 0.95); // Adjust this value to your preference
        }

        return Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true);
    }

    boolean playOrNot = false;

    // Handles the game loop
    @Override
    public void run() {
        while (mPlaying) {
            if (!mPaused) {
                // Update 10 times a second
                if (updateRequired()) {
                    update();
                }
            }

            draw();
        }
    }

    public boolean updateRequired() {
        final long TARGET_FPS = 10;
        final long MILLIS_PER_SECOND = 1000;

        if (mNextFrameTime <= System.currentTimeMillis()) {
            mNextFrameTime = System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;
            return true;
        }

        return false;
    }

    public void update() {
        mSnake.move();
        eatApple();
        snakeDeath();
        mGhost.update();
    }

    private void eatApple() {
        int newColor = Color.BLUE;
        if (mSnake.checkDinner(mApple.getLocation())) {
            // Overloading with parameter
            mApple.spawn(newColor);
            mScore++;
            if (mHighScore < mScore) {
                mHighScore = mScore;
            }
            mSP.play(mEat_ID, 1, 1, 0, 0, 1);

            // Update the score and high score TextViews
            mTxtScore.post(() -> mTxtScore.setText("Score: " + mScore));
            mTxtHighScore.post(() -> mTxtHighScore.setText("High Score: " + mHighScore));
        }
    }

    private void snakeDeath() {
        if (mSnake.detectDeath() || mGhost.detectCollision(mSnake.getHeadBounds())) {
            mSP.play(mCrashID, 1, 1, 0, 0, 1);
            if (SnakeActivity_btnPauseOrResume != null) {
                SnakeActivity_btnPauseOrResume.setVisibility(INVISIBLE);
            }
            mPaused = true;
            mScore = 0;

            // Update the score TextView
            mTxtScore.post(() -> mTxtScore.setText("Score: " + mScore));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            if (mPaused) {
                mPaused = false;

                StartNewGame.newGame(mSnake, mScore, playOrNot, NUM_BLOCKS_WIDE, mNumBlocksHigh, mApple, mNextFrameTime);
                SnakeActivity_btnPauseOrResume = SnakeActivity.btnPauseOrResume;

                if (SnakeActivity_btnPauseOrResume != null) {
                    SnakeActivity_btnPauseOrResume.setVisibility(VISIBLE);

                    SnakeActivity_btnPauseOrResume.setOnClickListener(v -> {
                        playOrNot = !playOrNot;
                        if (playOrNot) {
                            pause();
                            SnakeActivity_btnPauseOrResume.setText("Resume");
                        } else {
                            resume();
                            SnakeActivity_btnPauseOrResume.setText("Pause");
                        }
                    });
                }
                // Don't want to process snake direction for this tap
                return true;
            }

            // Let the Snake class handle the input
            mSnake.switchHeading(motionEvent);
        }
        return true;
    }

    // Draw the game objects on the canvas
    private void draw() {
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();

            // Draw the background
            drawBackground(mCanvas);

            // Draw the apple, snake, and ghost
            mApple.draw(mCanvas, mPaint);
            mSnake.draw(mCanvas, mPaint);
            mGhost.draw(mCanvas);

            // Unlock the canvas and reveal the graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private void drawBackground(Canvas canvas) {
        // Draw the background image
        canvas.drawBitmap(mBackgroundBitmap, 0, 0, null);
    }

    // Stop the thread
    public void pause() {
        mPlaying = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    // Start the thread
    public void resume() {
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }
}