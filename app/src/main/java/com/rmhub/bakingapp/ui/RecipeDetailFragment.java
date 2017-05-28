package com.rmhub.bakingapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Step;
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
    @BindView(R.id.video_player)
    SimpleExoPlayerView playerView;
    @BindView(R.id.steps_desc)
    TextView mStepDesc;

    public static RecipeDetailFragment newInstance(Step item) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(RecipeDetailFragment.STEP, item);
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_pager_item, container, false);
        ButterKnife.bind(this, rootView);
        Step item = getArguments().getParcelable(STEP);
        if (item != null) {
            mStepDesc.setText(item.getDescription());
        }
        return rootView;
    }
}
