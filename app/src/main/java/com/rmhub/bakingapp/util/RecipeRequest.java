package com.rmhub.bakingapp.util;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;
import com.rmhub.bakingapp.model.Recipe;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by MOROLANI on 4/10/2017
 * <p>
 * owm
 * .
 */

public class RecipeRequest extends Request<List<Recipe>> {
    private static final String TAG = RecipeRequest.class.getSimpleName();
    private final Response.Listener<List<Recipe>> listener;

    RecipeRequest(MovieRequestListener listener) {
        super(Method.GET, (NetworkUtil.QUERY_URL), listener);
        Log.d(getClass().getSimpleName(), "http requestUrl - " + (NetworkUtil.QUERY_URL));
        this.listener = listener;
    }

    @Override
    protected Response<List<Recipe>> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.d(TAG, "response => " + json);
            List<Recipe> obj = Recipe.getRecipeFromJson(json);
            ProviderUtil.insertRecipes(obj);
            return Response.success(obj, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(List<Recipe> response) {
        listener.onResponse(response);
    }

    public static class MovieRequestListener implements Response.Listener<List<Recipe>>, Response.ErrorListener {


        @Override
        public void onErrorResponse(VolleyError error) {

        }

        @Override
        public void onResponse(List<Recipe> response) {

        }

        public void onNetworkError() {

        }
    }

}
