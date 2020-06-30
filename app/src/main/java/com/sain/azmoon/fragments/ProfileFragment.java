package com.sain.azmoon.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.EventLogTags;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.sain.azmoon.MainActivity;
import com.sain.azmoon.R;
import com.sain.azmoon.UserInfoActivity;
import com.sain.azmoon.auth.Authentication;
import com.sain.azmoon.helpers.AppLog;
import com.sain.azmoon.helpers.EndPointUriProcessor;
import com.sain.azmoon.helpers.Utils;
import com.sain.azmoon.helpers.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment
{
    private final String TAG = "ProfileFragment";

    private TextView usernameText;
    private TextView firstNameText;
    private TextView lastNameText;
    private TextView organizationText;
    private TextView countryText;
    private TextView emailText;
    private TextView phoneText;
    private TextView mobileText;
    private TextView departmentText;
    private TextView roleText;
    private Button logoutButton;

    private Authentication auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        initUi(view);

        auth = new Authentication(Objects.requireNonNull(getActivity()));
        fetchUserInfo();

        return view;
    }

    private void initUi(View view)
    {
        usernameText = view.findViewById(R.id.usernameText);
        firstNameText = view.findViewById(R.id.firstNameText);
        lastNameText = view.findViewById(R.id.lastNameText);
        organizationText = view.findViewById(R.id.organizationText);
        countryText = view.findViewById(R.id.countryText);
        emailText = view.findViewById(R.id.emailText);
        phoneText = view.findViewById(R.id.phoneText);
        mobileText = view.findViewById(R.id.mobileText);
        departmentText = view.findViewById(R.id.departmentText);
        roleText = view.findViewById(R.id.roleText);
        logoutButton = view.findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(this::logoutButtonClicked);
    }

    private void fetchUserInfo()
    {
        StringRequest request = new StringRequest(Request.Method.POST, EndPointUriProcessor.getOAuthUserInfoEndPoint(getActivity()),
                response ->
                {
                    if (response != null)
                    {
                        AppLog.i(TAG, "Successfully Received Profile Info");
                        AppLog.i(TAG, "User Profile JSON:" + response);

                        try
                        {
                            JSONObject json = new JSONObject(response);

                            if (json.has("sub"))
                                usernameText.setText(json.getString("sub"));

                            if (json.has("country"))
                                countryText.setText(json.getString("country"));

                            if (json.has("groups"))
                                roleText.setText(json.getString("groups"));

                            if (json.has("given_name"))
                                firstNameText.setText(json.getString("given_name"));

                            if (json.has("family_name"))
                                lastNameText.setText(json.getString("family_name"));
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                error ->
                {
                    AppLog.i(TAG, "Error Fetching Profile Info");

                    if (error != null)
                        AppLog.e(TAG, "Error Message: " + (error.getMessage() != null ? error.getMessage() : "NULL"));
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + auth.getAccessToken());

                return headers;
            }
        };

        AppLog.i(TAG, "Requesting User Profile Info");
        VolleySingleton.getInstance(getActivity()).addToQueue(request);
    }

    private void logoutButtonClicked(View v)
    {
        Utils.showMessageBoxYesNo(getActivity(), "خروج", "میخواهید خارج شوید؟",
                (dlg, w) ->
                {
                    StringRequest request = new StringRequest(Request.Method.POST, EndPointUriProcessor.getOAuthRevokeTokenEndPoint(getActivity()),
                            response ->
                            {
                                AppLog.i(TAG, "Successfully Revoked The Token");
                                AppLog.i(TAG, "Clearing Saved AuthState...");

                                auth.resetState(getActivity());

                                AppLog.i(TAG, "Returning to Start Activity");
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);

                                Objects.requireNonNull(getActivity()).finish();
                            },
                            error ->
                            {
                                AppLog.e(TAG, "Error Revoking Token");

                                if (error != null)
                                    AppLog.e(TAG, "Error Message: " + (error.getMessage() != null ? error.getMessage() : "NULL"));
                                else
                                    AppLog.e(TAG, "Error is NULL");
                            })
                    {
                        @Override
                        public String getBodyContentType()
                        {
                            return "application/x-www-form-urlencoded; charset=UTF-8";
                        }

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError
                        {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("token", auth.getCurrentAuthState().getAccessToken());

                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders()
                        {
                            String credentials = auth.getClientId() + ":" + auth.getSecretKey();
                            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

                            final Map<String, String> headers = new HashMap<>();
                            headers.put("Authorization", "Basic " + base64EncodedCredentials);
                            headers.put("Content-Type", "application/x-www-form-urlencoded");

                            return headers;
                        }
                    };

                    AppLog.i(TAG, "Requesting Token Revoke");
                    VolleySingleton.getInstance(getActivity()).addToQueue(request);

                    dlg.dismiss();
                },
                (dlg, w) ->
                {
                    dlg.dismiss();
                });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        auth.dispose();
    }
}
