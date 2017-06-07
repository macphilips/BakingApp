package com.rmhub.bakingapp.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Ingredient;
import com.rmhub.bakingapp.model.Recipe;
import com.rmhub.bakingapp.model.Step;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MOROLANI on 5/26/2017
 * <p>
 * owm
 * .
 */

public class RecipeDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int INGREDIENTS = 0;
    private static final int STEPS = 1;
    private final Recipe mValues;
    private RecipeFragment.OnFragmentInteractionListener mListener;

    public RecipeDetailListAdapter(Recipe items) {
        mValues = items;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == INGREDIENTS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_details_ingredients, parent, false);
            return new IngredientViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_details_desc_steps, parent, false);

            return new StepsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof IngredientViewHolder) {
            IngredientViewHolder ingredientHolder = (IngredientViewHolder) holder;
            StringBuilder builder = new StringBuilder();
            int i = 0;
            for (Ingredient ingredient : mValues.getIngredients()) {
                String s = String.format(Locale.getDefault(), "%s %s of %s",
                        ingredient.getQuantity(), ingredient.getMeasure(), ingredient.getIngredient());
                ++i;
                if (i == mValues.getIngredients().size() - 1) {
                    builder.append(s).append(", \nand");
                } else if (i == mValues.getIngredients().size()) {
                    builder.append(s).append(".");
                } else {
                    builder.append(s).append(", \n");
                }
            }
            ingredientHolder.mContentView.setText(builder.toString());
        } else {
            StepsViewHolder stepsViewHolder = (StepsViewHolder) holder;
            final Step step = mValues.getSteps().get(position - 1);
            String step_desc = String.format(Locale.getDefault(),
                    "Step %d of %d %s", step.getId() + 1, mValues.getSteps().size(), step.getShortDescription());
            stepsViewHolder.short_desc.setText(step_desc);
            if (mListener != null) {
                stepsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onStepItemClicked(step);
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return INGREDIENTS;
        }
        return STEPS;
    }

    @Override
    public int getItemCount() {
        return mValues.getSteps().size() + 1;
    }

     void setListener(RecipeFragment.OnFragmentInteractionListener listener) {
        this.mListener = listener;
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        //  final View mView;
        @BindView(R.id.ingredients)
        TextView mContentView;

        IngredientViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

     class StepsViewHolder extends RecyclerView.ViewHolder {


        private final View mView;
        @BindView(R.id.steps_desc)
        TextView short_desc;

         StepsViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }


    }
}
