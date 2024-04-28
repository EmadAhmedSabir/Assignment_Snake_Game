package com.gamecodeschool.c17snake;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
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

class SnakeGame extends SurfaceView implements Runnable{

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

    TextView btnPauseOrResume;

    // This is the constructor method that gets called
    // from SnakeActivity
    public SnakeGame(Context context, Point size) {
        super(context);

        this.mContext = context;

        // Work out how many pixels each block is
        this.blockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        this.mNumBlocksHigh = size.y / this.blockSize;

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
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        mGhost = new Ghost(context, blockSize, speed, new Rect(0, 0, size.x, size.y));

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
        }
    }

    private void snakeDeath() {
        if (mSnake.detectDeath() || mGhost.getBounds().intersect(mSnake.getHeadBounds())) {
            mSP.play(mCrashID, 1, 1, 0, 0, 1);
            try {
                SnakeActivity.btnPauseOrResume.setVisibility(INVISIBLE);
            } catch (Exception e) {
            }
            mPaused = true;
            mScore = 0;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            if (mPaused) {
                mPaused = false;

                StartNewGame.newGame(mSnake, mScore, playOrNot, NUM_BLOCKS_WIDE, mNumBlocksHigh, mApple, mNextFrameTime);
                btnPauseOrResume = SnakeActivity.btnPauseOrResume;

                btnPauseOrResume.setVisibility(VISIBLE);

                btnPauseOrResume.setOnClickListener(v -> {
                    playOrNot = !playOrNot;
                    if (playOrNot) {
                        pause();
                        btnPauseOrResume.setText("Resume");
                    } else {
                        resume();
                        btnPauseOrResume.setText("Pause");
                    }
                });
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

            // Fill the screen with a color
            mCanvas.drawColor(Color.parseColor("#f3953d"));

            // Draw the apple, snake, and ghost
            mApple.draw(mCanvas, mPaint);
            mSnake.draw(mCanvas, mPaint);
            mGhost.draw(mCanvas);

            // Unlock the canvas and reveal the graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
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
