package com.rmhub.bakingapp.ui;

import android.os.Build;

/**
 * Created by MOROLANI on 5/29/2017
 * <p>
 * owm
 * .
 */

public class Utils {

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
}
