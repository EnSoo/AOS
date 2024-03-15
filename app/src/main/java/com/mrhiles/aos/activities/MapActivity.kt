package com.mrhiles.aos.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.mrhiles.aos.R
import com.mrhiles.aos.data.KakaoSearchStudyRoomRespnose
import com.mrhiles.aos.data.Meta
import com.mrhiles.aos.data.StudyRoom
import com.mrhiles.aos.databinding.ActivityMapBinding
import com.mrhiles.aos.network.RetrofitHelper
import com.mrhiles.aos.network.RetrofitService
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private val binding by lazy { ActivityMapBinding.inflate(layoutInflater) }
    private lateinit var naverMap : NaverMap
    private var location=""
    private var distance=""
    private var searchQuery=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 입력창이 제일 상단에 위치하도록..
        binding.relativeLayout.bringToFront()

        dropDownSetting()

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        // fragment의 getMapAsync() 메서드로 OnMapReadyCallback 콜백을 등록하면 비동기로 NaverMap 객체를 얻을 수 있다.
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("ResourceAsColor")
    override fun onMapReady(naverMap: NaverMap) {
        // naverMap.cameraPosition.target.also{it.y, it.x} 현재 카메라 위치 구하는 방법
        this.naverMap=naverMap
        val type=intent.getStringExtra("type")
        val s:String?=intent.getStringExtra("studyRoom")
        val studyRooms:MutableList<StudyRoom> = mutableListOf()


        if(type=="Item") { // 1개의 아이템일 경우
            val studyRoom= Gson().fromJson(s, StudyRoom::class.java)
            studyRooms.add(studyRoom)
        } else if (type=="list") {  // 아이템 리스트일 경우
            val data=Gson().fromJson(s,Array<StudyRoom>::class.java)
            data.forEach {
              studyRooms.add(it)
            }
        }
        setMarkerCameraMove(LatLng(studyRooms.get(0).y.toDouble(),studyRooms.get(0).x.toDouble()),studyRooms)
    }

    private fun dropDownSetting() {
        // 드롭다운 지역 아이템 설정
        val regionArray= resources.getStringArray(R.array.lecture_location_list)
        val arrayAdapter=ArrayAdapter<String>(this,R.layout.lecture_dropdown_item, regionArray)
        binding.dropdownMenu.setAdapter(arrayAdapter)

        // 드롭다운 범위 아이템 설정
        val distanceList= mutableListOf<String>()
        distanceList.add("")
        for(i in 10 until 300 step 10) {
            distanceList.add(i.toString())
        }
        for(i in 300 until 2000 step 100) {
            distanceList.add(i.toString())
        }
        for(i in 2000..20000 step 1000) {
            distanceList.add(i.toString())
        }
        val distanceAdapter=ArrayAdapter<String>(this,R.layout.lecture_dropdown_item,distanceList)
        binding.dropdownMenu2.setAdapter(distanceAdapter)
        // 지역 드롭다운에서 아이템 클릭 시
        binding.dropdownMenu.setOnItemClickListener { parent, view, position, id ->
            location=(view as TextView).text.toString()
            thread {
                val lock = Any()
                synchronized(lock) {
                    val studyRooms=getStudyRooms()
                    if(studyRooms.size != 0) { studyRooms.get(0).also { setMarkerCameraMove(LatLng(it.y.toDouble(), it.x.toDouble()), studyRooms) } }
                }
            }

        }
        // 범위 드롭다운에서 아이템 클릭 시
        binding.dropdownMenu2.setOnItemClickListener { parent, view, position, id ->
            distance=(view as TextView).text.toString()
            thread {
                val lock = Any()
                synchronized(lock) {
                    val studyRooms=getStudyRooms()
                    if(studyRooms.size != 0) { studyRooms.get(0).also { setMarkerCameraMove(LatLng(it.y.toDouble(), it.x.toDouble()), studyRooms) } }
                }
            }
        }
        binding.inputEditer.setOnEditorActionListener { v, actionId, event ->
            searchQuery=binding.inputEditer.text.toString()
            thread {
                val lock = Any()
                synchronized(lock) {
                    val studyRooms=getStudyRooms()
                    if(studyRooms.size != 0) { studyRooms.get(0).also { setMarkerCameraMove(LatLng(it.y.toDouble(), it.x.toDouble()), studyRooms) } }
                }
            }

            false
        }
    }
    //검색 결과에 따라 studyroom list 출력
    private fun getStudyRooms() :List<StudyRoom> {
        val studyRooms:MutableList<StudyRoom> = mutableListOf()
        val retrofit= RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        val retrofitService=retrofit.create(RetrofitService::class.java)
        var call:Call<KakaoSearchStudyRoomRespnose>
        var documents:List<StudyRoom> = mutableListOf()
        if(distance=="") distance="1000" //기본값 1000
        Log.d("tset","${location} ${searchQuery} 스터디룸|스터디카페")
        call=retrofitService.searchStudyRoomToString(radius=distance.toInt(), query = "${location} ${searchQuery} 스터디룸|스터디카페")
        call.enqueue(object : Callback<KakaoSearchStudyRoomRespnose>{
            override fun onResponse(
                call: Call<KakaoSearchStudyRoomRespnose>,
                response: Response<KakaoSearchStudyRoomRespnose>
            ) {
                val searchStudyRoomResponse=response.body()

                searchStudyRoomResponse ?: return
                documents= searchStudyRoomResponse!!.documents
            }

            override fun onFailure(call: Call<KakaoSearchStudyRoomRespnose>, t: Throwable) {
                Toast.makeText(this@MapActivity, "서버 오류가 있습니다.", Toast.LENGTH_SHORT).show()
            }

        })


        return documents
    }

    // studyroom에 따라 마커 표시 및 카메라 이동
    @SuppressLint("ResourceAsColor")
    private fun setMarkerCameraMove(latLng:LatLng, studyRooms: List<StudyRoom>){
        // 카메라 이동
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng,15.0)

        //내 위치로 카메라 이동
        this.naverMap.moveCamera(cameraUpdate)

        // 마커 및 정보창 처리
        studyRooms.forEach{studyRoom ->

            // 마커 작업
            val maker=Marker()                    // 마커 객체 생성
            maker.apply {
                position=latLng                   // 마커 위치지정
                map=naverMap                      // 마커 지도 추가 삭제하려면 null
                captionText=studyRoom.place_name  // 마커 캡션 텍스트
                captionRequestedWidth=200         // 마커 캡션 텍스트 너비
                setCaptionAligns(Align.Bottom)    // 마커 캡션 위치
            }

            // 정보창
            val infoWindow = InfoWindow()          // 윈도우창 객체 생성
            infoWindow.adapter=object : InfoWindow.DefaultTextAdapter(this) { // 정보창 내용
                override fun getText(p0: InfoWindow): CharSequence {
                    return "${studyRoom.place_name}\n\n${studyRoom.address_name}\n\n${studyRoom.phone}"
                }
            }

            //지도를 클릭 하면 열려 있는 정보 창이 닫힘
            naverMap.setOnMapClickListener { pointF, latLng -> infoWindow.close() }

            // 마커 클릭 시
            val listener = Overlay.OnClickListener { overlay ->
                val marker=overlay as Marker
                if (marker.infoWindow == null) {
                    // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                    infoWindow.open(marker)
                } else {
                    // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                    infoWindow.close()
                }
                true
            }

            maker.onClickListener=listener
        }

    }
}