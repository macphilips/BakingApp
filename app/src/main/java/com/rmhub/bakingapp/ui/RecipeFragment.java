package com.rmhub.bakingapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Recipe;

import butterknife.ButterKnife;

/**
 * Created by MOROLANI on 5/24/2017
 * <p>
 * owm
 * .
 */

public class RecipeFragment extends Fragment {
    private static final String RECIPE = "recipe";

    public static RecipeFragment newInstance(Recipe recipe) {
        Bundle args = new Bundle();
        RecipeFragment fragment = new RecipeFragment();
        args.putParcelable(RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_list, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
