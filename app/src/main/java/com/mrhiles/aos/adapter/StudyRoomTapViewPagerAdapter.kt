package com.mrhiles.aos.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mrhiles.aos.fragments.BottomHomeTapFavorFragment
import com.mrhiles.aos.fragments.BottomHomeTapListFragment

class StudyRoomTapViewPagerAdapter(val fragmentActivity:FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount()=2
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> BottomHomeTapListFragment()
            1 -> BottomHomeTapFavorFragment()
            else -> BottomHomeTapListFragment()
        }

    }

}