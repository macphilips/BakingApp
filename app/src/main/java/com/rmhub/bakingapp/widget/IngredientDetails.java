package com.rmhub.bakingapp.widget;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.rmhub.bakingapp.R;
import com.rmhub.bakingapp.model.Ingredient;
import com.rmhub.bakingapp.model.Recipe;
import com.rmhub.bakingapp.ui.adapters.RecipeDetailListAdapter;
import com.rmhub.bakingapp.util.ProviderUtil;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IngredientDetails extends AppCompatActivity {
    @BindView(R.id.ingredients_list)
    TextView tv;
    @BindView(R.id.recipe_name)
    TextView recipe_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_details);
        ButterKnife.bind(this);
        Log.d("IngredientDetails", "onCreate");
        Recipe recipe = ProviderUtil.getRecipeByID(this, getIntent().getData());
        if (recipe != null) {
            recipe_name.setText(recipe.getName());
            String text = "<h2>Ingredients:</h2>" +
                    // "<br/>" +
                    "<p>"
                    + getIngredientText(recipe) +
                    "</p>";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tv.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
            } else {
                tv.setText(Html.fromHtml(text));

            }
        }


    }

    private String getIngredientText(Recipe mValues) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (Ingredient ingredient : mValues.getIngredients()) {
            String s = String.format(Locale.getDefault(), "%d. %s %s of %s", ++i,
                    ingredient.getQuantity(), ingredient.getMeasure(), ingredient.getIngredient());

            if (i <= mValues.getIngredients().size() - 1) {
                builder.append(s).append(".<br/>");
            } else {
                builder.append(s).append(".");
            }
        }
        Log.d(RecipeDetailListAdapter.class.getSimpleName(), builder.toString());
        return builder.toString();
    }

    @OnClick(R.id.close_button)
    void close() {
        finish();
    }

}
