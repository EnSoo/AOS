package com.mrhiles.aos.network

import com.mrhiles.aos.data.KakaoSearchStudyRoomRespnose
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitService {
    //카카오 키워드로 장소 검색하기
    // 스터디 장소를 확인하기 위한 api 요청. KakaoAK REST API Key
    @Headers("Authorization KakaoAK e85398842cfcf70cda90f41067f11aa6")
    @GET("/v2/local/search/keyword.json")
    fun searchStudyRoomToString(@Query("query") query:String="스터디룸|스터디카페", @Query("x") longitute:String="", @Query("y") latitude:String="", @Query("radius") radius:Int=1000, @Query("page") page:Int=1,@Query("sort") sort:String="accuracy") : Call<KakaoSearchStudyRoomRespnose>
}