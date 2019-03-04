package com.example.nescara.myapplication.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentsAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    public FragmentsAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
    }

    public void addFragment(Fragment fragment){
        fragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        return fragments.get(position);
    }

    @Override
    public int getCount() {
            return fragments.size();
        }
}

