package com.rmhub.bakingapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class PlayerTest extends AppCompatActivity {

    private static final int PICK_PHOTO_ACTIVITY_REQUEST_CODE = 4;

    private static final String TAG = PlayerTest.class.getSimpleName();

    @BindView(R.id.video_player)
    SimpleExoPlayerView playerView;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.test_layout_steps_desc)
    TextView mStepDesc;
    @BindView(R.id.picker_button)
    View button;
    @Nullable
    private PlayerEventHandler mHandler;


    void setFullMode(boolean fullscreen) {
        LinearLayout.LayoutParams mLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        if (!fullscreen) {
            mLayoutParam.weight = 0.5f;
            mStepDesc.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        } else {
            mLayoutParam.weight = 1.0f;
            mStepDesc.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        }
        playerView.setLayoutParams(mLayoutParam);
        // playerView.getController().updateFullScreenControlButton(fullscreen);
        toggleHideyBar(fullscreen);
    }

    void keepWakeLock(boolean keepAwake) {
        if (keepAwake) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            Toast.makeText(this, "Play", Toast.LENGTH_LONG).show();
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            Toast.makeText(this, "Pause", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_test);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        mHandler = new PlayerEventHandler(this, playerView);
        mHandler.addPlayerListener(new PlayerEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                keepWakeLock(playWhenReady);
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                View view = LayoutInflater.from(PlayerTest.this).inflate(R.layout.retry_layout, null);
                playerView.getOverlayFrameLayout().removeAllViews();
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.gravity = Gravity.CENTER;
                view.setLayoutParams(layoutParams);
                view.findViewById(R.id.retry_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHandler.initializePlayer();
                    }
                });
                playerView.getOverlayFrameLayout().addView(view);

            }
        });


    }

    public void changeScreenMode(boolean fullscreen) {
        int orientation = getResources().getConfiguration().orientation;
        if (fullscreen) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } else {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    @OnClick(R.id.picker_button)
    @Optional
    void picker() {
        if (Build.VERSION.SDK_INT < 19) {
            final Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/* video/*");
            startActivityForResult(photoPickerIntent, PICK_PHOTO_ACTIVITY_REQUEST_CODE);
        } else {
            final Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("*/*");
            photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
            startActivityForResult(photoPickerIntent, PICK_PHOTO_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            if (mHandler != null) {
                mHandler.initializePlayer();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23)) {
            if (mHandler != null) {
                mHandler.initializePlayer();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            if (mHandler != null) {
                mHandler.releasePlayer();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            if (mHandler != null) {
                mHandler.releasePlayer();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setFullMode(false);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setFullMode(true);
        }
        if (isAutoRotateOn())
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (mHandler != null) {
                mHandler.play(data.getData());
                keepWakeLock(true);
            }
        }
    }

    public void toggleHideyBar(boolean b) {
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
            Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (b) {
            if (actionBar != null && actionBar.isShowing()) {
                actionBar.hide();
            }
        } else {
            if (actionBar != null && !actionBar.isShowing()) {
                actionBar.show();
            }
        }
    }

    boolean isAutoRotateOn() {
        return (android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1);
    }
}
