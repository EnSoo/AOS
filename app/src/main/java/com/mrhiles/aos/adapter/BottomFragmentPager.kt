package com.mrhiles.aos.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mrhiles.aos.fragments.BottomChatFragment
import com.mrhiles.aos.fragments.BottomHomeFragment
import com.mrhiles.aos.fragments.BottomListFragment
import com.mrhiles.aos.fragments.BottomProfileFragment
import com.mrhiles.aos.fragments.BottomReservedFragment

class BottomFragmentPager(fragmentActivity : FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount()=5

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> BottomHomeFragment()
            1 -> BottomListFragment()
            2 -> BottomChatFragment()
            3 -> BottomReservedFragment()
            4 -> BottomProfileFragment()
            else -> BottomHomeFragment()
        }
    }

}