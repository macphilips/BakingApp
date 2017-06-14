package com.rmhub.bakingapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.rmhub.bakingapp.BakingApp;
import com.rmhub.bakingapp.model.Recipe;

/**
 * Created by MOROLANI on 6/13/2017
 * <p>
 * owm
 * .
 */

public class PrefUtil {
    private static final String RECENT = "recent";

    public static void saveRecentRecipe(Recipe mRecipe) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BakingApp.getInstance());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(RECENT, mRecipe.getJsonString());
        editor.apply();
    }


    public static Recipe getRecentRecipe(Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BakingApp.getInstance());
        String s = prefs.getString(RECENT, "");
        if (!TextUtils.isEmpty(s)) return new Gson().fromJson(s, Recipe.class);
        return new Recipe();
    }
}
