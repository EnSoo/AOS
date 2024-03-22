package com.mrhiles.aos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mrhiles.aos.activities.MainActivity
import com.mrhiles.aos.adapter.StudyRoomTapHomeFavorRecyclerAdapter
import com.mrhiles.aos.adapter.StudyRoomTapHomeListRecyclerAdapter
import com.mrhiles.aos.databinding.FragmentTapHomeListBinding

class TapHomeListFragment : Fragment() {
    private val binding by lazy { FragmentTapHomeListBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ma:MainActivity = activity as MainActivity
        ma.searchStudyRoomResponse ?: return // null일 경우 서버로딩이 완료되지 않았을 수도 있어서 리턴
        binding.homeRecycler.adapter = StudyRoomTapHomeListRecyclerAdapter(requireContext(), ma.searchStudyRoomResponse!!.documents)
        binding.homeRecycler.adapter!!.notifyDataSetChanged()

    }
}