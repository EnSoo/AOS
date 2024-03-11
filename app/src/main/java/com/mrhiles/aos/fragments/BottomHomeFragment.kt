package com.mrhiles.aos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    var searchStudyRoomResponse:KakaoSearchStudyRoomRespnose?=null
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
        val call=getRetrofitCall()
        call.enqueue(object : Callback<KakaoSearchStudyRoomRespnose>{
            override fun onResponse(
                call: Call<KakaoSearchStudyRoomRespnose>,
                response: Response<KakaoSearchStudyRoomRespnose>
            ) {
                searchStudyRoomResponse=response.body()

            }

            override fun onFailure(call: Call<KakaoSearchStudyRoomRespnose>, t: Throwable) {
                Toast.makeText(requireContext(), "서버 오류가 있습니다.", Toast.LENGTH_SHORT).show()
            }

        })
        // document가 null이 아닐 경우 처리 필요
        binding.homeRecycler.adapter = StudyRoomRecyclerAdapter(requireContext(), searchStudyRoomResponse!!.documents)

    }

    //Retrofit Call 생성 메소드
    fun getRetrofitCall(longitute:String="", latitude:String="", radius:Int=1000, page:Int=1, sort:String="accuracy") : Call<KakaoSearchStudyRoomRespnose> {
        val retrofit= RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        val retrofitService=retrofit.create(RetrofitService::class.java)
        return retrofitService.searchStudyRoomToString(longitute="", latitude="", radius=1000, page=1, sort="accuracy")
    }
    //스터디룸 서치 메소드

}