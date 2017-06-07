package com.rmhub.bakingapp.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Recipe;
import com.rmhub.bakingapp.model.Step;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeFragment.OnFragmentInteractionListener,RecipeDetailFragment.OnFragmentInteraction {


    public static final String RECIPE = "recipe";
    public static final String EXTRAS = "extras";
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private boolean mTwoPane;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mRecipe = getIntent().getBundleExtra(EXTRAS).getParcelable(RECIPE);

        if (findViewById(R.id.recipe_detail_container) != null) {
            mTwoPane = true;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.recipe_detail_master, RecipeFragment.newInstance(mRecipe))
                .commit();

    }

    @Override
    public void onStepItemClicked(Step step) {
        attachFragment(step);
    }

    Fragment currentFragment;

    @Override
    public void onBackPressed() {
      /*  if (!mTwoPane && currentFragment instanceof RecipeStepFragment) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.remove(currentFragment);
            fragmentTransaction.commit();
        } */
        super.onBackPressed();
    }

    private void attachFragment(Step step) {

        if (!mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(RecipeStepActivity.RECIPE, mRecipe);
            arguments.putInt(RecipeStepActivity.CURRENT_STEP_POSITION, mRecipe.getSteps().indexOf(step));
            Intent i = new Intent(this, RecipeStepActivity.class);
            i.putExtra(RecipeStepActivity.EXTRAS, arguments);
            overridePendingTransition(R.anim.fragment_slide_left_enter,
                    R.anim.fragment_slide_left_exit);
            startActivity(i);

        } else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            int containerViewId;
            containerViewId = R.id.recipe_detail_container;
            currentFragment = RecipeDetailFragment.newInstance(step);
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