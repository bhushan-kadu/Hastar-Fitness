package com.hastarfitness.hastarfitnessapp.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class ViewPagerAdapter(fa: FragmentManager): FragmentStatePagerAdapter(fa) {


//    override fun getItemCount(): Int {
//        return 2
//    }
//
//    override fun createFragment(position: Int): Fragment {
//        var fragments: Fragment? = null
//        when (position) {
//            0 -> return  ReportsFragment.newInstance("","")
//            1 -> return FitnessDataFragment()
//        }
//       return fragments!!
//
//    }



    override fun getItem(position: Int): Fragment {
        var fragments: Fragment? = null
        when (position) {
            0 -> return  ReportsFragment()
            1 -> return FitnessDataFragment()
        }
        return fragments!!
    }

    override fun getCount(): Int {
        return 2
    }
    var list = listOf<String>("Reports", "Fitness Data")
    override fun getPageTitle(position: Int): CharSequence? {
        return list[position]
    }
}