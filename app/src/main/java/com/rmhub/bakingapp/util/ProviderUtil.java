package com.rmhub.bakingapp.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
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
        List<ContentValues> contentValues = new ArrayList<>();
        for (Recipe recipe : details) {
            ContentValues movieCV = new ContentValues();
            movieCV.put(Contract.RECIPE.COLUMN_RECIPE_ID, recipe.getId());
            movieCV.put(Contract.RECIPE.COLUMN_RECIPE_NAME, recipe.getName());
            movieCV.put(Contract.RECIPE.COLUMN_INGREDIENTS, recipe.getIngredientsJsonString());
            movieCV.put(Contract.RECIPE.COLUMN_SERVINGS, recipe.getServings());
            movieCV.put(Contract.RECIPE.COLUMN_IMAGE, recipe.getImage());
            movieCV.put(Contract.RECIPE.COLUMN_STEPS, recipe.getStepsJsonString());
            contentValues.add(movieCV);
        }

        int result = BakingApp.getInstance().getContentResolver()
                .bulkInsert(Contract.RECIPE.URI,
                        contentValues.toArray(new ContentValues[contentValues.size()]));
        return result > 0;

    }

    public static void saveRecentRecipe(Recipe recipe) {
        ContentValues movieCV = new ContentValues();
        movieCV.put(Contract.RECIPE_RECENT.COLUMN_RECIPE_ID, recipe.getId());
        BakingApp.getInstance().getContentResolver()
                .insert(Contract.RECIPE_RECENT.URI, movieCV);
    }

    @Nullable
    public static Recipe getRecentRecipe(Context context) {
        Cursor returnCursor = ContentResolverCompat.query(context.getContentResolver(),
                Contract.RECIPE_RECENT.URI,
                null,
                null,
                null,
                null,
                null);

        if (returnCursor != null && !isCursorEmpty(returnCursor)) {
            int recipeID = returnCursor.getInt(returnCursor.getColumnIndexOrThrow(Contract.RECIPE_RECENT.COLUMN_RECIPE_ID));
            Recipe recipe = getRecipeByID(context, Contract.RECIPE.makeUriForId(recipeID));
            Log.d(ProviderUtil.class.getSimpleName(), String.valueOf(recipe));
            returnCursor.close();
            return recipe;
        }
        return null;

    }

    public static Recipe getRecipeByID(Context context, Uri id) {
        Cursor cursor = ContentResolverCompat.query(context.getContentResolver(),
                id,
                null,
                null,
                null,
                null,
                null);

        if (cursor == null || isCursorEmpty(cursor)) {
            return null;
        }
        return Recipe.buildFromCursor(cursor);
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
