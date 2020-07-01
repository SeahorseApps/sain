package com.sain.azmoon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.sain.azmoon.fragments.MainPagerAdapter;
import com.sain.azmoon.fragments.ProfileFragment;

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
        adapter.addFragment(profile);
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