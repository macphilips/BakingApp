package com.rmhub.bakingapp.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.dummy.DummyContent;
import com.rmhub.bakingapp.model.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RecipeDetailFragment extends Fragment {
    private static final String ARG_ITEM_ID = "item_id";
    private static final String RECIPE = "recipe";
    @BindView(R.id.button_next)
    ImageButton next;
    @BindView(R.id.button_prev)
    ImageButton prev;
    @BindView(R.id.button_done)
    Button done;
    @BindView(R.id.view_pager)
    ViewPager pager;
    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    @SuppressLint("ValidFragment")
    private RecipeDetailFragment() {
    }

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     *
     * @param arg1
     */
    public static RecipeDetailFragment newInstance(String arg1) {
        Bundle arguments = new Bundle();
        arguments.putString(RecipeDetailFragment.ARG_ITEM_ID, arg1);
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public static Fragment newInstance(Recipe item) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(RecipeDetailFragment.RECIPE, item);
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.button_done)
    void done() {

    }

    protected int getNextPageIndex() {
        return pager.getCurrentItem() + 1;
    }

    protected int getPreviousPageIndex() {
        return pager.getCurrentItem() + -1;
    }

    @OnClick(R.id.button_prev)
    void previous() {
        if (!canScrollToPreviousPage()) {
            prev.setVisibility(View.GONE);
        } else {
            prev.setVisibility(View.VISIBLE);
        }

        pager.setCurrentItem(getPreviousPageIndex());
    }

    @OnClick(R.id.button_next)
    void next() {
        if (!canScrollToNextPage()) {
            next.setVisibility(View.GONE);
            done.setVisibility(View.VISIBLE);
            return;
        } else {
            done.setVisibility(View.GONE);
            next.setVisibility(View.VISIBLE);
        }
        pager.setCurrentItem(getNextPageIndex());
    }

    protected boolean canScrollToNextPage() {
        return false;
    }

    protected boolean canScrollToPreviousPage() {
        return false;
    }
}
