package com.rmhub.bakingapp.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.rmhub.bakingapp.PlayerEventHandler;
import com.rmhub.bakingapp.PlayerEventListener;
import com.rmhub.bakingapp.PlayerTest;
import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Step;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MOROLANI on 5/28/2017
 * <p>
 * owm
 * .
 */

public class RecipeDetailFragment extends Fragment implements View.OnClickListener {

    private static final String STEP = "recipe";
    private static final String TAG = PlayerTest.class.getSimpleName();
    @BindView(R.id.no_video_text)
    TextView noVideo;
    @BindView(R.id.exo_exit_full_screen)
    View exitFullScreenButton;
    @BindView(R.id.video_player_container)
    View container;
    @BindView(R.id.exo_fullscreen)
    View fullScreenButton;
    @BindView(R.id.video_player)
    SimpleExoPlayerView playerView;
    @BindView(R.id.steps_desc)
    TextView mStepDesc;
    private OnFragmentInteraction mCallBack;
    @Nullable
    private PlayerEventHandler mHandler;
    private boolean isFullScreen;
    private OnPlaybackComplete mComplete;

    public static RecipeDetailFragment newInstance(Step item) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(RecipeDetailFragment.STEP, item);
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_pager_item, container, false);
        ButterKnife.bind(this, rootView);
        fullScreenButton.setOnClickListener(this);
        exitFullScreenButton.setOnClickListener(this);
        Step item = getArguments().getParcelable(STEP);
        if (item != null) {
            mStepDesc.setText(item.getDescription());
            setupPlayer(item);
        }
        return rootView;
    }

    void keepWakeLock(boolean keepAwake) {
        if (keepAwake) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHandler != null) {
            mHandler.initializePlayer();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mHandler != null) {
            mHandler.releasePlayer();
        }
    }


    void setScreenMode(boolean fullscreen) {
        LinearLayout.LayoutParams mLayoutParam;
        if (!fullscreen) {
            mLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(250));
            mStepDesc.setVisibility(View.VISIBLE);
        } else {
            mLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mStepDesc.setVisibility(View.GONE);
        }
        container.setLayoutParams(mLayoutParam);
        mCallBack.setFullMode(fullscreen);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setScreenMode(false);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setScreenMode(true);
        }
        if (isAutoRotateOn())
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    boolean isAutoRotateOn() {
        return (android.provider.Settings.System.getInt(getActivity().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1);
    }

    public void changeScreenMode(boolean fullscreen) {
        int orientation = getResources().getConfiguration().orientation;
        if (fullscreen) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } else {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        isFullScreen = fullscreen;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallBack = (OnFragmentInteraction) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == fullScreenButton) {
            fullScreenButton.setVisibility(View.GONE);
            exitFullScreenButton.setVisibility(View.VISIBLE);
            changeScreenMode(true);
        } else if (v == exitFullScreenButton) {
            exitFullScreenButton.setVisibility(View.GONE);
            fullScreenButton.setVisibility(View.VISIBLE);
            changeScreenMode(false);
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void setupPlayer(Step item) {

        if (TextUtils.isEmpty(item.getVideoURL())) {
            noVideo.setVisibility(View.VISIBLE);
            return;
        }

        noVideo.setVisibility(View.GONE);
        mHandler = new PlayerEventHandler(getActivity(), playerView);
        mHandler.registerControllerToActivity(getActivity());
        mHandler.addPlayerListener(new PlayerEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (mComplete != null && playbackState == ExoPlayer.STATE_ENDED) mComplete.next();

                if (playbackState == ExoPlayer.STATE_ENDED && isFullScreen) {
                    exitFullScreenButton.performClick();
                }
                keepWakeLock(playWhenReady);
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.retry_layout, null);
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
        mHandler.play(Uri.parse(item.getVideoURL()));
    }

    public void setOnPlaybackComplete(OnPlaybackComplete mComplete) {
        this.mComplete = mComplete;
    }

    interface OnFragmentInteraction {
        void setFullMode(boolean fullMode);
    }

    interface OnPlaybackComplete {
        void prev();

        void next();
    }

}
