package com.rmhub.bakingapp.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.rmhub.bakingapp.BakingApp;

import org.apache.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.util.Map;

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

    private static final String DEFAULT_CACHE_DIR = "volley";

    private static boolean checkConnection() {
        Context context = BakingApp.getInstance();
        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting() || !isInternetAvailable()) {
            Log.e(NetworkUtil.class.getSimpleName(), "checkConnection - no connection found");
            return false;
        }
        return true;
    }
    private static boolean isInternetAvailable() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            return (returnVal == 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;

    }

    public static synchronized NetworkUtil getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkUtil();
        }
        return mInstance;
    }

    public void fetchResult(RecipeRequest.MovieRequestListener listener) {
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


    public static RequestQueue newRequestQueue(Context context) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }

        HurlStack stack;
        stack = new MyHurlStack();

        Network network = new BasicNetwork(stack);

        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        queue.start();

        return queue;
    }

    private void addToRequestQueue(RecipeRequest req) {
        getRequestQueue().add(req);
    }

    private static class MyHurlStack extends HurlStack {
        @Override
        public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
            if (!checkConnection()) {
                throw new IOException("No Internet Connection.");
              /* return new BasicHttpResponse(new StatusLine() {
                    @Override
                    public ProtocolVersion getProtocolVersion() {
                        return HttpVersion.HTTP_1_1;
                    }

                    @Override
                    public int getStatusCode() {
                        return 405;
                    }

                    @Override
                    public String getReasonPhrase() {
                        return "No Internet Connection.";
                    }
                });*/
            }
            return super.performRequest(request, additionalHeaders);
        }
    }
}
