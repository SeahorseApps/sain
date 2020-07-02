package com.sain.azmoon.models.interfaces;

public interface AuthorizationAction
{
    void run(String accessToken, String idToken, Exception ex);
}
