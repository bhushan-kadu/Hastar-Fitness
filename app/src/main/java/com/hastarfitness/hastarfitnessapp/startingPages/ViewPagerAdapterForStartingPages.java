package com.hastarfitness.hastarfitnessapp.startingPages;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hastarfitness.hastarfitnessapp.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class ViewPagerAdapterForStartingPages extends FragmentStateAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.title_activity_get_gender,
            R.string.enter_age_text,
            R.string.title_activity_get_height,
            R.string.title_activity_get_weight,
            R.string.title_activity_get_goal_weight,
            R.string.title_activity_get_activity_level};


    public ViewPagerAdapterForStartingPages(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        switch(position) {

            case 0: return new FragmentGetGender();
            case 1: return new FragmentGetAge();
            case 2: return new FragmentGetHeight();
            case 3: return new FragmentGetWeight();
            case 4: return new FragmentGetGoalWeight();
            case 5: return new FragmentGetActivityLevel();
            case 6: return new FragmentGetWeeklyGoal();
            case 7: return new FragmentAppInfoPage();
        }
        return null;
    }


    @Override
    public int getItemCount() {
        // Show 7 total pages.
        return 8;
    }
}