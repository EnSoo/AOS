package com.mrhiles.aos.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.mrhiles.aos.G
import com.mrhiles.aos.R
import com.mrhiles.aos.adapter.BottomFragmentPager
import com.mrhiles.aos.data.KakaoSearchStudyRoomRespnose
import com.mrhiles.aos.databinding.ActivityMainBinding
import com.mrhiles.aos.fragments.BottomChatFragment
import com.mrhiles.aos.fragments.BottomLoginFragment
import retrofit2.Call

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val pagerAdapter by lazy { BottomFragmentPager(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.pager.apply {
            adapter=pagerAdapter
            isUserInputEnabled=true
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){ override fun onPageSelected(position: Int) { binding.bnv.menu.getItem(position).isChecked=true } })
        }

        //BottomNavigation 각 4개 메뉴 클릭 시 Fragment 붙이기
        binding.bnv.setOnItemSelectedListener {
            binding.pager.visibility= ViewPager2.VISIBLE
            binding.containerFragment.visibility= FrameLayout.INVISIBLE
            when(it.itemId) {
                R.id.menu_bnv_home -> {
                    binding.pager.setCurrentItem(0)
                }
                R.id.menu_bnv_list -> binding.pager.setCurrentItem(1)
                R.id.empty -> true
                R.id.menu_bnv_reserved -> {
                    if(!G.isLogin) BottomLoginFragment().show(supportFragmentManager,"bnv_favor")
                    else binding.pager.setCurrentItem(3)
                }
                R.id.menu_bnv_profile -> {
                    if(!G.isLogin) BottomLoginFragment().show(supportFragmentManager,"bnv_profile")
                    else binding.pager.setCurrentItem(4)
                }
            }
            true
        }

        //BottomFloating 버튼
        binding.fabChat.setOnClickListener {
            if(!G.isLogin) BottomLoginFragment().show(supportFragmentManager,"bnv_fabRefresh")
            else {
                binding.pager.visibility= ViewPager2.INVISIBLE
                binding.containerFragment.visibility= FrameLayout.VISIBLE
                supportFragmentManager.beginTransaction().replace(R.id.container_fragment,
                    BottomChatFragment()
                ).commit()
            }
        }

    }

}