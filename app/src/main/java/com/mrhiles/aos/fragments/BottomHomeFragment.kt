package com.mrhiles.aos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mrhiles.aos.adapter.StudyRoomRecyclerAdapter
import com.mrhiles.aos.data.StudyRoom
import com.mrhiles.aos.databinding.FragmentBottomHomeBinding

class BottomHomeFragment : Fragment(){
    // kakao search API 응답결과 객체 참조변수
    val documents:MutableList<StudyRoom> by lazy { mutableListOf() }
    private val binding by lazy { FragmentBottomHomeBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.inputLayout.bringToFront()
        binding.homeRecycler.adapter=StudyRoomRecyclerAdapter(requireContext(),documents)
    }

    //스터디룸 서치 메소드

}