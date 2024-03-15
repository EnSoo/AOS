package com.mrhiles.aos.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.mrhiles.aos.R
import com.mrhiles.aos.data.KakaoSearchStudyRoomRespnose
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

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private val binding by lazy { ActivityMapBinding.inflate(layoutInflater) }
    private lateinit var naverMap : NaverMap
    var searchStudyRoomResponse:KakaoSearchStudyRoomRespnose?=null

    //마커 리스트
    val markerList:MutableList<Marker> by lazy { mutableListOf() }

    //스터디 룸 리스트
    val studyRooms:MutableList<StudyRoom> by lazy { mutableListOf() }

    //검색 종류 : 지역, 범위, 검색키워드
    private var location=""
    private var distance=""
    private var searchQuery=""

    // 현재 지도에서 검색 클릭 여부
    private var isCurrentSearch=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 입력창이 제일 상단에 위치하도록..
        binding.relativeLayout.bringToFront()

        // 입력창의 dropDown 설정
        dropDownSetting()

        // 현 지도에서 검색
        binding.currentLocationSearch.apply {
            // 제일 상단에 위치하도록
            bringToFront()

            // 클릭 시 현재 지도 시점의 위치 구하기
            setOnClickListener { getStudyRooms(true) }
        }

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        // fragment의 getMapAsync() 메서드로 OnMapReadyCallback 콜백을 등록하면 비동기로 NaverMap 객체를 얻을 수 있다.
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        // naverMap.cameraPosition.target.also{it.y, it.x} 현재 카메라 위치 구하는 방법
        this.naverMap=naverMap
        val type=intent.getStringExtra("type")
        val s:String?=intent.getStringExtra("studyRoom")


        if(type=="Item") { // 1개의 아이템일 경우
            val studyRoom= Gson().fromJson(s, StudyRoom::class.java)
            studyRooms.add(studyRoom)
        } else if (type=="list") {  // 아이템 리스트일 경우
            val data=Gson().fromJson(s,Array<StudyRoom>::class.java)
            data.forEach {
              studyRooms.add(it)
            }
        }

        // 초기 맵 지도에서 마커 설정 및 카메라 이동
        setMarker()
        studyRooms.get(0).apply { setCameraMove(LatLng(y.toDouble(),x.toDouble())) }
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
            getStudyRooms()
        }
        // 범위 드롭다운에서 아이템 클릭 시
        binding.dropdownMenu2.setOnItemClickListener { parent, view, position, id ->
            distance=(view as TextView).text.toString()
            getStudyRooms()
        }
        binding.inputEditer.setOnEditorActionListener { v, actionId, event ->
            searchQuery=binding.inputEditer.text.toString()
            getStudyRooms()
            false
        }
    }
    //검색 결과에 따라 studyroom list 출력
    private fun getStudyRooms(isCurrentSearch:Boolean=false) {
        // 마커 및 studyRoom 초기화
        clearMarkerStudyRoomList()

        val retrofit= RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        val retrofitService=retrofit.create(RetrofitService::class.java)
        var call:Call<KakaoSearchStudyRoomRespnose>
        if(distance=="") distance="1000" //기본값 1000
        // 현재 지도에서 검색 클릭이 아닐 경우
        if(!isCurrentSearch) call=retrofitService.searchStudyRoomToString(radius=distance.toInt(), query = "${location} ${searchQuery} 스터디룸|스터디카페")
        else naverMap.cameraPosition.target.also {
            call=retrofitService.searchStudyRoomToString(latitude = it.latitude.toString(), longitute = it.longitude.toString(), radius=distance.toInt(), query = "${location} ${searchQuery} 스터디룸|스터디카페")
        }
        call.enqueue(object : Callback<KakaoSearchStudyRoomRespnose>{
            override fun onResponse(
                call: Call<KakaoSearchStudyRoomRespnose>,
                response: Response<KakaoSearchStudyRoomRespnose>
            ) {
                if (response.isSuccessful){ //응답결과가 성공적으로 나왔을 경우

                    searchStudyRoomResponse=response.body()
                    if(searchStudyRoomResponse!!.documents.size >0) { // 결과가 1개 이상일 경우
                        // Marker 및 StudyRoomList 초기화
                        searchStudyRoomResponse!!.documents.also { studyList->
                            studyList.forEach{ studyRooms.add(it) }
                            // 현재 지도에서 검색 클릭이 아닐 경우
                            if(!isCurrentSearch) setCameraMove(LatLng(studyRooms.get(0).y.toDouble(),studyRooms.get(0).x.toDouble()),naverMap.cameraPosition.zoom)
                            else naverMap.cameraPosition.also { // 현재 지도에서 검색 클릭 시
                                val latLng = LatLng(it.target.latitude, it.target.longitude)
                                setCameraMove(latLng, it.zoom)
                            }
                            setMarker()
                            var i=0
                            markerList.forEach{
                                Log.d("마커 ${++i}","${it.position.latitude} ${it.position.longitude}")
                            }
                        }
                    } else {
                        Toast.makeText(this@MapActivity, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<KakaoSearchStudyRoomRespnose>, t: Throwable) {
                Toast.makeText(this@MapActivity, "서버 오류가 있습니다.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    // studyroom에 따라 마커 표시
    private fun setMarker(){
        Toast.makeText(this, "${studyRooms.size}개가 검색 되었습니다.", Toast.LENGTH_SHORT).show()

        // 마커 및 정보창 처리
        studyRooms.forEach{studyRoom ->

            //위치 지정
            var latLng=LatLng(studyRoom.y.toDouble(),studyRoom.x.toDouble())
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
            // 정보창 클릭 시
            infoWindow.setOnClickListener {
                val intent = Intent(this,StudyRoomDetailActivity::class.java)

                //StudyRoom에 대한 데이터를 추가로 보내기
                val gson= Gson()
                val s=gson.toJson(studyRoom)
                intent.putExtra("studyRoom",s)

                this.startActivity(intent)
                true
            }

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

            //마커 리스트 보관
            markerList.add(maker)
        }
    }

    private fun setCameraMove(latLng:LatLng,zoom:Double=15.0) {
        // 카메라 이동
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng,zoom)
        //내 위치로 카메라 이동
        this.naverMap.moveCamera(cameraUpdate)
    }

    private fun clearMarkerStudyRoomList() {
        // 스터디룸 목록 삭제
        studyRooms.clear()

        // 지도상에 나와 있는 마커 삭제
        markerList.forEach{ it.map=null }
        markerList.clear()
    }
}