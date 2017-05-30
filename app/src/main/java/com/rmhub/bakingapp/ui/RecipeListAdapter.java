package com.rmhub.bakingapp.ui;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rmhub.bakingapp.BakingApp;
import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Recipe;
import com.rmhub.bakingapp.util.ColorGenerator;

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
    private int numColumns;
    private int itemWidth;
    private int itemHeight;
    private LinearLayout.LayoutParams mImageViewLayoutParams;

    RecipeListAdapter() {
        setImageLayoutSize(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

    private void setImageLayoutSize(int width, int height) {
        mImageViewLayoutParams = new LinearLayout.LayoutParams(width, height);
    }

    @Override
    public void onBindViewHolder(final RecipeListAdapter.ViewHolder holder, int position) {
        Recipe item = mValues.get(position);
        holder.recipe_name.setText(item.getName());

        if (!TextUtils.isEmpty(item.getImage())) {
            holder.recipe_thumbnail.setLayoutParams(mImageViewLayoutParams);

            Glide
                    .with(BakingApp.getInstance())
                    .load(item.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .override(itemWidth, itemHeight)
                    .fitCenter()
                    .placeholder(R.drawable.ic_picture)
                    .error(R.drawable.ic_picture)
                    .crossFade()
                    .into(holder.recipe_thumbnail);
        } else {
            holder.recipe_thumbnail.setBackgroundColor(ColorGenerator.MATERIAL.getColor(item.getName()));
        }
        if (listener != null) {
            holder.container.setTag(item);
            holder.container.setOnClickListener(listener);
        }
        holder.recipe_thumbnail.setContentDescription(BakingApp.getInstance().getResources().getString(R.string.recipe_name,
                item.getName()));
    }

    @Override
    public int getItemCount() {
        return (mValues == null) ? 0 : mValues.size();
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public void setItemWidth(int itemWidth) {
        if (this.itemWidth == itemWidth) {
            return;
        }
        this.itemWidth = itemWidth;
        this.itemHeight = (itemWidth);
        ;
        setImageLayoutSize(itemWidth, itemHeight);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        View container;
        @BindView(R.id.recipe_name)
        TextView recipe_name;
        @BindView(R.id.recipe_thumbnail)
        ImageView recipe_thumbnail;

        ViewHolder(View view) {
            super(view);
            container = view;

            ButterKnife.bind(this, view);
        }

    }

}
