package com.gamecodeschool.c17snake.activities;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gamecodeschool.c17snake.R;

import java.io.IOException;

public class TapToPlayActivity extends AppCompatActivity {
    private MediaPlayer mBackgroundMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_to_play);

        // Hide the action bar
        getSupportActionBar().hide();

        // Start background music
        mBackgroundMusic = new MediaPlayer();
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor = assetManager.openFd("game_music.ogg");
            mBackgroundMusic.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            mBackgroundMusic.setLooping(true); // Loop the background music
            mBackgroundMusic.prepare();
            // Increase volume
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mBackgroundMusic.setVolume(volume, volume);
            mBackgroundMusic.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start LevelsActivity when the button is clicked
        findViewById(R.id.tapToPlayBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TapToPlayActivity.this, LevelsActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer when the activity is destroyed
        if (mBackgroundMusic != null) {
            mBackgroundMusic.release();
        }
    }
}
