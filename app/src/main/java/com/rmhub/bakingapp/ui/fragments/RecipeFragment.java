package com.rmhub.bakingapp.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Recipe;
import com.rmhub.bakingapp.model.Step;
import com.rmhub.bakingapp.ui.adapters.RecipeDetailListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MOROLANI on 5/24/2017
 * <p>
 * owm
 * .
 */

public class RecipeFragment extends Fragment {
    private static final String RECIPE = "recipe";

    @BindView(R.id.item_list)
    RecyclerView recyclerView;

    @BindView(R.id.detail_layout_recipe_name)
    TextView recipe_name;

    private RecipeFragment.OnFragmentInteractionListener mCallback;

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
        Recipe item = getArguments().getParcelable(RECIPE);
        if (item != null) {
            recipe_name.setText(item.getName());
            final RecipeDetailListAdapter mAdapter = new RecipeDetailListAdapter(item);
            recyclerView.setAdapter(mAdapter);
            mAdapter.setListener(mCallback);
        }
        return rootView;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {

            mCallback = (RecipeFragment.OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (RecipeFragment.OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        void onStepItemClicked(Step item);
    }
}
