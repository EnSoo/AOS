package com.mrhiles.aos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.mrhiles.aos.activities.MainActivity
import com.mrhiles.aos.adapter.TapHomeViewPagerAdapter
import com.mrhiles.aos.databinding.FragmentBottomHomeBinding

class BottomHomeFragment : Fragment(){
    // kakao search API 응답결과 객체 참조변수
    //val documents:MutableList<StudyRoom> by lazy { mutableListOf() }
    private val binding by lazy { FragmentBottomHomeBinding.inflate(layoutInflater) }
    private val tabtitle by lazy { listOf("목록","찜목록") }
    private var searchQuery:String=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pager.adapter=TapHomeViewPagerAdapter(requireActivity())
        TabLayoutMediator(binding.tabLayout,binding.pager, TabLayoutMediator.TabConfigurationStrategy{tab, position ->
            tab.setText(tabtitle[position])
        }).attach()

        val ma:MainActivity = activity as MainActivity
        binding.inputEditer.setOnEditorActionListener { v, actionId, event ->
            searchQuery=binding.inputEditer.text.toString()
            ma.SearchStudyRoom("${searchQuery} 스터디룸|스터디카페",1000,1,"accuracy")
            false
        }
    }
}