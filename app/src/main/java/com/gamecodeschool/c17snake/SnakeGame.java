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

import com.gamecodeschool.c17snake.activities.GameActivity;
import com.gamecodeschool.c17snake.activities.TapToPlayActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.io.IOException;

public class SnakeGame extends SurfaceView implements Runnable {

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
    private Ghost mGhost;

    private final Context mContext;
    private final int speed = 30;

    TextView SnakeActivity_btnPauseOrResume;

    private TextView mTxtScore;
    private final TextView mTxtHighScore;

    // Background image
    private Bitmap mBackgroundBitmap;

    private List<Ghost> ghosts;
    private final Rect gameBounds;
    private int timeSMile;

    public SnakeGame(Context context,int timeSMile, Point size, TextView txtScore, TextView txtHighScore) {
        super(context);
        this.timeSMile = timeSMile;
        this.mContext = context;
        mTxtScore = txtScore;
        mTxtHighScore = txtHighScore;
        this.gameBounds = new Rect(0, 0, size.x, size.y);

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
                .setMaxStreams(10)
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
        mGhost = new Ghost(context, blockSize, speed, new Rect(0, 0, size.x, size.y), false);

        // Load the background image
        try {
            Bitmap originalBitmap = BitmapFactory.decodeStream(context.getAssets().open("background.png"));
            mBackgroundBitmap = getScaledBitmap(originalBitmap, size.x, size.y);
        } catch (IOException e) {
            // Handle the exception
            e.printStackTrace();
        }
        ghosts = new ArrayList<>();
        ghosts.add(new Ghost(context, blockSize, speed, new Rect(0, 0, size.x, size.y), false));
        ghosts.add(new SamGhost(context, blockSize, speed, new Rect(0, 0, size.x, size.y), false));
        ghosts.add(new SamGhost(context, blockSize, speed, new Rect(0, 0, size.x, size.y), false));
        //add your guy's ghost here to the array list





        spawnRandomGhost(); // Initial spawn
    }

    private void spawnRandomGhost() {
        Random random = new Random();
        int ghostType = random.nextInt(5); // Generate a random number between 0 and 4
        switch (ghostType) {
            case 0:
                mGhost = new Ghost(mContext, blockSize, speed, gameBounds, false);
                break;
            case 1:
                mGhost = new SamGhost(mContext, blockSize, speed, gameBounds,false);
                break;
            case 2:
                mGhost = new JacobsGhost(mContext, blockSize, speed, gameBounds, false);
                break;
            case 3:
                mGhost = new EmadsGhost(mContext, blockSize, speed, gameBounds, false);
                break;
            case 4:
                mGhost = new EvasGhost(mContext, blockSize, speed, gameBounds, false);
                break;
        }
        ghosts.clear();
        ghosts.add(mGhost);
    }


    private void checkGhostEaten() {
        if (mGhost.isEdible() && mGhost.detectCollision(mSnake.getHeadBounds())) {
            mGhost.eat();
            mScore += 10;
            updateScoreDisplay();
            spawnRandomGhost();
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
        final long MILLIS_PER_SECOND = timeSMile;

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
        eatGhost();
        snakeDeath();
        for (Ghost ghost : ghosts) {
            ghost.update();
        }
        checkCollisions();
        checkGhostEaten();
    }

    private void eatApple() {
        int newColor = Color.BLUE;
        if (mSnake.checkDinner(mApple.getLocation())) {
            mApple.spawn(newColor); // Spawn a new apple
            mScore++;              // Increase the score
            updateHighScore();
            mSP.play(mEat_ID, 1, 1, 0, 0, 1); // Play eating sound
            updateScoreDisplay();

            mGhost.setIsEdible(true);        // Make the ghost edible
            mGhost.onAppleEaten();           // Trigger any special behavior or animations

            if (shouldSpawnNewGhost()) {
                spawnRandomGhost();
            }
        }
    }


    private void eatGhost() {
        if (mGhost.isEdible() && mSnake.checkDinner(mGhost.getGhostPosition())) {
            int newColor = Color.BLUE;
            mApple.spawn(newColor);  // This might be unnecessary here unless you want to respawn the apple whenever a ghost is eaten

            mScore++;               // Increase the score
            updateHighScore();
            mSP.play(mEat_ID, 1, 1, 0, 0, 1); // Play eating sound
            updateScoreDisplay();

            mGhost.eat();           // Method to set ghost as eaten which might also make it non-edible and initiate other changes

            if (shouldSpawnNewGhost()) {
                spawnRandomGhost();
            }
        }
    }


    private void updateHighScore() {
        if (mHighScore < mScore) {
            mHighScore = mScore;
        }
    }
    private void updateScoreDisplay() {
        mTxtScore.post(() -> mTxtScore.setText("Score: " + mScore));
        mTxtHighScore.post(() -> mTxtHighScore.setText("HiScore: " + mHighScore));
    }





    private void snakeDeath() {
        if (mSnake.detectDeath()) {
            mSP.play(mCrashID, 1, 1, 0, 0, 1);
            if (SnakeActivity_btnPauseOrResume != null) {
                SnakeActivity_btnPauseOrResume.setVisibility(INVISIBLE);
            }
            mPaused = true;
            mScore = 0;

            // Update the score TextView
            mTxtScore.post(() -> mTxtScore.setText("Score: " + mScore));

            // Reset the ghost position to the top left corner
            mGhost.resetPosition();

            // Draw the "Tap to Start" text
            drawTapToStartText(mCanvas);
        }
        else if (mGhost.detectCollision(mSnake.getHeadBounds()) && !mGhost.isEdible()) {
            mSP.play(mCrashID, 1, 1, 0, 0, 1);  // Play the crash sound effect

            if (SnakeActivity_btnPauseOrResume != null) {
                SnakeActivity_btnPauseOrResume.setVisibility(INVISIBLE);  // Make pause/resume button invisible
            }

            mPaused = true;  // Pause the game
            mScore = 0;  // Reset the score

            // Update the score TextView to reflect the reset
            mTxtScore.post(() -> mTxtScore.setText("Score: " + mScore));

            // Reset the ghost's position to a defined start position, such as the top left corner
            mGhost.resetPosition();

            // Display the "Tap to Start" text to indicate the game can be restarted
            drawTapToStartText(mCanvas);
        }




    }

    private void drawTapToStartText(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(80);
        textPaint.setTextAlign(Paint.Align.CENTER);

        String tapToStartText = "Tap to Start";
        //float textWidth = textPaint.measureText(tapToStartText);
        float canvasWidth = canvas.getWidth();
        float canvasHeight = canvas.getHeight();

        canvas.drawText(tapToStartText, canvasWidth / 2, canvasHeight / 2 + (textPaint.descent() + textPaint.ascent()) / 2, textPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            if (mPaused) {
                mPaused = false;

                StartNewGame.newGame(mSnake, mScore, playOrNot, NUM_BLOCKS_WIDE, mNumBlocksHigh, mApple, mNextFrameTime);
                SnakeActivity_btnPauseOrResume = GameActivity.btnPauseOrResume;

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
            drawBackground(mCanvas);
            mApple.draw(mCanvas, mPaint);
            mSnake.draw(mCanvas, mPaint);
            if (!mGhost.isEaten()) {
                mGhost.draw(mCanvas);  // Only draw the ghost if it hasn't been eaten
            }
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private boolean shouldSpawnNewGhost() {
        // Define conditions under which a new ghost should spawn
        return false; // Implement your condition
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
    private void checkCollisions() {
        if (mGhost.detectCollision(mSnake.getHeadBounds())) {
            if (mGhost.isEdible()) {
                mGhost.eat();
                spawnRandomGhost();
                mScore++;
            } else {
                snakeDeath();
            }
        }
    }

}