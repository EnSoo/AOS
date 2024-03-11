package com.mrhiles.aos.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kakao.sdk.common.util.Utility
import com.mrhiles.aos.activities.MainActivity
import com.mrhiles.aos.adapter.StudyRoomRecyclerAdapter
import com.mrhiles.aos.data.KakaoSearchStudyRoomRespnose
import com.mrhiles.aos.data.StudyRoom
import com.mrhiles.aos.databinding.FragmentBottomHomeBinding
import com.mrhiles.aos.network.RetrofitHelper
import com.mrhiles.aos.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query

class BottomHomeFragment : Fragment(){
    // kakao search API 응답결과 객체 참조변수
    //val documents:MutableList<StudyRoom> by lazy { mutableListOf() }
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
        val ma:MainActivity = activity as MainActivity
        ma.searchStudyRoomResponse ?: return // null일 경우 서버로딩이 완료되지 않았을 수도 있어서 리턴
        binding.homeRecycler.adapter = StudyRoomRecyclerAdapter(requireContext(), ma.searchStudyRoomResponse!!.documents)

    }
}