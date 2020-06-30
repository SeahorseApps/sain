package com.sain.azmoon.helpers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton
{
    private static VolleySingleton instance;
    private RequestQueue queue;
    private Context ctx;

    private VolleySingleton(Context context)
    {
        ctx = context;
    }

    public static synchronized VolleySingleton getInstance(Context context)
    {
        if (instance == null)
            instance = new VolleySingleton(context);

        return instance;
    }

    public RequestQueue getQueue()
    {
        if (queue == null)
            queue = Volley.newRequestQueue(ctx.getApplicationContext());

        return queue;
    }

    public <T> void addToQueue(Request<T> req)
    {
        getQueue().add(req);
    }
}