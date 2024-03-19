package com.mrhiles.aos.network

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.mrhiles.aos.G
import com.mrhiles.aos.R
import com.mrhiles.aos.activities.LoginActivity
import com.mrhiles.aos.activities.MainActivity
import com.mrhiles.aos.data.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder

class Service(
    val context : Context,
    val serviceUrl : String,
    var params: Any
) {
    lateinit var error:String
    lateinit var code:String
    lateinit var responseData:String
    private lateinit var accessToken: String
    private lateinit var refreshToken: String
    private fun getToken() {
        // 글로벌 변수에서 액세스 토큰 및 리프레시 토큰 가져오기
        accessToken=G.accessToken
        refreshToken=G.refreshToken
    }
    private fun setRetrofitService() : RetrofitService {
        val retrofit= RetrofitHelper.getunsafeRetrofitInstance(G.baseUrl)
        val retrofitService=retrofit.create(RetrofitService::class.java)
        return retrofitService
    }

    fun serviceRequest() {
        if(G.isLogin) { // 로그인 상태 경우만 실행
            getToken()
            val retrofitService = setRetrofitService()

            // 파라미터를 Json 형태로 변환
            val data: String = Gson().toJson(params)
            var requestData: requestData = requestData(data, URLEncoder.encode(accessToken, "UTF-8"))

            val call = retrofitService.serviceRequest(serviceUrl, requestData)
            call.enqueue(object : Callback<responseData> {
                override fun onResponse(
                    call: Call<responseData>,
                    response: Response<responseData>
                ) {
                    if (response.isSuccessful) {
                        val s = response.body()
                        s ?: return
                        error = s.error
                        code = s.code
                        responseData = s.params
                        if (error == "5301") {  // 액세스 토큰이 만료 되었을 경우 리프레쉬로 요청
                            getAccessToken()
                        } else if (error == "5302" || error == "5303" || error == "5204") { // 5302, 리프레쉬 토큰 만료, 5303 유효하지 않은 토큰일 경우, 5204 액세스 토큰 재발급 실패
                            logout()
                        } else if (error == "5300") {  // 유효한 토큰일 경우
                            serviceProcess()
                        } else if (error == "5200") { // 액세스 토큰 재발급 요청이 정상적으로 이루어 졌을 경우 서비스 재요청
                            serviceRequest()
                        }
                    }
                }

                override fun onFailure(call: Call<responseData>, t: Throwable) {
                    Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            //로그인 상태가 아닐 경우 activity 띄워야 함
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra("login_type",G.userInfo.loginType)
            context.startActivity(intent)
        }
    }

// 서비스별로 멘트를 다르게 처리해야 함
    private fun serviceProcess() {
        if(code == "200") {
            Toast.makeText(context, "성공적으로 처리 되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "오류가 있어 처리 되지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

// 로그아웃. 네이버, 카카오 등 브라우저 로그인을 취소해야 함
    fun logout() {
        getToken()
        val retrofitService=setRetrofitService()
        val call=retrofitService.logout(URLEncoder.encode(refreshToken, "UTF-8"))
        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {

                if (response.isSuccessful) {
                    val error=response.body()
                    error ?: return
                    val ma: MainActivity = context as MainActivity

                    if(error =="7201") {
                        Toast.makeText(context, "로그아웃 실패", Toast.LENGTH_SHORT).show()
                    }
                    if(error =="7200" || error =="6200" || error =="6201") { // 로그인된 회원에서 찾을 수 없음 6201
                        Toast.makeText(context, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                        // sharedPreferences 초기화
                        initsharedPreferences()

                        // 글로벌변수 초기화
                        initGlobal()

                        ma.findViewById<BottomNavigationView>(R.id.bnv).selectedItemId= R.id.menu_bnv_home
                    }

// 간편 로그인 취소 처리 추가 부분
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun initsharedPreferences() {
        val sharedPreferences = context.getSharedPreferences("logininfo", Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.putBoolean("is_login",false)
        editor.putString("access_token","")
        editor.putString("refresh_token","")
        editor.putString("login_type","")
        editor.putString("providerId","")
        editor.putString("email","")
        editor.apply()
    }

    private fun initGlobal() {
        // 글로벌 변수 설정
        G.isLogin=false
        G.accessToken=""
        G.refreshToken=""
        G.userInfo= UserInfo("","","")
    }

    fun getAccessToken() {
        val retrofitService=setRetrofitService()
        val call=retrofitService.tokenGenrate(refreshToken,"refresh_token")
        call.enqueue(object : Callback<UserCheck>{
            override fun onResponse(call: Call<UserCheck>, response: Response<UserCheck>) {
                if (response.isSuccessful) {
                    val s= response.body()
                    s ?: return
                    error=s.error
                    accessToken=s.access_token
                }
            }

            override fun onFailure(call: Call<UserCheck>, t: Throwable) {
                Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}