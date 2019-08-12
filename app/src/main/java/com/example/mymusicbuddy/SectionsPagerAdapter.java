package com.example.mymusicbuddy;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mymusicbuddy.R;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;
    private static final String TAG = "SectionsPagerAdapter";

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
//        Log.d(TAG, "SectionsPagerAdapter: ");
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem: position -> " + position);
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
//        Log.d(TAG, "getPageTitle: ");
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
//        Log.d(TAG, "getCount: ");
        return 3;
    }
}