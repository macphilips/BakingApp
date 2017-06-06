package com.rmhub.bakingapp.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContentResolverCompat;
import android.util.Log;

import com.rmhub.bakingapp.BakingApp;
import com.rmhub.bakingapp.data.Contract;
import com.rmhub.bakingapp.model.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MOROLANI on 5/25/2017
 * <p>
 * owm
 * .
 */

public class ProviderUtil {
    public static boolean insertRecipes(List<Recipe> details) {
        List<ContentValues> movieCVs = new ArrayList<>();
        for (Recipe movieDetails : details) {

            ContentValues movieCV = new ContentValues();

            movieCV.put(Contract.RECIPE.COLUMN_RECIPE_ID, movieDetails.getId());

            movieCV.put(Contract.RECIPE.COLUMN_RECIPE_NAME, movieDetails.getName());

            movieCV.put(Contract.RECIPE.COLUMN_INGREDIENTS, movieDetails.getIngredientsJsonString());

            movieCV.put(Contract.RECIPE.COLUMN_SERVINGS, movieDetails.getServings());

            movieCV.put(Contract.RECIPE.COLUMN_IMAGE, movieDetails.getImage());

            movieCV.put(Contract.RECIPE.COLUMN_STEPS, movieDetails.getStepsJsonString());

            movieCVs.add(movieCV);
        }

        int result = BakingApp.getInstance().getContentResolver()
                .bulkInsert(
                        Contract.RECIPE.URI,
                        movieCVs.toArray(new ContentValues[movieCVs.size()]));
        return result > 0;

    }

    public static ArrayList<Recipe> getRecipes(Context context) {
        ArrayList<Recipe> arrayList = new ArrayList<>();
        Cursor cursor = ContentResolverCompat.query(context.getContentResolver(),
                Contract.RECIPE.URI,
                null,
                null,
                null,
                null,
                null);

        if (cursor == null || isCursorEmpty(cursor)) {
            return arrayList;
        }
        cursor.moveToFirst();
        while (true) {
            Recipe emp = Recipe.buildFromCursor(cursor);
            Log.d(ProviderUtil.class.getSimpleName(), String.valueOf(emp));
            arrayList.add(emp);
            if (!cursor.moveToNext())
                break;
        }
        cursor.close();

        return arrayList;
    }

    private static boolean isCursorEmpty(Cursor cursor) {
        if (!cursor.moveToFirst() || cursor.getCount() == 0) {
            cursor.close();
            return true;
        }
        return false;
    }
}
