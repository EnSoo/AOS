package com.mrhiles.aos.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.AnyRes
import com.mrhiles.aos.G
import com.mrhiles.aos.R
import com.mrhiles.aos.data.KakaoSearchStudyRoomRespnose
import com.mrhiles.aos.data.Meta
import com.mrhiles.aos.data.StudyRoom
import com.mrhiles.aos.databinding.ActivityMainBinding
import com.mrhiles.aos.fragments.BottomChatFragment
import com.mrhiles.aos.fragments.BottomHomeFragment
import com.mrhiles.aos.fragments.BottomListFragment
import com.mrhiles.aos.fragments.BottomLoginFragment
import com.mrhiles.aos.fragments.BottomProfileFragment
import com.mrhiles.aos.fragments.BottomReservedFragment
import com.mrhiles.aos.network.RetrofitHelper
import com.mrhiles.aos.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var searchStudyRoomResponse:KakaoSearchStudyRoomRespnose?=null
    // study Room Search Index
    var pgIndex:Int=1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //val keyHash:String = Utility.getKeyHash(requireContext())
        //키해시 발급
        //Log.d("keyHash",keyHash)

        //BottomNavigation 각 4개 메뉴 클릭 시 Fragment 붙이기
        binding.bnv.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.menu_bnv_home -> supportFragmentManager.beginTransaction().replace(R.id.container_fragment, BottomHomeFragment()).commit()
                R.id.menu_bnv_list -> supportFragmentManager.beginTransaction().replace(R.id.container_fragment, BottomListFragment()).commit()
                R.id.menu_bnv_reserved -> {
                    if(!G.isLogin) BottomLoginFragment().show(supportFragmentManager,"bnv_favor")
                    else supportFragmentManager.beginTransaction().replace(R.id.container_fragment, BottomReservedFragment()).commit()
                }
                R.id.menu_bnv_profile -> {
                    if(!G.isLogin) BottomLoginFragment().show(supportFragmentManager,"bnv_profile")
                    else supportFragmentManager.beginTransaction().replace(R.id.container_fragment, BottomProfileFragment()).commit()
                }
            }
            true
        }

        //BottomFloating 버튼
        binding.fabChat.setOnClickListener {
            if(!G.isLogin) BottomLoginFragment().show(supportFragmentManager,"bnv_fabRefresh")
            else supportFragmentManager.beginTransaction().replace(R.id.container_fragment, BottomChatFragment()).commit()
        }

        // 초기 Study Room 불러오기
        SearchStudyRoom("스터디룸|스터디카페",1000,pgIndex,"accuracy")
    }

    //Retrofit Call 생성 메소드
    fun getRetrofitCall(query:String="",longitute:String="", latitude:String="", radius:Int, page:Int, sort:String) : Call<KakaoSearchStudyRoomRespnose> {
        val retrofit= RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        val retrofitService=retrofit.create(RetrofitService::class.java)
        return retrofitService.searchStudyRoomToString(longitute=longitute, latitude=latitude, radius=radius, page=page, sort=sort, query = query)
    }

    //Study Room Search 메소드
    var SearchStudyRoom=fun(query:String, radius:Int, page:Int, sort:String) {
        val call=getRetrofitCall(query = query, radius = radius, page = page, sort = sort)
        call.enqueue(object : Callback<KakaoSearchStudyRoomRespnose> {
            override fun onResponse(
                call: Call<KakaoSearchStudyRoomRespnose>,
                response: Response<KakaoSearchStudyRoomRespnose>
            ) {
                searchStudyRoomResponse=response.body()

                var meta:Meta?=searchStudyRoomResponse?.meta
                var documents:List<StudyRoom>? =searchStudyRoomResponse?.documents

                // 무조건 검색이 완료되면 '리스트' 형태로 먼저 보여주도록 할 것임
                binding.bnv.selectedItemId=R.id.menu_bnv_home
            }

            override fun onFailure(call: Call<KakaoSearchStudyRoomRespnose>, t: Throwable) {
                Toast.makeText(this@MainActivity, "서버 오류가 있습니다.", Toast.LENGTH_SHORT).show()
            }

        })
    }
}