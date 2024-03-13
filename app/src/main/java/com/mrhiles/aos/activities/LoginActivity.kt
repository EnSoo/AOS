package com.mrhiles.aos.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.mrhiles.aos.G
import com.mrhiles.aos.R
import com.mrhiles.aos.data.NaverLogin
import com.mrhiles.aos.data.UserInfo
import com.mrhiles.aos.databinding.ActivityLoginBinding
import com.mrhiles.aos.network.RetrofitHelper
import com.mrhiles.aos.network.RetrofitService
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.SecureRandom

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val redirectUri by lazy { "https://ec2-34-238-84-139.compute-1.amazonaws.com" }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val loginType: String? = intent.getStringExtra("login_type")

        when(loginType) {
            "email" -> emailLogin()
            "naver" -> naverLogin2()
            "kakao" -> kakaoLogin()
        }
    }

    //이메일 로그인
    private fun emailLogin() {

    }
    //네이버 간편 로그인
    private fun naverLogin() {
        // 네아로 SDK 초기화
        NaverIdLoginSDK.initialize(this, "YQOhvcuPJbnpf4yLQWNh", "ZdeJyRYW2s", "AOS")

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
                val sharedPreferences = getSharedPreferences("logininfo", Context.MODE_PRIVATE)
                val editor=sharedPreferences.edit()
                /* AcessToken 토급 PHP에 보내는 작업
                    1. 네이버 개발자 사이트에서 Callback URL에 세션을 처리할 php 경로 작성
                    2. PHP 서버에서 액세스 토큰을 받아 세션으로 처리
                */
                //Retrofit 작업을 통해 사용자 정보 가져오기
                val retrofit= RetrofitHelper.getunsafeRetrofitInstance(redirectUri)
                val retrofitApiService=retrofit.create(RetrofitService::class.java)
                val call=retrofitApiService.getNidUserInfo("test")
                call.enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        val s= response.body()
                        Log.d("test","${s}")
//                        val naverLoginInfo=Gson().fromJson(s,NaverLogin::class.java)
//                        naverLoginInfo.response.apply { G.userInfo= UserInfo(id,email,"naver") }
//                        G.isLogin=true
//                        editor.putString("access_token",G.accessToken)
//                        editor.putString("login_type","naver")
//                        editor.apply()
                        finish()
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                    }

                })
            }

        })
    }
    private fun naverLogin2() {
        val clientId= "YQOhvcuPJbnpf4yLQWNh"
        val redirectUri= "https://ec2-34-238-84-139.compute-1.amazonaws.com/login/login_check.php"
        val state = generateState()
        val url="https://nid.naver.com/oauth2.0/authorize?&response_type=code&client_id=${clientId}&redirect_uri=${redirectUri}&state=${state}"
        Log.d("link",url)
        val intent=Intent(this@LoginActivity,NaverLoginActivity::class.java)
        intent.putExtra("login_type","naver")
        intent.putExtra("naverLoginUrl", url)
        resultLauncher.launch(intent)
        finish()

    }
    // csrf 방지를 위한 토큰
    fun generateState(): String {
        val bytes = ByteArray(16)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.DEFAULT).toString()
    }

    //카카오 간편 로그인
    private fun kakaoLogin() {

    }


    // 간편로그인 웹뷰 띄운 후 결과
    private val resultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult(),{
        if(it.resultCode==RESULT_OK) {
            val data=it.data
            val code = data?.getStringExtra("code")
            val state = data?.getStringExtra("state")
        }
    })
}