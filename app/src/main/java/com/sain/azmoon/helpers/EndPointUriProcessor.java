package com.sain.azmoon.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.sain.azmoon.R;

import static android.content.Context.MODE_PRIVATE;

public final class EndPointUriProcessor
{
    private static final String prefName = "EndPoints";
    private static final String oAuthTokenEndPoint_ID = "OAuthTokenEndPoint";
    private static final String oAuthIntrospectEndPoint_ID = "OAuthIntrospectEndPoint";
    private static final String oAuthAuthenticationEndPoint_ID = "OAuthAuthenticationEndPoint";
    private static final String oAuthUserInfoEndPoint_ID = "OAuthUserInfoEndPoint";
    private static final String oAuthRevokeTokenEndPoint_ID = "OAuthRevokeTokenEndPoint";

    public static String getOAuthTokenEndPoint(Context context)
    {
        String saved = getSavedEndPoint(context, oAuthTokenEndPoint_ID);

        return saved == null ? context.getResources().getString(R.string.OAuthTokenEndPoint) : saved;
    }

    public static void setOAuthTokenEndPoint(Context context, String uri)
    {
        setSavedEndPoint(context, oAuthTokenEndPoint_ID, uri);
    }

    public static String getOAuthIntrospectEndPoint(Context context)
    {
        String saved = getSavedEndPoint(context, oAuthIntrospectEndPoint_ID);

        return saved == null ? context.getResources().getString(R.string.OAuthIntrospectEndPoint) : saved;
    }

    public static void setOAuthIntrospectEndPoint(Context context, String uri)
    {
        setSavedEndPoint(context, oAuthIntrospectEndPoint_ID, uri);
    }

    public static String getOAuthAuthenticationEndPoint(Context context)
    {
        String saved = getSavedEndPoint(context, oAuthAuthenticationEndPoint_ID);

        return saved == null ? context.getResources().getString(R.string.OAuthAuthenticationEndPoint) : saved;
    }

    public static void setOAuthAuthenticationEndPoint(Context context, String uri)
    {
        setSavedEndPoint(context, oAuthAuthenticationEndPoint_ID, uri);
    }

    public static String getOAuthUserInfoEndPoint(Context context)
    {
        String saved = getSavedEndPoint(context, oAuthUserInfoEndPoint_ID);

        return saved == null ? context.getResources().getString(R.string.OAuthUserInfoEndPoint) : saved;
    }

    public static void setOAuthUserInfoEndPoint(Context context, String uri)
    {
        setSavedEndPoint(context, oAuthUserInfoEndPoint_ID, uri);
    }

    public static String getOAuthRevokeTokenEndPoint(Context context)
    {
        String saved = getSavedEndPoint(context, oAuthRevokeTokenEndPoint_ID);

        return saved == null ? context.getResources().getString(R.string.OAuthRevokeTokenEndPoint) : saved;
    }

    public static void setOAuthRevokeTokenEndPoint(Context context, String uri)
    {
        setSavedEndPoint(context, oAuthRevokeTokenEndPoint_ID, uri);
    }

    private static String getSavedEndPoint(Context context, String endpoint)
    {
        SharedPreferences sp = context.getSharedPreferences(prefName, MODE_PRIVATE);
        return sp.getString(endpoint, null);
    }

    private static void setSavedEndPoint(Context context, String endpoint, String uri)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(prefName, MODE_PRIVATE).edit();
        editor.putString(endpoint, uri);
        editor.apply();
    }
}
