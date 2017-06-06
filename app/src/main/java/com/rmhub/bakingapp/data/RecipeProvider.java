package com.rmhub.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class RecipeProvider extends ContentProvider {
    private static final int RECIPE = 100;
    private static final int RECIPE_FOR_ID = 101;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private DbHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(Contract.AUTHORITY, Contract.PATH_RECIPE, RECIPE);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_RECIPE_WITH_ID, RECIPE_FOR_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case RECIPE:
                returnCursor = db.query(
                        Contract.PATH_RECIPE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case RECIPE_FOR_ID:
                returnCursor = db.query(
                        Contract.PATH_RECIPE,
                        projection,
                        Contract.RECIPE.COLUMN_RECIPE_ID + " = ?",
                        new String[]{Contract.RECIPE.getMovieIDFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );

                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        Context context = getContext();
        if (context != null) {
            returnCursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;

        switch (uriMatcher.match(uri)) {
            case RECIPE:
                db.insert(
                        Contract.PATH_RECIPE,
                        null,
                        values
                );
                returnUri = Contract.RECIPE.URI;
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;

        if (null == selection) {
            selection = "1";
        }
        switch (uriMatcher.match(uri)) {
            case RECIPE:
                rowsDeleted = db.delete(
                        Contract.PATH_RECIPE,
                        selection,
                        selectionArgs
                );

                break;

            case RECIPE_FOR_ID:
                String symbol = Contract.RECIPE.getMovieIDFromUri(uri);
                rowsDeleted = db.delete(
                        Contract.PATH_RECIPE,
                        '"' + symbol + '"' + " =" + Contract.RECIPE.COLUMN_RECIPE_NAME,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        if (rowsDeleted != 0) {
            Context context = getContext();
            if (context != null) {
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int returnUri;

        switch (uriMatcher.match(uri)) {
            case RECIPE_FOR_ID:
                returnUri = db.update(
                        Contract.PATH_RECIPE,
                        values,
                        selection,
                        selectionArgs
                );

                break;

            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int returnCount;
        Context context;
        switch (uriMatcher.match(uri)) {

            case RECIPE:
                returnCount = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long valueReturned = db.insert(
                                Contract.PATH_RECIPE,
                                null,
                                value
                        );

                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                }


                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }


    }

}