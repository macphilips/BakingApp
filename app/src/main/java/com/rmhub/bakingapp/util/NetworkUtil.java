package com.rmhub.bakingapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.rmhub.bakingapp.BakingApp;

/**
 * Created by MOROLANI on 3/27/2017
 * <p>
 * owm
 * .
 */

public class NetworkUtil {

    static final String QUERY_URL = "https://go.udacity.com/android-baking-app-json";
    private final static String TAG = NetworkUtil.class.getSimpleName();
    private static NetworkUtil mInstance;

    private RequestQueue mRequestQueue;

    private NetworkUtil() {
        mRequestQueue = getRequestQueue();
    }

    /**
     * Simple network connection check.
     *
     * @param context
     */
    private static boolean checkConnection(Context context) {
        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            Log.e(NetworkUtil.class.getSimpleName(), "checkConnection - no connection found");
            return false;
        }
        return true;
    }

    public static synchronized NetworkUtil getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkUtil();
        }
        return mInstance;
    }

    public void fetchResult(RecipeRequest.MovieRequestListener listener) {
        if (!checkConnection(BakingApp.getInstance())) {
            listener.onNetworkError();
            return;
        }
        RecipeRequest request = new RecipeRequest(listener);
        request.setTag(QUERY_URL);
        addToRequestQueue(request);
    }

    public void cancelRequest() {
        mRequestQueue.cancelAll(QUERY_URL);
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(BakingApp.getInstance());
        }
        return mRequestQueue;
    }

    private void addToRequestQueue(RecipeRequest req) {
        getRequestQueue().add(req);
    }
}
