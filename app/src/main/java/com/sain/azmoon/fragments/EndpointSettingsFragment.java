package com.sain.azmoon.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sain.azmoon.R;
import com.sain.azmoon.helpers.EndPointUriProcessor;
import com.sain.azmoon.helpers.Utils;

public class EndpointSettingsFragment extends Fragment
{
    private EditText tokenInput;
    private EditText introspectInput;
    private EditText authenticationInput;
    private EditText userInfoInput;
    private EditText revokeInput;
    private Button saveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_endpoint_settings, container, false);
        initUi(view);

        return view;
    }

    private void initUi(View view)
    {
        tokenInput = view.findViewById(R.id.tokenEndpointInput);
        introspectInput = view.findViewById(R.id.introspectInput);
        authenticationInput = view.findViewById(R.id.authenticationEndpointInput);
        userInfoInput = view.findViewById(R.id.userinfoEndpointInput);
        revokeInput = view.findViewById(R.id.revokeEndpointInput);
        saveButton = view.findViewById(R.id.saveButton);

        tokenInput.setText(EndPointUriProcessor.getOAuthTokenEndPoint(getActivity()));
        introspectInput.setText(EndPointUriProcessor.getOAuthIntrospectEndPoint(getActivity()));
        authenticationInput.setText(EndPointUriProcessor.getOAuthAuthenticationEndPoint(getActivity()));
        userInfoInput.setText(EndPointUriProcessor.getOAuthUserInfoEndPoint(getActivity()));
        revokeInput.setText(EndPointUriProcessor.getOAuthRevokeTokenEndPoint(getActivity()));

        saveButton.setOnClickListener(this::saveButtonClicked);
    }

    private void saveButtonClicked(View v)
    {
        Utils.showMessageBoxYesNo(getActivity(), "ذخیره", "مقادیر ذخیره شوند؟",
                (dlg, w) ->
                {
                    EndPointUriProcessor.setOAuthTokenEndPoint(getActivity(), tokenInput.getText().toString());
                    EndPointUriProcessor.setOAuthIntrospectEndPoint(getActivity(), introspectInput.getText().toString());
                    EndPointUriProcessor.setOAuthAuthenticationEndPoint(getActivity(), authenticationInput.getText().toString());
                    EndPointUriProcessor.setOAuthUserInfoEndPoint(getActivity(), userInfoInput.getText().toString());
                    EndPointUriProcessor.setOAuthRevokeTokenEndPoint(getActivity(), revokeInput.getText().toString());

                    dlg.dismiss();
                },
                (dlg, w) ->
                {
                    dlg.dismiss();
                });
    }
}
