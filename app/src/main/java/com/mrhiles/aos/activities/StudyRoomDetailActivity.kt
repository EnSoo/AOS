package com.mrhiles.aos.activities

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.mrhiles.aos.R
import com.mrhiles.aos.data.StudyRoom
import com.mrhiles.aos.databinding.ActivityStudyRoomDetailBinding

class StudyRoomDetailActivity : AppCompatActivity() {
    private val binding by lazy { ActivityStudyRoomDetailBinding.inflate(layoutInflater) }
    lateinit var studyRoom:StudyRoom
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // 인텐트로 부터 데이터 전달받기
        val s: String? = intent.getStringExtra("studyRoom")
        s?.also {
            studyRoom = Gson().fromJson(it, StudyRoom::class.java)

            //웹뷰를 사용할때 반드시 해야할 3가지 설정
            binding.wv.webViewClient= WebViewClient() // 현재 웹뷰안에서 웹문서 오픈
            binding.wv.webChromeClient= WebChromeClient() // 웹문서안에서 다이얼로그나 팝업 같은 것들이 발동.

            binding.wv.settings.javaScriptEnabled= true //웹뷰는 기본적으로 보안문제로 JS 동작 허용

            binding.wv.loadUrl(studyRoom.place_url)
        }
    }
}