package com.mrhiles.aos.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.mrhiles.aos.activities.MainActivity
import com.mrhiles.aos.activities.MapActivity
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
        binding.locationSearch.setOnClickListener {
            //맵 전체 보여주기
            val intent= Intent(context, MapActivity::class.java)

            //StudyRoom List에 대한 데이터를 보내기
            val gson= Gson()
            val s=gson.toJson(ma.searchStudyRoomResponse!!.documents)
            intent.putExtra("studyRoom",s)
            intent.putExtra("type","list") // Type이 Item일 경우 1개만 검색
            requireContext().startActivity(intent)
        }
    }
}