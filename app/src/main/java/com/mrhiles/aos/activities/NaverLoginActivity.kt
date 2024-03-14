package com.mrhiles.aos.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.mrhiles.aos.R
import com.mrhiles.aos.databinding.ActivityNaverLoginBinding

class NaverLoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityNaverLoginBinding.inflate(layoutInflater) }
    private lateinit var webView:WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        webView=binding.webviewNaverLogin
        //네이버 로그인 받아오기
        val naverLoginUrl=intent.getStringExtra("naverLoginUrl") ?: ""
        //웹뷰 설정
        webView.webViewClient= object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val uri:Uri = Uri.parse(url);
                val code = uri.getQueryParameter("code");
                val state = uri.getQueryParameter("state");

                //로그인 결과 처리
                val intent=Intent();
                intent.putExtra("code",code)
                intent.putExtra("state",state)
                intent.putExtra("state",state)
                setResult(RESULT_OK,intent)
                Log.d("aaa","${url}")
                //finish()
            }
        }
        webView.loadUrl(naverLoginUrl)
    }
}