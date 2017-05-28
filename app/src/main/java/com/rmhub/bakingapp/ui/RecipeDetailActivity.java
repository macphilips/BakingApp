package com.rmhub.bakingapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Recipe;
import com.rmhub.bakingapp.model.Step;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeFragment.OnFragmentInteractionListener {

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

    private void attachFragment(Step step) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        int containerViewId;
        Fragment fragment;
        if (!mTwoPane) {
            fragment = RecipeStepFragment.newInstance(mRecipe, step);
            fragmentTransaction
                    .setCustomAnimations(
                            R.anim.fragment_slide_left_enter,
                            R.anim.fragment_slide_left_exit,
                            R.anim.fragment_slide_right_enter,
                            R.anim.fragment_slide_right_exit)
                    .addToBackStack(null);
            containerViewId = R.id.recipe_detail_master;
        } else {
            containerViewId = R.id.recipe_detail_container;
            fragment = RecipeDetailFragment.newInstance(step);
        }
        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.commit();
    }
}
