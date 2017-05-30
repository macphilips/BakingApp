package com.rmhub.bakingapp.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.util.Util;
import com.rmhub.bakingapp.PlayerTest;
import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Step;
import com.rmhub.bakingapp.ui.views.PlaybackControlView;
import com.rmhub.bakingapp.ui.views.PlayerEventHandler;
import com.rmhub.bakingapp.ui.views.SimpleExoPlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MOROLANI on 5/28/2017
 * <p>
 * owm
 * .
 */

public class RecipeDetailFragment extends Fragment {
    private static final String STEP = "recipe";
    private static final int PICK_PHOTO_ACTIVITY_REQUEST_CODE = 4;
    public static RecipeDetailFragment newInstance(Step item) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(RecipeDetailFragment.STEP, item);
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }
    private static final String TAG = PlayerTest.class.getSimpleName();
    @BindView(R.id.video_player)
    SimpleExoPlayerView playerView;
    @BindView(R.id.steps_desc)
    TextView mStepDesc;
    private OnFragmentInteraction mCallBack;
    @Nullable
    private PlayerEventHandler mHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_pager_item, container, false);
        ButterKnife.bind(this, rootView);
        Step item = getArguments().getParcelable(STEP);
        if (item != null) {
            mStepDesc.setText(item.getDescription());
            setupPlayer(item);
        }
        return rootView;
    }

    private void setupPlayer(Step item) {
        mHandler = new PlayerEventHandler(getActivity(), playerView, Uri.parse(item.getVideoURL()));

        //  mHandler = new PlayerEventHandler(getActivity(), playerView, null);
        playerView.setControlDispatcher(
                new PlaybackControlView.ControlDispatcher() {

                    @Override
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
                    }

                    @Override
                    public boolean dispatchSetPlayWhenReady(ExoPlayer player, boolean playWhenReady) {
                        player.setPlayWhenReady(playWhenReady);
                        keepWakeLock(playWhenReady);
                        return true;
                    }

                    @Override
                    public boolean dispatchSeekTo(ExoPlayer player, int windowIndex, long positionMs) {
                        player.seekTo(windowIndex, positionMs);
                        return true;
                    }

                }
        );
    }

    void keepWakeLock(boolean keepAwake) {
        if (keepAwake) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            Toast.makeText(getActivity(), "Play", Toast.LENGTH_LONG).show();
        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            Toast.makeText(getActivity(), "Pause", Toast.LENGTH_LONG).show();
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

    void setScreenMode(boolean fullscreen) {
        LinearLayout.LayoutParams mLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        if (!fullscreen) {
            mLayoutParam.weight = 0.5f;
            mStepDesc.setVisibility(View.VISIBLE);
        } else {
            mLayoutParam.weight = 1.0f;
            mStepDesc.setVisibility(View.GONE);
        }
        playerView.setLayoutParams(mLayoutParam);
        playerView.getController().updateFullScreenControlButton(fullscreen);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallBack = (OnFragmentInteraction) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    interface OnFragmentInteraction {
        void setFullMode(boolean fullMode);
    }
}
