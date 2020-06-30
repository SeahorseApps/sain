package com.sain.azmoon.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.sain.azmoon.MainActivity;
import com.sain.azmoon.R;
import com.sain.azmoon.helpers.AppLog;
import com.sain.azmoon.helpers.EndPointUriProcessor;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;
import net.openid.appauth.connectivity.ConnectionBuilder;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

public class Authentication
{
    private static final String TAG = "AUTH";

    public static final int AUTH_RESPONSE_CODE = 130;
    private static final String REDIRECT_URI = "sain://com.sain.azmoon";

    private final String authEndPoint;
    private final String tokenEndPoint;
    private final String clientId;
    private final String secretKey;

    private AuthorizationServiceConfiguration serviceConfig = null;
    private AuthorizationRequest authRequest = null;
    private AuthState authState = null;
    private AuthorizationService authService = null;

    private TokenReceivedListener tokenListener = null;

    public interface TokenReceivedListener
    {
        void onFinish(TokenResponse resp, AuthorizationException ex);
    }

    public void setOnTokenReceivedListener(TokenReceivedListener listener)
    {
        this.tokenListener = listener;
    }

    public Authentication(Context context)
    {
        authEndPoint = EndPointUriProcessor.getOAuthAuthenticationEndPoint(context);
        tokenEndPoint = EndPointUriProcessor.getOAuthTokenEndPoint(context);
        clientId = context.getResources().getString(R.string.OAuthClientKey);
        secretKey = context.getResources().getString(R.string.OAuthSecret);

        AppLog.i(TAG, "Creating Authorization Service");
        authService = new AuthorizationService(context);

        serviceConfig = new AuthorizationServiceConfiguration(Uri.parse(authEndPoint), Uri.parse(tokenEndPoint));
        AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(serviceConfig, clientId, ResponseTypeValues.CODE, Uri.parse(REDIRECT_URI));

        authRequest = authRequestBuilder.setScope("openid").setPrompt("login").build();

        AppLog.i(TAG, "Checking for Saved AuthState");
        AuthState prevState = readAuthState(context);

        AppLog.i(TAG, prevState == null ? "Saved State Not Found. Creating New One" : "Saved State Found. Loading Saved Data");
        authState = prevState == null ? new AuthState(serviceConfig) : prevState;
    }

    public String getClientId()
    {
        return clientId;
    }

    public String getSecretKey()
    {
        return secretKey;
    }

    public boolean isAuthStateValid()
    {
        if (authState.getRefreshToken() == null)
            return false;

        return !authState.getNeedsTokenRefresh();
    }

    public String getAccessToken()
    {
        return authState.getAccessToken();
    }

    public AuthState getCurrentAuthState()
    {
        return authState;
    }

    public Intent getAuthIntent()
    {
        return authService.getAuthorizationRequestIntent(authRequest);
    }

    public void updateState(Context context, AuthorizationResponse response, AuthorizationException ex)
    {
        authState.update(response, ex);
        writeAuthState(context);
    }

    public void updateState(Context context, TokenResponse response, AuthorizationException ex)
    {
        authState.update(response, ex);
        writeAuthState(context);
    }

    public void resetState(Context context)
    {
        authState = new AuthState();
        writeAuthState(context);

        AppLog.i(TAG, "AuthState Reset");
    }

    public void requestToken(AuthorizationResponse response)
    {
        Map<String, String> additionalParameters = new HashMap<>();
        additionalParameters.put("client_secret", secretKey);

        AppLog.i(TAG, "Requesting Token Exchange...");
        authService.performTokenRequest(response.createTokenExchangeRequest(additionalParameters), (resp, ex) ->
        {
            if (tokenListener != null)
            {
                tokenListener.onFinish(resp, ex);
            }
        });
    }

    public void requestToken()
    {
        Map<String, String> additionalParameters = new HashMap<>();
        additionalParameters.put("client_secret", secretKey);

        AppLog.i(TAG, "Requesting RefreshToken...");
        authService.performTokenRequest(authState.createTokenRefreshRequest(additionalParameters), (resp, ex) ->
        {
            if (tokenListener != null)
            {
                tokenListener.onFinish(resp, ex);
            }
        });
    }

    public AuthState readAuthState(@NonNull Context context)
    {
        SharedPreferences authPrefs = context.getSharedPreferences("auth", MODE_PRIVATE);
        String stateJson = authPrefs.getString("stateJson", null);

        if (stateJson != null)
        {
            try
            {
                return AuthState.jsonDeserialize(stateJson);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                return null;
            }
        }

        else
            return null;
    }

    public void writeAuthState(@NonNull Context context)
    {
        if (authState == null)
            return;

        SharedPreferences authPrefs = context.getSharedPreferences("auth", MODE_PRIVATE);
        authPrefs.edit().putString("stateJson", authState.jsonSerializeString()).apply();
    }

    public void dispose()
    {
        authService.dispose();
        authService=null;

        authRequest=null;
    }
}
