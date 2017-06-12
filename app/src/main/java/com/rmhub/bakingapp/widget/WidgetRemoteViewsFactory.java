package com.rmhub.bakingapp.widget;

import android.content.Context;
import android.os.Binder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Ingredient;
import com.rmhub.bakingapp.model.Recipe;
import com.rmhub.bakingapp.ui.adapters.RecipeDetailListAdapter;
import com.rmhub.bakingapp.util.ProviderUtil;

import java.util.List;
import java.util.Locale;

/**
 * Created by MOROLANI on 5/16/2017
 * <p>
 * owm
 * .
 */

class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private List<Recipe> items;

    WidgetRemoteViewsFactory(Context context) {
        mContext = context;
    }


    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();
        items = ProviderUtil.getRecipes(mContext);
        Binder.restoreCallingIdentity(identityToken);

    }


    public void onDestroy() {

    }


    public RemoteViews getViewAt(int position) {

        Recipe item = items.get(position);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_layout);
        String text = item.getName() + "\nIngredients:\n\n"
                + getIngredientText(item);
        rv.setTextViewText(R.id.widget_recipe_name, text);
        return rv;
    }

    private String getIngredientText(Recipe mValues) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (Ingredient ingredient : mValues.getIngredients()) {
            String s = String.format(Locale.getDefault(), "%d. %s %s of %s", ++i,
                    ingredient.getQuantity(), ingredient.getMeasure(), ingredient.getIngredient());

            if (i <= mValues.getIngredients().size() - 1) {
                builder.append(s).append(".<br/>");
            } else {
                builder.append(s).append(".");
            }
        }
        Log.d(RecipeDetailListAdapter.class.getSimpleName(), builder.toString());
        return builder.toString();
    }

    public RemoteViews getLoadingView() {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public int getCount() {
        int count = 0;
        if (items != null) {
            count = items.size();
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
