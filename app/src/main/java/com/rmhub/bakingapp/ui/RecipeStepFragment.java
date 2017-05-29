package com.rmhub.bakingapp.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Recipe;
import com.rmhub.bakingapp.model.Step;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;


public class RecipeStepFragment extends Fragment {
    private static final String RECIPE = "recipe";
    private static final String CURRENT_STEP_POSITION = "current_step_position";


    @BindView(R.id.button_next)
    ImageButton next;

    @BindView(R.id.button_prev)
    ImageButton prev;

    @BindView(R.id.button_done)
    Button done;

    @BindView(R.id.pager_indicator)
    TextView indicator;


    @BindView(R.id.video_player_container)
    View container;

    private Step mCurrentStep;
    private Recipe mRecipe;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    @SuppressLint("ValidFragment")
    public RecipeStepFragment() {
    }

    public static Fragment newInstance(Recipe item, Step currentStep) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(RECIPE, item);
        arguments.putParcelable(CURRENT_STEP_POSITION, currentStep);
        RecipeStepFragment fragment = new RecipeStepFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipe = getArguments().getParcelable(RECIPE);

    }

    void setupSinglePane(Step step) {
        hideOrShowPreviousButton(step);
        hideOrShowNextButton(step);
        if (container != null) attachFragment(step);

    }

    private void updateIndicator() {
        if (indicator != null) {
            int totalSteps = mRecipe.getSteps().size();
            int currentStep = mRecipe.getSteps().indexOf(mCurrentStep) + 1;
            indicator.setText(String.format(Locale.getDefault(), "%d / %d", currentStep, totalSteps));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail, container, false);
        ButterKnife.bind(this, rootView);
        Step mInit = getArguments().getParcelable(CURRENT_STEP_POSITION);
        setupSinglePane(mInit);
        return rootView;
    }

    @OnClick(R.id.button_done)
    void done() {
        if (done == null) return;
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    @OnClick(R.id.button_prev)
    void previous() {
        prevButtonClicked(mCurrentStep);
    }

    private void hideOrShowPreviousButton(Step step) {
        if (prev == null) return;
        if (!canScrollToPreviousStep(step)) {
            prev.setVisibility(View.GONE);
        } else {
            prev.setVisibility(View.VISIBLE);
        }
    }

    @Optional
    @OnClick(R.id.button_next)
    void next() {
        if (hideOrShowNextButton(mCurrentStep)) return;
        nextButtonClicked(mCurrentStep);
    }

    private boolean hideOrShowNextButton(Step step) {
        if (next == null || done == null) return false;
        if (!canScrollToNextStep(step)) {
            next.setVisibility(View.GONE);
            done.setVisibility(View.VISIBLE);
            return true;
        } else {
            done.setVisibility(View.GONE);
            next.setVisibility(View.VISIBLE);
        }
        return false;
    }

    public void nextButtonClicked(Step mStep) {
        if (canScrollToNextStep(mStep)) {
            List<Step> steps = mRecipe.getSteps();
            int nextStep = steps.indexOf(mStep) + 1;
            attachFragment(steps.get(nextStep));
        }
    }

    public void prevButtonClicked(Step mStep) {
        if (canScrollToPreviousStep(mStep)) {
            List<Step> steps = mRecipe.getSteps();
            int prevStep = steps.indexOf(mStep) - 1;
            attachFragment(steps.get(prevStep));
        }
    }

    private void attachFragment(Step step) {
        Fragment fragment = RecipeDetailFragment.newInstance(step);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        int containerViewId;
        containerViewId = R.id.video_player_container;
        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.commit();
        mCurrentStep = step;
        updateIndicator();
        hideOrShowPreviousButton(mCurrentStep);
        hideOrShowNextButton(mCurrentStep);
    }

    public boolean canScrollToNextStep(Step mStep) {
        List<Step> steps = mRecipe.getSteps();
        return steps.contains(mStep) && (steps.indexOf(mStep) < steps.size() - 1);
    }

    public boolean canScrollToPreviousStep(Step mStep) {
        List<Step> steps = mRecipe.getSteps();
        return steps.contains(mStep) && steps.indexOf(mStep) > 0;
    }

}
