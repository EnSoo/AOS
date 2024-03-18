package com.mrhiles.aos.network

import retrofit2.Call

abstract class BaseRequest<T>(val baseUrl : String, val accessToken: String, val refreshToken: String)
{
    abstract val serviceUrl : String
    abstract val params: MutableMap<String, String> // error, accessToken 및 서비스별 데이터 정보
    abstract val responseClass : Class<T>

    private fun setRetrofitService() : RetrofitService {
        val retrofit= RetrofitHelper.getRetrofitInstance(baseUrl)
        val retrofitService=retrofit.create(RetrofitService::class.java)
        return retrofitService
    }
    fun enqueue(retrofitService: RetrofitService) : Call<getResponseData> {
        return retrofitService.serviceRequest(serviceUrl,params)
    }
}