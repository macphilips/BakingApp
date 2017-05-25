package com.rmhub.bakingapp.ui;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rmhub.bakingapp.BakingApp;
import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Step;

import java.util.List;

/**
 * Created by MOROLANI on 5/25/2017
 * <p>
 * owm
 * .
 */

public class RecipePageAdapter extends PagerAdapter {
    private final List<Step> steps;
    private final LayoutInflater mInflater;

    public RecipePageAdapter(List<Step> steps) {
        this.steps = steps;
        mInflater = LayoutInflater.from(BakingApp.getInstance());
    }
    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return steps.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = mInflater.inflate(R.layout.recipe_pager_item, container, false);
     //   final View playButton = v.findViewById(R.id.trailer_icon);

        container.addView(v);
        return v;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
