package com.rmhub.bakingapp.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.rmhub.bakingapp.data.Contract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MOROLANI on 5/24/2017
 * <p>
 * owm
 * .
 */

public class Recipe implements Parcelable {
    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @SerializedName("ingredients")
    @Expose
    private List<Ingredient> ingredients;

    @SerializedName("steps")
    @Expose
    private List<Step> steps;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("servings")
    @Expose
    private int servings;

    @SerializedName("name")
    @Expose
    private String name;

    public Recipe() {
        ingredients = new ArrayList<>();
        steps = new ArrayList<>();
    }

    public static
    @Nullable
    Recipe buildFromCursor(Cursor data) {
        try {
            Recipe recipe = new Recipe();
            recipe.setId(data.getInt(data.getColumnIndexOrThrow(Contract.RECIPE.COLUMN_RECIPE_ID)));
            recipe.setName(data.getString(data.getColumnIndexOrThrow(Contract.RECIPE.COLUMN_RECIPE_NAME)));
            recipe.setServings(data.getInt(data.getColumnIndexOrThrow(Contract.RECIPE.COLUMN_SERVINGS)));
            recipe.setImage(data.getString(data.getColumnIndexOrThrow(Contract.RECIPE.COLUMN_IMAGE)));
            recipe.setSteps(Step.getStepFromJson(data.getString(data.getColumnIndexOrThrow(Contract.RECIPE.COLUMN_STEPS))));
            recipe.setIngredients(Ingredient.getIngredientsFromJson(data.getString(data.getColumnIndexOrThrow(Contract.RECIPE.COLUMN_INGREDIENTS))));
            return recipe;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Recipe(Parcel in) {
        ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        steps = in.createTypedArrayList(Step.CREATOR);
        image = in.readString();
        id = in.readInt();
        servings = in.readInt();
        name = in.readString();
    }

    public static List<Recipe> getRecipeFromJson(String json) {
        return new Gson().fromJson(json, new TypeToken<List<Recipe>>() {
        }.getType());
    }

    public String getJsonString() {
        return new Gson().toJson(this);
    }


    public String getIngredientsJsonString() {
        return new Gson().toJson(ingredients, new TypeToken<List<Ingredient>>() {
        }.getType());
    }

    public String getStepsJsonString() {
        return new Gson().toJson(steps, new TypeToken<List<Step>>() {
        }.getType());
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(ingredients);
        dest.writeTypedList(steps);
        dest.writeString(image);
        dest.writeInt(id);
        dest.writeInt(servings);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                // "ingredients=" + ingredients +
                // ", steps=" + steps +
                ", image='" + image + '\'' +
                ", id=" + id +
                ", servings=" + servings +
                ", name='" + name + '\'' +
                '}';
    }
}
