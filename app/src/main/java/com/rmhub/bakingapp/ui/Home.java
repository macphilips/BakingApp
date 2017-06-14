package com.rmhub.bakingapp.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Recipe;
import com.rmhub.bakingapp.ui.adapters.RecipeListAdapter;
import com.rmhub.bakingapp.util.NetworkUtil;
import com.rmhub.bakingapp.util.RecipeRequest;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Home extends AppCompatActivity {
    @BindView(R.id.recipe_list)
    RecyclerView mRecyclerView;
    RecipeListAdapter mAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.empty_view)
    View empty_view;
    @BindView(R.id.progress_bar)
    ProgressBar progress;
    //  private ProgressDialog progress;
    private boolean recipeAvail = false;

    private List<Recipe> recipeList;
    private boolean isShowing;

    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    void showProgress() {
        isShowing = true;
        progress.setVisibility(View.VISIBLE);
        empty_view.setVisibility(View.GONE);
    }

    void hideProgress(final boolean success) {
        if (isShowing) {
            int width = progress.getWidth();
            float start = progress.getX();
            float end = -width;
            ObjectAnimator animX = ObjectAnimator.ofFloat(progress, "x", start, end);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    progress.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    progress.setVisibility(View.GONE);
                    if (success) {
                        empty_view.setVisibility(View.GONE);
                    } else {

                        empty_view.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animSetXY.playTogether(animX);
            animSetXY.setDuration(500);
            animSetXY.start();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        showProgress();
        mAdapter = new RecipeListAdapter();
        mAdapter.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putParcelable(RecipeDetailActivity.RECIPE, (Recipe) v.getTag());
                Intent intent = new Intent(Home.this, RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailActivity.EXTRAS, extras);
                startActivity(intent);
            }
        });
        if (getResources().getBoolean(R.bool.tablet)) {

            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            final int mImageWidthSize = getResources().getDimensionPixelSize(R.dimen.image_width_size);
            final int padding = getResources().getDimensionPixelSize(R.dimen.image_padding);
            mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            RecyclerView.LayoutManager mLayoutManager = mRecyclerView.getLayoutManager();
                            if (mAdapter.getNumColumns() == 0) {
                                final int numColumns = (int) Math.floor(
                                        mLayoutManager.getWidth() / (mImageWidthSize + (padding * 2)));
                                if (numColumns > 0) {
                                    final int columnWidth =
                                            (mLayoutManager.getWidth() / numColumns) - padding;
                                    mAdapter.setNumColumns(numColumns);

                                    if (mLayoutManager instanceof GridLayoutManager) {
                                        ((GridLayoutManager) mLayoutManager).setSpanCount(numColumns);
                                    }
                                    mAdapter.setItemWidth(columnWidth);
                                    if (Utils.hasJellyBean()) {
                                        mRecyclerView.getViewTreeObserver()
                                                .removeOnGlobalLayoutListener(this);
                                    } else {
                                        mRecyclerView.getViewTreeObserver()
                                                .removeGlobalOnLayoutListener(this);
                                    }
                                }
                            }
                        }
                    });
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        mRecyclerView.setAdapter(mAdapter);
        RecipeRequest.MovieRequestListener requestListener = new RecipeRequest.MovieRequestListener() {
            @Override
            public void onResponse(List<Recipe> response) {

                if (response.isEmpty()) {
                    recipeAvail = false;
                } else {
                    recipeAvail = true;
                }
                hideProgress(recipeAvail);
                recipeList = response;
                mAdapter.setRecipeList(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                hideProgress(false);
                // empty_view.setVisibility(View.VISIBLE);
            }


        };
        NetworkUtil.getInstance().fetchResult(requestListener);
    }

    public boolean hasRecipe() {
        return recipeAvail;
    }
}
