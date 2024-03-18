package com.mrhiles.aos.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
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

    // 1. search : 일반 검색,
    // 2. currentSearch : 현재 보이는 지도에서 검색
    // 3. myLocationSearch : 내 위치에서 검색
    private var searchType="search"

    // 현재 내 위치 정보 객체(위도, 경도)
    private var myLocation:Location?=null

    // [ Google Fused Location API 사용 : play-services-location ]
    val locationProviderClient : FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 입력창이 제일 상단에 위치하도록..
        binding.relativeLayout.bringToFront()

        // 현재 내 위치 찾기 이미지가 제일 상단에 위치하도록
        binding.myLocation.bringToFront()

        // 현재 내 위치로 카메라 이동 및 주변 검색
        binding.myLocation.setOnClickListener {
            // 위치정보 제공에 대한 퍼미션 체크
            val permissionState:Int=checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            if(permissionState==PackageManager.PERMISSION_DENIED) {
                //퍼미션 요청 다이얼로그 보이고 그 결과를 받아오는 작업을 대신해주는 대행사 이용
                permissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                //위치정보수집이 허가되어 있다면.. 곧바로 위치정보 얻어오는 작업 시작
                requestMyLocation()
            }
        }

        // 입력창의 dropDown 설정
        dropDownSetting()

        // 현 지도에서 검색
        binding.currentLocationSearch.apply {
            // 제일 상단에 위치하도록
            bringToFront()

            // 클릭 시 현재 지도 시점의 위치 구하기
            setOnClickListener { getStudyRooms("currentSearch") }
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
            binding.inputEditer.setText("")
            getStudyRooms()
            false
        }
    }
    //검색 결과에 따라 studyroom list 출력
    private fun getStudyRooms(type:String="search") {
        // 마커 및 studyRoom 초기화
        clearMarkerStudyRoomList()

        val retrofit= RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        val retrofitService=retrofit.create(RetrofitService::class.java)
        var call:Call<KakaoSearchStudyRoomRespnose>
        if(distance=="") distance="1000" //기본값 1000
        // 현재 지도에서 검색 클릭이 아닐 경우
        if(type=="search") call=retrofitService.searchStudyRoomToString(radius=distance.toInt(), query = "${location} ${searchQuery} 스터디룸|스터디카페")
        else if(type=="currentSearch"){ // 현재 보이는 지도에서 검색일 경우
            naverMap.cameraPosition.target.also {
                call=retrofitService.searchStudyRoomToString(latitude = it.latitude.toString(), longitute = it.longitude.toString(), radius=distance.toInt(), query = "${location} ${searchQuery} 스터디룸|스터디카페")
            }
        } else {    // 내 위치에서 검색 할 경우 myLocationSearch
            call=retrofitService.searchStudyRoomToString(latitude = myLocation?.latitude.toString(), longitute = myLocation?.longitude.toString(), radius=distance.toInt(), query = "${location} ${searchQuery} 스터디룸|스터디카페")
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
                            if(type=="search") setCameraMove(LatLng(studyRooms.get(0).y.toDouble(),studyRooms.get(0).x.toDouble()),naverMap.cameraPosition.zoom)
                            else if(type=="currentSearch") { // 현재 보이는 지도에서 검색일 경우
                                naverMap.cameraPosition.also { // 현재 지도에서 검색 클릭 시
                                    val latLng = LatLng(it.target.latitude, it.target.longitude)
                                    setCameraMove(latLng, it.zoom)
                                }
                            } else {
                                // 내 위치에서 검색 할 경우 myLocationSearch
                                setCameraMove(LatLng(myLocation!!.latitude,myLocation!!.longitude))
                            }
                            setMarker()
                        }
                    } else {
                        Toast.makeText(this@MapActivity, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()

                        //내 위치에서 검색 시 카메라 이동
                        if(type=="myLocationSearch") setCameraMove(LatLng(myLocation!!.latitude,myLocation!!.longitude))
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

    // 지정된 위치로 카메라 이동
    private fun setCameraMove(latLng:LatLng,zoom:Double=15.0) {
        // 카메라 이동
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng,zoom)
        //내 위치로 카메라 이동
        this.naverMap.moveCamera(cameraUpdate)
    }

    // StudyRoom 및 Marker clear
    private fun clearMarkerStudyRoomList() {
        // 스터디룸 목록 삭제
        studyRooms.clear()

        // 지도상에 나와 있는 마커 삭제
        markerList.forEach{ it.map=null }
        markerList.clear()
    }

    // 퍼미션 요청 및 결과를 받아오는 작업을 대신하는 대행사 등록
    val permissionResultLauncher:ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if(it) requestMyLocation()
        else Toast.makeText(this, "내 위치정보를 제공하지 않아서 검색기능 사용이 제한됩니다.", Toast.LENGTH_SHORT).show()
    }

    //현재 위치를 얻어오는 작업요청 코드가 있는 메소드
    private fun requestMyLocation() {
        //요청 객체 생성
        val request: LocationRequest=LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,3000).build()
        //실시간 위치정보 갱신 요청
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        { return }
        locationProviderClient.requestLocationUpdates(request,locationCallback, Looper.getMainLooper())

    }

    //위치정보 갱신 떄마다 발동하는 콜백 객체
    private val locationCallback=object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)

            myLocation=p0.lastLocation

            //위치 탐색이 종료되었으니 내 위치 정보 업데이트를 이제 그만
            locationProviderClient.removeLocationUpdates(this) // this는 location callback 객체

            // 내 위치를 기준으로 장소 검색
            getStudyRooms("myLocationSearch")
        }
    }
}