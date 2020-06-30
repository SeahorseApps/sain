package com.sain.azmoon.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public final class LocalSavedData
{
    public static String getRefreshToken(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences("auth", MODE_PRIVATE);

        return sp.getString("refreshToken", null);
    }

    public static void setRefreshToken(Context context, String token)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences("auth", MODE_PRIVATE).edit();
        editor.putString("refreshToken", token);
        editor.apply();
    }
}
