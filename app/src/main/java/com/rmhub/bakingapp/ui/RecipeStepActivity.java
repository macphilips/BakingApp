package com.rmhub.bakingapp.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Recipe;
import com.rmhub.bakingapp.model.Step;
import com.rmhub.bakingapp.ui.fragments.RecipeStepFragment;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class RecipeStepActivity extends AppCompatActivity implements RecipeStepFragment.OnFragmentInteraction {

    public static final String EXTRAS = "Extras";
    public static final String RECIPE = "recipe";
    public static final String CURRENT_STEP_POSITION = "current_step_position";
    @BindView(R.id.button_next)
    ImageButton next;
    @BindView(R.id.button_prev)
    ImageButton prev;
    @BindView(R.id.button_done)
    Button done;
    @BindView(R.id.pager_indicator)
    TextView indicator;
    @BindView(R.id.video_player_container)
    View container;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottom_nav)
    View bottom_nav;
    RecipeStepFragment mCurrentFragment;

    private Step mCurrentStep;
    private Recipe mRecipe;

    private Handler mHandler = new Handler();
    private Runnable mDelayedStopRunnable = new Runnable() {
        @Override
        public void run() {
            int height = bottom_nav.getHeight();
            float start = bottom_nav.getY() + height;
            float end = bottom_nav.getY();
            ObjectAnimator animX = ObjectAnimator.ofFloat(bottom_nav, "y", start, end);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    bottom_nav.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    bottom_nav.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animSetXY.playTogether(animX);
            animSetXY.setDuration(TimeUnit.SECONDS.toMillis(1));
            animSetXY.start();
        }
    };
    private String thumbnailTest = "https://firebasestorage.googleapis.com/v0/b/sinecera-1.appspot.com/o/eIjDF2jpfCRYsBGshFwjDaNfRKI2%2Fimage%2Fjpeg%2Fartcover.jpg?alt=media&token=3b77ff4f-dd63-4029-8ed6-c62802f3b396";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setupActionBar();
        Bundle bundleExtra = getIntent().getBundleExtra(EXTRAS);
        mRecipe = bundleExtra.getParcelable(RECIPE);

        Step mInit = null;
        if (mRecipe != null) {
            for (Step step : mRecipe.getSteps()) {
                if (TextUtils.isEmpty(step.getVideoURL())) {
                    step.setThumbnailURL(thumbnailTest);
                }
            }
            mInit = mRecipe.getSteps().get(bundleExtra.getInt(CURRENT_STEP_POSITION));
        }
        setupSinglePane(mInit);

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && !getResources().getBoolean(R.bool.tablet)) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void toggleHideyBar(boolean toggle) {
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);

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
        if (toggle) {
            if (actionBar != null && actionBar.isShowing()) {
                actionBar.hide();
            }
        } else {
            if (actionBar != null && !actionBar.isShowing()) {
                actionBar.show();
            }
        }
    }

    void setupSinglePane(Step step) {
        hideOrShowPreviousButton(step);
        hideOrShowNextButton(step);
        if (container != null) attachFragment(step);

    }

    @Override
    public void setFullMode(boolean fullMode) {
        if (fullMode) {
            hideNav();
        } else {
            showNav();
        }
        toggleHideyBar(fullMode);
    }

    private void updateIndicator() {
        if (indicator != null) {
            int totalSteps = mRecipe.getSteps().size();
            int currentStep = mRecipe.getSteps().indexOf(mCurrentStep) + 1;
            indicator.setText(String.format(Locale.getDefault(), "%d / %d", currentStep, totalSteps));
        }
    }

    @OnClick(R.id.button_done)
    void done() {
        if (done == null) return;
        finish();
    }

    @OnClick(R.id.button_prev)
    void previous() {
        prevButtonClicked(mCurrentStep);
    }

    private void hideOrShowPreviousButton(Step step) {
        if (prev == null) return;
        if (!canScrollToPreviousStep(step)) {
            prev.setVisibility(View.INVISIBLE);
        } else {
            prev.setVisibility(View.VISIBLE);
        }
    }

    @Optional
    @OnClick(R.id.button_next)
    void next() {
        if (hideOrShowNextButton(mCurrentStep)) return;
        nextButtonClicked(mCurrentStep);
    }

    private boolean hideOrShowNextButton(Step step) {
        if (next == null || done == null) return false;
        if (!canScrollToNextStep(step)) {
            next.setVisibility(View.GONE);
            done.setVisibility(View.VISIBLE);
            return true;
        } else {
            done.setVisibility(View.GONE);
            next.setVisibility(View.VISIBLE);
        }
        return false;
    }

    public void nextButtonClicked(Step mStep) {
        if (canScrollToNextStep(mStep)) {
            List<Step> steps = mRecipe.getSteps();
            int nextStep = steps.indexOf(mStep) + 1;
            attachFragment(steps.get(nextStep));
        }
    }

    public void prevButtonClicked(Step mStep) {
        if (canScrollToPreviousStep(mStep)) {
            List<Step> steps = mRecipe.getSteps();
            int prevStep = steps.indexOf(mStep) - 1;
            attachFragment(steps.get(prevStep));
        }
    }

    private void attachFragment(Step step) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager
                .beginTransaction();
        mCurrentFragment = RecipeStepFragment.newInstance(step);
        mCurrentFragment.setOnPlaybackComplete(new RecipeStepFragment.OnPlaybackComplete() {
            @Override
            public void prev() {

            }

            @Override
            public void next() {
                RecipeStepActivity.this.next();
            }
        });

        int containerViewId;
        containerViewId = R.id.video_player_container;
        fragmentTransaction.replace(containerViewId, mCurrentFragment);
        fragmentTransaction.commit();
        mCurrentStep = step;
        updateIndicator();
        hideOrShowPreviousButton(mCurrentStep);
        hideOrShowNextButton(mCurrentStep);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(step.getShortDescription());
        }
    }

    public boolean canScrollToNextStep(Step mStep) {
        List<Step> steps = mRecipe.getSteps();
        return steps.contains(mStep) && (steps.indexOf(mStep) < steps.size() - 1);
    }

    public boolean canScrollToPreviousStep(Step mStep) {
        List<Step> steps = mRecipe.getSteps();
        return steps.contains(mStep) && steps.indexOf(mStep) > 0;
    }

    void hideNav() {
        bottom_nav.setVisibility(View.GONE);
    }

    void showNav() {
        mHandler.postDelayed(mDelayedStopRunnable, 200);
    }
}
