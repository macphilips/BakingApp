package com.rmhub.bakingapp.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Recipe;
import com.rmhub.bakingapp.model.Step;
import com.rmhub.bakingapp.ui.fragments.RecipeDetailsFragment;
import com.rmhub.bakingapp.ui.fragments.RecipeStepFragment;
import com.rmhub.bakingapp.util.PrefUtil;
import com.rmhub.bakingapp.util.ProviderUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rmhub.bakingapp.widget.BakingAppWidgetProvider.ACTION_DATA_UPDATED;
import static com.rmhub.bakingapp.widget.BakingAppWidgetProvider.LAUNCH_DETAILS;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailsFragment.OnFragmentInteractionListener, RecipeStepFragment.OnFragmentInteraction {


    public static final String RECIPE = "recipe";
    public static final String EXTRAS = "extras";
    public static final String TAG = RecipeDetailActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private boolean mTwoPane;

    public void sendUpdateBroadcast() {
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
        sendBroadcast(dataUpdatedIntent);
    }

    private Recipe mRecipe;

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

    public Recipe getRecipe() {
        return mRecipe;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        String action = getIntent().getAction();
        if (action != null && action.equals(LAUNCH_DETAILS)) {
            mRecipe = getIntent().getParcelableExtra(RECIPE);
        } else {
            mRecipe = getIntent().getBundleExtra(EXTRAS).getParcelable(RECIPE);
            ProviderUtil.saveRecentRecipe(mRecipe);
            PrefUtil.saveRecentRecipe(mRecipe);
            sendUpdateBroadcast();
        }

        if (findViewById(R.id.recipe_detail_container) != null) {
            mTwoPane = true;
        }
        setupActionBar();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.recipe_detail_master, RecipeDetailsFragment.newInstance(mRecipe))
                .commit();
        if (mRecipe != null && getSupportActionBar() != null) {
            Log.e(TAG, "Setting title " + mRecipe.getName());
            getSupportActionBar().setTitle(mRecipe.getName());
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null && !mTwoPane) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onStepItemClicked(Step step) {
        attachFragment(step);
    }

    Fragment currentFragment;


    private void attachFragment(Step step) {

        if (!mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(RecipeStepActivity.RECIPE, mRecipe);
            arguments.putInt(RecipeStepActivity.CURRENT_STEP_POSITION, mRecipe.getSteps().indexOf(step));
            Intent i = new Intent(this, RecipeStepActivity.class);
            i.putExtra(RecipeStepActivity.EXTRAS, arguments);
            startActivity(i);
            overridePendingTransition(R.anim.fragment_slide_left_enter,
                    R.anim.fragment_slide_left_exit);
        } else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            int containerViewId;
            containerViewId = R.id.recipe_detail_container;
            currentFragment = RecipeStepFragment.newInstance(step);
            fragmentTransaction.replace(containerViewId, currentFragment);
            fragmentTransaction.commit();
        }
    }

    public void toggleHideyBar(boolean toggle) {
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions =  getWindow().getDecorView().getSystemUiVisibility();
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

    @Override
    public void setFullMode(boolean fullMode) {
        toggleHideyBar(fullMode);
    }
}