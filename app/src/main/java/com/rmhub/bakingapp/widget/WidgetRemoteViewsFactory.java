package com.rmhub.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Ingredient;
import com.rmhub.bakingapp.model.Recipe;
import com.rmhub.bakingapp.util.PrefUtil;
import com.rmhub.bakingapp.util.ProviderUtil;

import java.util.Locale;

import static com.rmhub.bakingapp.ui.RecipeDetailActivity.RECIPE;

/**
 * Created by MOROLANI on 5/16/2017
 * <p>
 * owm
 * .
 */

class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Recipe recipe;

    WidgetRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();

        recipe = PrefUtil.getRecentRecipe(mContext);
        Log.e(WidgetRemoteViewsFactory.class.getSimpleName(), "From SharedPref => " + String.valueOf(recipe));
        Log.e(WidgetRemoteViewsFactory.class.getSimpleName(), "From Provider => " + String.valueOf(ProviderUtil.getRecentRecipe(mContext)));
        Binder.restoreCallingIdentity(identityToken);

    }

    public void onDestroy() {

    }


    public RemoteViews getViewAt(int position) {

        if (position == 0) {
            final RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_layout_1);
            rv.setTextViewText(R.id.recipe_name, recipe.getName());
            rv.setTextViewText(R.id.recipe_details, "Serve for " + recipe.getServings());
            /*rv.setInt(R.id.recipe_image, "setBackgroundColor", (ColorGenerator.MATERIAL.getColor(recipe.getName())));

            Glide
                    .with(mContext)
                    .load(recipe.getImage())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .fitCenter()
                    .placeholder(R.drawable.ic_picture)
                    .error(R.drawable.ic_picture)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            rv.setBitmap(R.id.recipe_image, "setImageBitmap", resource);
                        }
                    });

            */

            return rv;
        } else if (position == recipe.getIngredients().size() + 1) {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_layout_3);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtra(RECIPE, recipe);
            rv.setOnClickFillInIntent(R.id.show_steps, fillInIntent);
            return rv;
        } else {
            Ingredient ingredient = recipe.getIngredients().get(position - 1);
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_layout_2);
            // String recipe_name = item.getName();
            String text = String.format(Locale.getDefault(), "%d. %s %s of %s", position,
                    ingredient.getQuantity(), ingredient.getMeasure(), ingredient.getIngredient());
            rv.setTextViewText(R.id.widget_recipe_name, text);

       /*  int i = 0;
        for (Ingredient ingredient : item.getIngredients()) {


        }*/
            return rv;
        }
    }


    public RemoteViews getLoadingView() {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }

    public int getViewTypeCount() {
        return 3;
    }

    public int getCount() {
        int count = 0;
        if (recipe != null && recipe.getIngredients() != null) {
            count = recipe.getIngredients().size() + 2;
        }
        return count;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }
}
