package com.mrhiles.aos.activities

import android.hardware.Camera
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.mrhiles.aos.data.StudyRoom
import com.mrhiles.aos.databinding.ActivityMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private val binding by lazy { ActivityMapBinding.inflate(layoutInflater) }
    private lateinit var studyRoom:StudyRoom
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(binding.map.id) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(binding.map.id, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        val type=intent.getStringExtra("type")
        Toast.makeText(this, "test", Toast.LENGTH_SHORT).show()
        if(type=="Item") {
            val s:String?=intent.getStringExtra("studyRoom")
            s.also { studyRoom= Gson().fromJson(it, StudyRoom::class.java) }
            val latLng= LatLng(studyRoom.x.toDouble(),studyRoom.y.toDouble())

            //내 위치로 카메라 이동
            val cameraUpdate=CameraUpdate.scrollTo(latLng)
            naverMap.moveCamera(cameraUpdate)

            //라벨레이어에 라벨 추가

        }
    }
}