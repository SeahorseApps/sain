package com.sain.azmoon.helpers;

import android.util.Log;

public class AppLog
{
    private static final boolean isDebug = true;

    public static void i(String tag, String msg)
    {
        if (isDebug)
        {
            Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg)
    {
        if (isDebug)
        {
            Log.e(tag, msg);
        }
    }
}
