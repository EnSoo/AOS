package com.mrhiles.aos.network

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.mrhiles.aos.G
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServiceRequest(
    val context : Context,
    val baseUrl : String="https://ec2-34-238-84-139.compute-1.amazonaws.com",
    val serviceUrl : String,
    var params: Any
) {
    private lateinit var error:String
    private lateinit var accessToken: String
    private lateinit var refreshToken: String
    private fun getToken() {
        // 글로벌 변수에서 액세스 토큰 및 리프레시 토큰 가져오기
        accessToken=G.accessToken
        refreshToken=G.refreshToken
    }
    private fun setRetrofitService() : RetrofitService {
        val retrofit= RetrofitHelper.getRetrofitInstance(baseUrl)
        val retrofitService=retrofit.create(RetrofitService::class.java)
        return retrofitService
    }

    fun serviceRequest() {
        getToken()
        val retrofitService=setRetrofitService()

        // 파라미터를 Json 형태로 변환
        var data:String=Gson().toJson(params)
        var requestData:requestData= requestData(data, accessToken)

        val call=retrofitService.serviceRequest(serviceUrl,requestData)
        call.enqueue(object : Callback<getResponseData>{
            override fun onResponse(
                call: Call<getResponseData>,
                response: Response<getResponseData>
            ) {
                if (response.isSuccessful) {
                    val s= response.body()
                    s ?: return
                    error=s.error
                    val code=s.code
                    if (error == "5301") {  // 액세스 토큰이 만료 되었을 경우 리프레쉬로 요청
                        getAccessToken()
                    } else if(error == "5302" || error == "5303" || error == "5204"){ // 5302, 리프레쉬 토큰 만료, 5303 유효하지 않은 토큰일 경우, 5204 액세스 토큰 재발급 실패
                        logout()
                    } else if(error=="5300") {  // 유효한 토큰일 경우
                        serviceProcess(code)
                    } else if(error =="5200") { // 액세스 토큰 재발급 요청이 정상적으로 이루어 졌을 경우 서비스 재요청
                        serviceRequest()
                    }
                }
            }

            override fun onFailure(call: Call<getResponseData>, t: Throwable) {
                Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

// 서비스별로 멘트를 다르게 처리해야 함
    private fun serviceProcess(code:String) {
        if(code == "200") {
            Toast.makeText(context, "성공적으로 처리 되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "오류가 있어 처리 되지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

// 로그아웃. 네이버, 카카오 등 브라우저 로그인을 취소해야 함
    fun logout() {
        val retrofitService=setRetrofitService()
        val call=retrofitService.logout(refreshToken)
        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val error=response.body()
                    error ?: return
                    if(error =="7201") {
                        Toast.makeText(context, "로그아웃 실패", Toast.LENGTH_SHORT).show()
                    }
                    if(error =="7200") {
                        Toast.makeText(context, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                    }
                    if(error =="6201") {
                        Toast.makeText(context, "로그인 된 회원에서 찾을 수 없음", Toast.LENGTH_SHORT).show()
                    }

// 간편 로그인 취소 처리 추가 부분
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
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