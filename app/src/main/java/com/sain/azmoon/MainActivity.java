package com.sain.azmoon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sain.azmoon.service.AppAuthAuthentication;
import com.sain.azmoon.helpers.AppLog;
import com.sain.azmoon.helpers.NukeSSLCerts;
import com.sain.azmoon.helpers.Utils;
import com.sain.azmoon.service.IAuthenticationService;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.TokenResponse;

public class MainActivity extends AppCompatActivity
{
    private final String TAG = "MainActivity";

    private boolean hasTokenRefreshed=false;
    private IAuthenticationService<AuthorizationResponse, TokenResponse, AuthorizationException> auth;

    private TextView welcomeText;
    private Button loginButton;
    private ProgressBar loadingPB;
    private TextView endpointSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NukeSSLCerts.nuke();    //Ignore SSL certificate errors. TODO:remove at release
        assignUiElements();
        tryAuthenticate();
    }

    private void assignUiElements()
    {
        welcomeText = findViewById(R.id.welcomeText);
        loginButton = findViewById(R.id.loginButton);
        loadingPB = findViewById(R.id.loadingProgressbar);
        endpointSettingsButton = findViewById(R.id.endpointSettingsButton);

        endpointSettingsButton.setOnClickListener(v ->
        {
            Intent intent = new Intent(MainActivity.this, EndpointSettingsActivity.class);
            startActivity(intent);
        });
    }

    private void tryAuthenticate()
    {
        auth = new AppAuthAuthentication(this);
        auth.setOnTokenReceivedListener(this::tokenReceived);

        if (auth.isAuthorized())     //Previous authentication data exists and not expired
        {
            AppLog.i(TAG, "Found valid authentication. Requesting token");

            auth.requestTokenRefresh();
            setUI(1);
        }

        else    //No saved authentication data or expired
        {
            AppLog.i(TAG, "No valid authentication was found. Waiting for user to login");

            setUI(0);
            loginButton.setOnClickListener(this::loginButtonClicked);
        }
    }

    private void loginButtonClicked(View v)
    {
        setUI(1);
        startActivityForResult(auth.getAuthBrowserIntent(), AppAuthAuthentication.AUTH_RESPONSE_CODE);
    }

    private void setUI(int state)
    {
        if (state == 0)    //User not logged in
        {
            welcomeText.setText(getResources().getString(R.string.welcome_guest));
            loginButton.setVisibility(View.VISIBLE);
            loadingPB.setVisibility(View.INVISIBLE);
        }

        else if (state == 1)  //Logging user in
        {
            welcomeText.setText(getResources().getString(R.string.loading));
            loginButton.setVisibility(View.INVISIBLE);
            loadingPB.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppAuthAuthentication.AUTH_RESPONSE_CODE)   //Return from browser login
        {
            AuthorizationResponse resp = AuthorizationResponse.fromIntent(data);
            AuthorizationException ex = AuthorizationException.fromIntent(data);

            auth.updateStateWithNewAuthorization(this, resp, ex);

            if (resp != null)   //User has logged in
            {
                AppLog.i(TAG, "Response from browser : AuthCode=" + resp.authorizationCode);
                AppLog.i(TAG, "Requesting Access Token now");

                auth.requestTokenExchange(resp);
            }
            else
            {
                AppLog.i(TAG, "Response from browser : did not log in");
                setUI(0);
            }
        }
    }

    private void tokenReceived(TokenResponse resp, AuthorizationException ex)
    {
        if (resp != null)  //Request was successful
        {
            AppLog.i(TAG, "Token Received : AccessToken=" + resp.accessToken);
            AppLog.i(TAG, "Token Received : RefreshToken=" + resp.refreshToken);

            auth.updateStateWithNewToken(this, resp, ex);

            //Refresh token To get full profile info
            if(!hasTokenRefreshed)
            {
                hasTokenRefreshed = true;
                auth.requestTokenRefresh();
                return;
            }

            Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            if (ex != null)
            {
                AppLog.e(TAG, "Error getting token. Message=" + ex.error);
                AppLog.e(TAG, "Error getting token. Details=" + ex.errorDescription);
            }

            setUI(0);
            Utils.showMessageBoxOK(this, "خطا", "خطا در دریافت اطلاعات", (dlg, w) -> dlg.dismiss());
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        auth.dispose();
    }
}