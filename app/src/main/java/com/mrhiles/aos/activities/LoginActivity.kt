package com.mrhiles.aos.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.mrhiles.aos.G
import com.mrhiles.aos.R
import com.mrhiles.aos.data.NaverLogin
import com.mrhiles.aos.databinding.ActivityLoginBinding
import com.mrhiles.aos.network.RetrofitHelper
import com.mrhiles.aos.network.RetrofitService
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val loginType: String? = intent.getStringExtra("login_type")

        when(loginType) {
            "email" -> clickEmail()
            "naver" -> clickNaver()
            "kakao" -> clickKakao()
        }
    }

    //이메일 로그인
    private fun clickEmail() {

    }
    //네이버 간편 로그인
    private fun clickNaver() {
        // 네아로 SDK 초기화
        NaverIdLoginSDK.initialize(this, "YQOhvcuPJbnpf4yLQWNh", "ZdeJyRYW2s", "SearchMap")

        // 로그인 요청
        NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback {
            override fun onError(errorCode: Int, message: String) {
                Toast.makeText(this@LoginActivity, "$message", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Toast.makeText(this@LoginActivity, "$message", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess() {
                Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()

                val accessToken:String? = NaverIdLoginSDK.getAccessToken()
                G.accessToken=accessToken!!
                /* AcessToken 토급 PHP에 보내는 작업
                    1. 네이버 개발자 사이트에서 Callback URL에 세션을 처리할 php 경로 작성
                    2. PHP 서버에서 액세스 토큰을 받아 세션으로 처리
                */
                //Retrofit 작업을 통해 사용자 정보 가져오기
                val retrofit= RetrofitHelper.getRetrofitInstance("https://openapi.naver.com")
                val retrofitApiService=retrofit.create(RetrofitService::class.java)
                val call=retrofitApiService.getNidUserInfo("Bearer ${accessToken}")
                call.enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        val s= response.body()
                        AlertDialog.Builder(this@LoginActivity).setMessage("네이버 로그인에 성공 하였습니다.").create().show()
                        val naverLoginInfo=Gson().fromJson(s,NaverLogin::class.java)
                        G.userInfo?.id=naverLoginInfo.response.id
                        G.userInfo?.email=naverLoginInfo.response.email
                        Toast.makeText(this@LoginActivity, "${s}", Toast.LENGTH_SHORT).show()
                        G.isLogin=true
                        finish()
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                    }

                })
            }

        })
    }

    //카카오 간편 로그인
    private fun clickKakao() {

    }
}