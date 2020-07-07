package com.sain.azmoon.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.sain.azmoon.R;
import com.sain.azmoon.helpers.PackageInspector;
import com.sain.azmoon.models.interfaces.AuthorizationAction;
import com.sain.azmoon.events.TokenReceivedListener;
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
import net.openid.appauth.TokenResponse;
import net.openid.appauth.browser.BrowserWhitelist;
import net.openid.appauth.browser.VersionedBrowserMatcher;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class AppAuthAuthentication implements IAuthenticationService<AuthorizationResponse, TokenResponse, AuthorizationException>
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

    private TokenReceivedListener<TokenResponse, AuthorizationException> tokenListener = null;

    public void setOnTokenReceivedListener(TokenReceivedListener<TokenResponse, AuthorizationException> listener)
    {
        this.tokenListener = listener;
    }

    public AppAuthAuthentication(Context context)
    {
        authEndPoint = EndPointUriProcessor.getEndPointUri(context, EndPointUriProcessor.AuthenticationEndPoint_ID);
        tokenEndPoint = EndPointUriProcessor.getEndPointUri(context, EndPointUriProcessor.TokenEndPoint_ID);
        clientId = context.getResources().getString(R.string.ClientKey);
        secretKey = context.getResources().getString(R.string.Secret);

        AppLog.i(TAG, "Creating Authorization Service");
        authService = new AuthorizationService(context, getConfig(context));

        serviceConfig = new AuthorizationServiceConfiguration(Uri.parse(authEndPoint), Uri.parse(tokenEndPoint));
        AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(serviceConfig, clientId, ResponseTypeValues.CODE, Uri.parse(REDIRECT_URI));

        authRequest = authRequestBuilder.setScope("openid").setPrompt("login").build();

        AppLog.i(TAG, "Checking for Saved AuthState");
        AuthState prevState = readAuthState(context);

        AppLog.i(TAG, prevState == null ? "Saved State Not Found. Creating New One" : "Saved State Found. Loading Saved Data");
        authState = prevState == null ? new AuthState(serviceConfig) : prevState;
    }

    private AppAuthConfiguration getConfig(Context context)
    {
        AppAuthConfiguration.Builder builder = new AppAuthConfiguration.Builder();

        if(PackageInspector.isChromeInstalled(context.getPackageManager()))
            builder.setBrowserMatcher(new BrowserWhitelist(VersionedBrowserMatcher.CHROME_BROWSER));

        else if(PackageInspector.isFireFoxInstalled(context.getPackageManager()))
            builder.setBrowserMatcher(new BrowserWhitelist(VersionedBrowserMatcher.FIREFOX_BROWSER));

        return builder.build();
    }

    public String getClientId()
    {
        return clientId;
    }

    public String getSecretKey()
    {
        return secretKey;
    }

    public boolean isAuthorized()
    {
        if (authState.getRefreshToken() == null)
            return false;

        return !authState.getNeedsTokenRefresh();
    }

    public Intent getAuthBrowserIntent()
    {
        return authService.getAuthorizationRequestIntent(authRequest);
    }

    public void updateStateWithNewAuthorization(Context context, AuthorizationResponse response, AuthorizationException ex)
    {
        authState.update(response, ex);
        writeAuthState(context);
    }

    public void updateStateWithNewToken(Context context, TokenResponse response, AuthorizationException ex)
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

    public void performActionWithFreshTokens(AuthorizationAction action)
    {
        authState.performActionWithFreshTokens(authService, action::run);
    }

    public void requestTokenExchange(AuthorizationResponse response)
    {
        Map<String, String> additionalParameters = new HashMap<>();
        additionalParameters.put("client_secret", secretKey);

        AppLog.i(TAG, "Requesting Token Exchange...");
        authService.performTokenRequest(response.createTokenExchangeRequest(additionalParameters), (resp, ex) ->
        {
            if (tokenListener != null)
            {
                tokenListener.onReceive(resp, ex);
            }
        });
    }

    public void requestTokenRefresh()
    {
        Map<String, String> additionalParameters = new HashMap<>();
        additionalParameters.put("client_secret", secretKey);

        AppLog.i(TAG, "Requesting RefreshToken...");
        authService.performTokenRequest(authState.createTokenRefreshRequest(additionalParameters), (resp, ex) ->
        {
            if (tokenListener != null)
            {
                tokenListener.onReceive(resp, ex);
            }
        });
    }

    private AuthState readAuthState(@NonNull Context context)
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

    private void writeAuthState(@NonNull Context context)
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
