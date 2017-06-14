package com.rmhub.bakingapp.data;


import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {

    static final String AUTHORITY = "com.rmhub.bakingapp.data";
    static final String PATH_RECIPE = "recipes";
    static final String PATH_RECIPE_RECENT = "recipes_recent";

    static final String PATH_RECIPE_WITH_ID = PATH_RECIPE + "/#";

    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private Contract() {
    }

    public static final class RECIPE_RECENT implements BaseColumns {
        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_RECIPE_RECENT).build();
        public static final String COLUMN_RECIPE_ID = "rid";
    }
    @SuppressWarnings("unused")
    public static final class RECIPE implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_RECIPE).build();

        public static final String COLUMN_RECIPE_NAME = "name";
        public static final String COLUMN_INGREDIENTS = "ingredients";
        public static final String COLUMN_STEPS = "steps";
        public static final String COLUMN_RECIPE_ID = "id";
        public static final String COLUMN_SERVINGS = "servings";
        public static final String COLUMN_IMAGE = "image";

        public static Uri makeUriForId(int symbol) {
            return URI.buildUpon().appendPath(String.valueOf(symbol)).build();
        }

        static String getRecipeWithID(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }
    }

}
