package com.rmhub.bakingapp.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Recipe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MOROLANI on 5/25/2017
 * <p>
 * owm
 * .
 */

class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {

    private List<Recipe> mValues;
    private View.OnClickListener listener;

    RecipeListAdapter() {
    }

    void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    void setRecipeList(List<Recipe> list) {
        mValues = list;
        notifyDataSetChanged();
    }

    @Override
    public RecipeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_list_content, parent, false);
        return new RecipeListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeListAdapter.ViewHolder holder, int position) {
        Recipe item = mValues.get(position);
        holder.recipe_name.setText(item.getName());
        if (listener != null) {
            holder.container.setTag(item);
            holder.container.setOnClickListener(listener);
        }
    }

    @Override
    public int getItemCount() {
        return (mValues == null) ? 0 : mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View container;
        @BindView(R.id.recipe_name)
        TextView recipe_name;

        ViewHolder(View view) {
            super(view);
            container = view;

            ButterKnife.bind(this, view);
        }

    }

}
