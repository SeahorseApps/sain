package com.sain.azmoon.events;

public interface TokenReceivedListener<TokenResponseType, AuthorizationException>
{
    void onReceive(TokenResponseType resp, AuthorizationException ex);
}
