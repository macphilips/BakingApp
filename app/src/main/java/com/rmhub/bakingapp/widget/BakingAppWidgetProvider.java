package com.rmhub.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.rmhub.bakingapp.R;


/**
 * Created by MOROLANI on 5/15/2017
 * <p>
 * owm
 * .
 */

public class BakingAppWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_DATA_UPDATED = "data_update";
    private static final String LAUNCH_HOME = "home";

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        String action = intent.getAction();
        if (action.equals(ACTION_DATA_UPDATED)) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, BakingAppWidgetProvider.class);
            int[] allWidgetIds = manager.getAppWidgetIds(thisWidget);
            manager.notifyAppWidgetViewDataChanged(allWidgetIds, R.id.flipper);
        }
        super.onReceive(context, intent);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // Get all ids
        ComponentName thisWidget = new ComponentName(context, BakingAppWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {

            Intent intent = new Intent(context, BakingAppWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);
            rv.setRemoteAdapter(R.id.flipper, intent);
            rv.setEmptyView(R.id.flipper, R.id.empty_view);
/*
            Intent launchGraph = new Intent(context, IngredientDetails.class);
            launchGraph.setAction(LAUNCH_HOME);
            PendingIntent pendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(launchGraph)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.flipper, pendingIntent);*/

            appWidgetManager.updateAppWidget(widgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


}
