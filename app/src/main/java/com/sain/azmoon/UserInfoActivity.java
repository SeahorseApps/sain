package com.sain.azmoon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Debug;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.sain.azmoon.auth.Authentication;
import com.sain.azmoon.fragments.EndpointSettingsFragment;
import com.sain.azmoon.fragments.MainPagerAdapter;
import com.sain.azmoon.fragments.ProfileFragment;
import com.sain.azmoon.helpers.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserInfoActivity extends AppCompatActivity
{
    private final String TAG = "UserInfo";

    private ViewPager fragmentsViewPager;
    private ImageView profileButton;
    private ImageView endpointsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(this::profileButtonClicked);
        endpointsButton = findViewById(R.id.settingsButton);
        endpointsButton.setOnClickListener(this::endpointsButtonClicked);

        fragmentsViewPager = findViewById(R.id.mainContentContainer);
        initViewPager(fragmentsViewPager);
    }

    private void initViewPager(ViewPager pager)
    {
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        ProfileFragment profile=new ProfileFragment();
        EndpointSettingsFragment endpoints=new EndpointSettingsFragment();

        adapter.addFragment(profile);
        adapter.addFragment(endpoints);

        pager.setAdapter(adapter);
    }

    public void setFragmentView(int id)
    {
        fragmentsViewPager.setCurrentItem(id);
    }

    private void profileButtonClicked(View v)
    {
        setFragmentView(0);
    }

    private void endpointsButtonClicked(View v)
    {
        setFragmentView(1);
    }
}