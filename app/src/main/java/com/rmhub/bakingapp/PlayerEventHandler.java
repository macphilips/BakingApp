package com.rmhub.bakingapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;

/**
 * Created by MOROLANI on 5/29/2017
 * <p>
 * owm
 * .
 */

public class PlayerEventHandler extends PlayerEventListener implements PlaybackControlView.ControlDispatcher {

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    private static final String TAG = "l";

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private final Context mContext;
    private final SimpleExoPlayerView mediaPlayer;
    private final ProgressBar mProgressBar;
    private AudioManager mAudioManager;
    private Handler mHandler = new Handler();
    private MediaControllerCompat mediaController;
    private Runnable mDelayedStopRunnable = new Runnable() {
        @Override
        public void run() {
            mediaController.getTransportControls().stop();
        }
    };
    private Uri linkUri;
    private Handler mainHandler;
    private DataSource.Factory mediaDataSourceFactory;
    private SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;
    private TrackGroupArray lastSeenTrackGroupArray;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private BecomingNoisyReceiver myNoisyAudioStreamReceiver = new BecomingNoisyReceiver();
    private List<PlayerEventListener> pendingListener = new ArrayList<>();
    private float currentVolume = 0.5f;
    private int resumeWindow;
    private long resumePosition;
    private boolean isPlaying;

    private AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // Permanent loss of audio focus
                // Pause playback immediately
                mediaController.getTransportControls().pause();
                // Wait 30 seconds before stopping playback
                mHandler.postDelayed(mDelayedStopRunnable,
                        TimeUnit.SECONDS.toMillis(30));
            } else if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
                shouldPlayMedia(false);
            } else if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // Lower the volume, keep playing
                currentVolume = player.getVolume();
                player.setVolume(0.2f);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Your app has been granted audio focus again
                // Raise volume to normal, restart playback if necessary
                player.setVolume(currentVolume);
            }
        }
    };
    private boolean needRetrySource;

    public PlayerEventHandler(Context mContext, SimpleExoPlayerView mediaPlayer) {
        this.mContext = mContext;
        this.mediaPlayer = mediaPlayer;
        clearResumePosition();
        mediaDataSourceFactory = buildDataSourceFactory(true);
        mainHandler = new Handler();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        mProgressBar = new ProgressBar(new ContextThemeWrapper(mContext, R.style.AppTheme_Dialog));
        mProgressBar.setIndeterminate(true);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        mProgressBar.setLayoutParams(layoutParams);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        initializeMediaSession();

        mediaPlayer.setControlDispatcher(this);
    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    public void registerControllerToActivity(Activity activity) {
        if (mediaController != null)
            MediaControllerCompat.setMediaController(activity, mediaController);
    }

    public void addPlayerListener(PlayerEventListener listener) {
        if (player != null) {
            player.addListener(listener);
        } else {
            pendingListener.add(listener);
        }
    }

    private void addPendListener() {
        for (PlayerEventListener listener : pendingListener) {
            player.addListener(listener);
        }
        pendingListener.clear();
    }

    public boolean play(Uri linkUri) {
        this.linkUri = linkUri;
        releasePlayer();
        return initializePlayer();
    }


    private void initializeMediaSession() {
        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(mContext, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
        mediaController = new MediaControllerCompat(mContext, mMediaSession);
        //    MediaControllerCompat.setMediaController(this, mediaController);
    }

    /**
     * Returns a new DataSource factory.
     *
     * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
     *                          DataSource factory.
     * @return A new DataSource factory.
     */
    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return BakingApp.getInstance().buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private void showToast(int messageId) {
        showToast(mContext.getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(BakingApp.getInstance(), message, Toast.LENGTH_LONG).show();
    }

    public boolean initializePlayer() {
        if (linkUri == null) return false;
        boolean done = false;

        boolean needNewPlayer = player == null;
        if (needNewPlayer) {
            boolean preferExtensionDecoders = false;
            @DefaultRenderersFactory.ExtensionRendererMode
            int extensionRendererMode =
                    BakingApp.getInstance().useExtensionRenderers()
                            ? (preferExtensionDecoders ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;

            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(mContext,
                    null, extensionRendererMode);

            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
            lastSeenTrackGroupArray = null;

            player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);
            player.addListener(this);
            mediaPlayer.setPlayer(player);
            shouldPlayMedia(true);
            addPendListener();
        }
        if (needNewPlayer || needRetrySource) {
            Uri[] uris = new Uri[]{linkUri};

            String[] extensions = new String[]{getExtensionFromUri(linkUri)};

            MediaSource[] mediaSources = new MediaSource[uris.length];
            for (int i = 0; i < uris.length; i++) {
                mediaSources[i] = buildMediaSource(uris[i], extensions[i]);
            }
            MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                    : new ConcatenatingMediaSource(mediaSources);
            boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
            if (haveResumePosition) {
                player.seekTo(resumeWindow, resumePosition);
            }
            player.prepare(mediaSource, !haveResumePosition, false);
            needRetrySource = false;
            done = true;
        }
        return done;
    }

    private String getExtensionFromUri(Uri linkUri) {
        String path = linkUri.getPath();
        return path.substring(path.lastIndexOf(".") + 1, path.length());
    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
                : Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(),
                        mainHandler, null);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    public void endMediaSession() {
        if (mMediaSession != null) {
            mMediaSession.setActive(false);
        }
    }

    public void pause() {

    }

    public void releasePlayer() {
        if (player != null) {
            shouldPlayMedia(false);
            updateResumePosition();
            player.release();
            player = null;
            trackSelector = null;
        }

    }

    private void updateResumePosition() {
        resumeWindow = player.getCurrentWindowIndex();
        resumePosition = player.isCurrentWindowSeekable() ? Math.max(0, player.getCurrentPosition())
                : C.TIME_UNSET;
    }

    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }

    private void shouldPlayMedia(boolean playWhenReady) {
        if (player == null) return;
        boolean shouldAutoPlay = playWhenReady;
        if (playWhenReady) {
            int result = mAudioManager.requestAudioFocus(afChangeListener,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            shouldAutoPlay = isPlaying = (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
            mContext.registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
        } else {
            if (isPlaying) {
                mAudioManager.abandonAudioFocus(afChangeListener);
                mContext.unregisterReceiver(myNoisyAudioStreamReceiver);
                isPlaying = false;
            }
        }

        player.setPlayWhenReady(shouldAutoPlay);
    }

    @Override
    @SuppressWarnings("ReferenceEquality")
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        if (trackGroups != lastSeenTrackGroupArray) {
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {
                if (mappedTrackInfo.getTrackTypeRendererSupport(C.TRACK_TYPE_VIDEO)
                        == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                    showToast(R.string.error_unsupported_video);
                }
                if (mappedTrackInfo.getTrackTypeRendererSupport(C.TRACK_TYPE_AUDIO)
                        == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                    showToast(R.string.error_unsupported_audio);
                }
            }
            lastSeenTrackGroupArray = trackGroups;
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    player.getCurrentPosition(), 1f);

        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    player.getCurrentPosition(), 1f);

        } else if (playbackState == ExoPlayer.STATE_ENDED) {
            isPlaying = false;
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());

        if (player.getPlaybackState() == ExoPlayer.STATE_BUFFERING) {
            mediaPlayer.getOverlayFrameLayout().removeAllViews();
            mediaPlayer.getOverlayFrameLayout().addView(mProgressBar);
        } else {
            mediaPlayer.getOverlayFrameLayout().removeAllViews();
        }
    }

    @Override
    public void onPositionDiscontinuity() {
        if (needRetrySource) {
            // This will only occur if the user has performed a seek whilst in the error state. Update the
            // resume position so that if the user then retries, playback will resume from the position to
            // which they seeked.
            updateResumePosition();
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        Log.e(TAG, "onPlayerError called => " + String.valueOf(e.getMessage()));
        String errorString = null;
        if (e.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = e.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = mContext.getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = mContext.getString(R.string.error_no_secure_decoder,
                                decoderInitializationException.mimeType);
                    } else {
                        errorString = mContext.getString(R.string.error_no_decoder,
                                decoderInitializationException.mimeType);
                    }
                } else {
                    errorString = mContext.getString(R.string.error_instantiating_decoder,
                            decoderInitializationException.decoderName);
                }
            }
        }
        if (errorString != null) {
            showToast(errorString);
        }
        needRetrySource = true;
        if (isBehindLiveWindow(e)) {
            clearResumePosition();
            initializePlayer();
        } else {
            updateResumePosition();
        }
    }


    @Override
    public boolean dispatchSetPlayWhenReady(ExoPlayer player, boolean playWhenReady) {
        shouldPlayMedia(playWhenReady);
        return true;
    }

    @Override
    public boolean dispatchSeekTo(ExoPlayer player, int windowIndex, long positionMs) {
        player.seekTo(windowIndex, positionMs);
        return true;
    }


    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onStop() {
            mAudioManager.abandonAudioFocus(afChangeListener);
        }

        @Override
        public void onPlay() {
            shouldPlayMedia(true);
        }

        @Override
        public void onPause() {
            shouldPlayMedia(false);
        }

        @Override
        public void onSkipToPrevious() {
            player.seekTo(0);
        }
    }

    private class BecomingNoisyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                // Pause the playback
                shouldPlayMedia(false);
            }
        }
    }

}
