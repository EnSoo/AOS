package com.mrhiles.aos.activities

import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.mrhiles.aos.R
import com.mrhiles.aos.data.StudyRoom
import com.mrhiles.aos.databinding.ActivityMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private val binding by lazy { ActivityMapBinding.inflate(layoutInflater) }
    private lateinit var studyRoom:StudyRoom
    private lateinit var naverMap : NaverMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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
        this.naverMap=naverMap
        val type=intent.getStringExtra("type")

        if(type=="Item") {
            val s:String?=intent.getStringExtra("studyRoom")
            s.also { studyRoom= Gson().fromJson(it, StudyRoom::class.java) }
            val latLng= LatLng(studyRoom.y.toDouble(),studyRoom.x.toDouble())
            //내 위치로 카메라 이동
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng,15.0)
            this.naverMap.moveCamera(cameraUpdate)

            // 마커 작업
            val maker=Marker()                    // 마커 객체 생성
            maker.apply {
                position=latLng                   // 마커 위치지정
                map=naverMap                      // 마커 지도 추가 삭제하려면 null
                iconTintColor=R.color.mydefault   // 마커 틴트컬러
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
        } else if (type=="list") {

        }
    }
}