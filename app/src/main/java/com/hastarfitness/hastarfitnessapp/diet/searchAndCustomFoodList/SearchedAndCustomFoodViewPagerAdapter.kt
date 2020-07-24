package com.hastarfitness.hastarfitnessapp.diet.searchAndCustomFoodList

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.hastarfitness.hastarfitnessapp.diet.searchAndCustomFoodList.myFoods.FragmentMyFoods
import com.hastarfitness.hastarfitnessapp.diet.searchAndCustomFoodList.recentlySearched.FragmentLastSearchedFood


class SearchedAndCustomFoodViewPagerAdapter(private val fa: FragmentManager): FragmentStatePagerAdapter(fa) {
    var list:List<String> = listOf<String>("LAST SEARCHED", "MY FOODS")


    override fun getItem(position: Int): Fragment {
        var fragments: Fragment? = null
        when (position) {
            0 -> return FragmentLastSearchedFood()
            1 -> return FragmentMyFoods()
        }
        return fragments!!
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return list[position]

    }
}