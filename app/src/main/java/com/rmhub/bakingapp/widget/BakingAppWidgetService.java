package com.rmhub.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by MOROLANI on 5/16/2017
 * <p>
 * owm
 * .
 */

public class BakingAppWidgetService extends RemoteViewsService {
    public BakingAppWidgetService() {
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(this.getApplicationContext());
    }

}
