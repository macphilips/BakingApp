package com.rmhub.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class DbHelper extends SQLiteOpenHelper {


    private static final String NAME = "bakingapp.db";
    private static final int VERSION = 1;


    DbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String movie_table = "CREATE TABLE " + Contract.PATH_RECIPE + " ("
                + Contract.RECIPE.COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY NOT NULL, "
                + Contract.RECIPE.COLUMN_RECIPE_NAME + " TEXT NOT NULL, "
                + Contract.RECIPE.COLUMN_INGREDIENTS + " TEXT NOT NULL, "
                + Contract.RECIPE.COLUMN_STEPS + " TEXT NOT NULL, "
                + Contract.RECIPE.COLUMN_SERVINGS + " TEXT NOT NULL, "
                + Contract.RECIPE.COLUMN_IMAGE + " TEXT NOT NULL"
                + ");";
        db.execSQL(movie_table);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        /*
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys = ON;");
        }*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + Contract.PATH_RECIPE);
        onCreate(db);
    }
}
