package com.sain.azmoon.service;

import android.content.Context;
import android.content.Intent;

import com.sain.azmoon.models.interfaces.AuthorizationAction;
import com.sain.azmoon.events.TokenReceivedListener;

public interface IAuthenticationService<AuthorizationResponseType, TokenResponseType, AuthorizationException>
{
    String getClientId();
    String getSecretKey();

    Intent getAuthBrowserIntent();

    boolean isAuthorized();
    void updateStateWithNewAuthorization(Context context, AuthorizationResponseType response, AuthorizationException exception);
    void updateStateWithNewToken(Context context, TokenResponseType response, AuthorizationException exception);
    void resetState(Context context);

    void performActionWithFreshTokens(AuthorizationAction action);
    void requestTokenExchange(AuthorizationResponseType responseFromBrowser);
    void requestTokenRefresh();

    void setOnTokenReceivedListener(TokenReceivedListener<TokenResponseType, AuthorizationException> listener);
    void dispose();
}
