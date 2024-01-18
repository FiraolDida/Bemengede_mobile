package com.example.bemengede;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class JSONRequestHelper {
    private static JSONRequestHelper jsonRequestHelper;
    private RequestQueue requestQueue;
    private static Context context;

    public JSONRequestHelper(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue(){
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized JSONRequestHelper getInstance(Context context){
        if (jsonRequestHelper == null){
            jsonRequestHelper = new JSONRequestHelper(context);
        }
        return jsonRequestHelper;
    }

    public<T> void addToRequest(Request<T> request){
        requestQueue.add(request);
    }
}
