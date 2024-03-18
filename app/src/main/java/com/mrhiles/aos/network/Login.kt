package com.mrhiles.aos.network

import android.content.Context
import com.mrhiles.aos.G
import com.mrhiles.aos.data.LoginResponse
import com.mrhiles.aos.data.UserInfo
import retrofit2.Call
import java.net.URLEncoder

class Login(val context:Context, val login_type:String, val access_token: String="", val email: String="", val password: String="") {
    private val host:String="https://ec2-34-238-84-139.compute-1.amazonaws.com"
    lateinit var responseData:LoginResponse

    fun setResult() { //global 변수 및 sharedPreferences 저장
        setGlobal()
        setsharedPreferences()
    }
    private fun setGlobal() {
        // 글로벌 변수 설정
            G.isLogin=true
            responseData.also {
                G.accessToken=it.access_token
                G.refreshToken=it.refresh_token
                G.userInfo= UserInfo(it.id,it.email,login_type)
             }
    }
    private fun setsharedPreferences() {
        val sharedPreferences = context.getSharedPreferences("logininfo", Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.putBoolean("is_login",G.isLogin)
        editor.putString("access_token",G.accessToken)
        editor.putString("refresh_token",G.refreshToken)
        editor.putString("login_type",G.userInfo.loginType)
        editor.putString("providerId",G.userInfo.id)
        editor.putString("email",G.userInfo.email)
        editor.apply()
    }
    fun getCall() : Call<LoginResponse> {
        val retrofit=RetrofitHelper.getunsafeRetrofitInstance(host)
        val retrofitService=retrofit.create(RetrofitService::class.java)
        val call=retrofitService.getLogin(login_type,
            "${URLEncoder.encode(access_token, "UTF-8")}",
            "${URLEncoder.encode(email, "UTF-8")}",
            "${URLEncoder.encode(password, "UTF-8")}")
        return call
    }
}