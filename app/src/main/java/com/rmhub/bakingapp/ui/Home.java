package com.rmhub.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Recipe;
import com.rmhub.bakingapp.util.NetworkUtil;
import com.rmhub.bakingapp.util.RecipeRequest;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Home extends AppCompatActivity {
    @BindView(R.id.recipe_list)
    RecyclerView recipe_rv;
    RecipeListAdapter mAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
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
        recipe_rv.setAdapter(mAdapter);
        RecipeRequest.MovieRequestListener requestListener = new RecipeRequest.MovieRequestListener() {
            @Override
            public void onResponse(List<Recipe> response) {
                mAdapter.setRecipeList(response);
            }
        };
        NetworkUtil.getInstance().fetchResult(requestListener);
    }

}
