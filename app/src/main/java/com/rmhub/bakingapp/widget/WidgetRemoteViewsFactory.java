package com.rmhub.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.data.Contract;
import com.rmhub.bakingapp.model.Recipe;
import com.rmhub.bakingapp.util.ProviderUtil;

import java.util.List;

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
        rv.setTextViewText(R.id.widget_recipe_name, item.getName());

        Intent fillInIntent = new Intent();
        fillInIntent.setData(Contract.RECIPE.makeUriForId(item.getId()));
        rv.setOnClickFillInIntent(R.id.widget_item_list_container, fillInIntent);
        //  rv.setOnClickFillInIntent(R.id.price, fillInIntent);
        // rv.setOnClickFillInIntent(R.id.change, fillInIntent);
        return rv;
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
