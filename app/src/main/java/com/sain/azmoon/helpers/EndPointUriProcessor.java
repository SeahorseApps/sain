package com.sain.azmoon.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.sain.azmoon.R;
import com.sain.azmoon.models.EndpointDataModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public final class EndPointUriProcessor
{
    private static final String TAG="EndPointUriProcessor";
    private static final String prefName = "EndPoints";

    public static final int TokenEndPoint_ID = 1;
    public static final int IntrospectEndPoint_ID = 2;
    public static final int AuthenticationEndPoint_ID = 3;
    public static final int UserInfoEndPoint_ID = 4;
    public static final int RevokeTokenEndPoint_ID = 5;

    private static final Map<Integer, String> endpoints=new HashMap<Integer, String>()
    {{
        put(TokenEndPoint_ID, "TokenEndPoint");
        put(IntrospectEndPoint_ID, "IntrospectEndPoint");
        put(AuthenticationEndPoint_ID, "AuthenticationEndPoint");
        put(UserInfoEndPoint_ID, "UserInfoEndPoint");
        put(RevokeTokenEndPoint_ID, "RevokeTokenEndPoint");
    }};

    public static int[] getIds()
    {
        Set<Integer> keys = endpoints.keySet();
        int[] result = new int[keys.size()];
        int index = 0;

        for (Integer item : keys)
            result[index++] = item;

        return result;
    }

    public static String getEndpointName(int id)
    {
        return endpoints.get(id);
    }

    public static String getEndPointUri(Context context, int endpointId)
    {
        SharedPreferences sp = context.getSharedPreferences(prefName, MODE_PRIVATE);
        String saved = sp.getString(endpoints.get(endpointId), null);

        if (saved == null)
        {
            if (endpointId == TokenEndPoint_ID)
                return context.getResources().getString(R.string.OAuthTokenEndPoint);

            if (endpointId == IntrospectEndPoint_ID)
                return context.getResources().getString(R.string.OAuthIntrospectEndPoint);

            if (endpointId == AuthenticationEndPoint_ID)
                return context.getResources().getString(R.string.OAuthAuthenticationEndPoint);

            if (endpointId == UserInfoEndPoint_ID)
                return context.getResources().getString(R.string.OAuthUserInfoEndPoint);

            if (endpointId == RevokeTokenEndPoint_ID)
                return context.getResources().getString(R.string.OAuthRevokeTokenEndPoint);

            AppLog.e(TAG, "Endpoint Uri Not Found");
        }

        return saved;
    }

    public static void setSavedEndPoint(Context context, int endpointId, String uri)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(prefName, MODE_PRIVATE).edit();
        editor.putString(endpoints.get(endpointId), uri);
        editor.apply();
    }

    public static void setSavedEndPoint(Context context, String endpointName, String uri)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(prefName, MODE_PRIVATE).edit();
        editor.putString(endpointName, uri);
        editor.apply();
    }

    public static EndpointDataModel[] getListViewItems(Context context)
    {
        EndpointDataModel[] result = new EndpointDataModel[endpoints.size()];

        int i = 0;
        for (Map.Entry<Integer, String> entry : endpoints.entrySet())
        {
            result[i] = new EndpointDataModel(entry.getValue(), getEndPointUri(context, entry.getKey()));
            i++;
        }

        return result;
    }
}