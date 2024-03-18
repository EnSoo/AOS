package com.mrhiles.aos.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mrhiles.aos.data.LoginRequire
import com.mrhiles.aos.data.LoginResponse
import com.mrhiles.aos.databinding.ActivityLoginBinding
import com.mrhiles.aos.network.Login
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import kotlin.math.log

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val redirectUri by lazy { "https://ec2-34-238-84-139.compute-1.amazonaws.com" }
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
        NaverIdLoginSDK.initialize(this, "YQOhvcuPJbnpf4yLQWNh", "ZdeJyRYW2s", "AOS")

        // 로그인 요청
        NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback {
            override fun onError(errorCode: Int, message: String) {
                Log.d("NaverLoginError","$message")
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Log.d("NaverLoginFailure","$message")
            }

            override fun onSuccess() {
                Log.d("NaverLogin","네이버 액세스 토큰 발급 성공")

                val accessToken:String? = NaverIdLoginSDK.getAccessToken()
                accessToken ?: return
                //val loginResult=Login(this@LoginActivity,"naver",accessToken,"","")
                val loginResult=Login(this@LoginActivity,"naver", accessToken, "","")
                val call= loginResult.getCall()
                call.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val s= response.body()

                            s ?: return
                            loginResult.responseData=s
                            loginResult.setResult()
                            val error=loginResult.responseData.error
                            if(error != "400" ) {
                                if(error=="5200") Toast.makeText(this@LoginActivity, "네이버 간편 로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                                else Toast.makeText(this@LoginActivity, "${error}: 네이버 간편 로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@LoginActivity, "서비스 서버에서 토큰 발급이 성공적으로 이루어지지 않았습니다.", Toast.LENGTH_SHORT).show()
                            }
                            finish()
                        }
                    }
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
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