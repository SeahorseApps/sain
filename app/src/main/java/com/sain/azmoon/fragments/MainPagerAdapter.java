package com.sain.azmoon.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter
{
    private List<Fragment> fragments = new ArrayList<>();

    public MainPagerAdapter(@NonNull FragmentManager fm, int behavior)
    {
        super(fm, behavior);
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragments.get(position);
    }

    @Override
    public int getCount()
    {
        return fragments.size();
    }

    public void addFragment(Fragment newFragment)
    {
        fragments.add(newFragment);
    }
}
