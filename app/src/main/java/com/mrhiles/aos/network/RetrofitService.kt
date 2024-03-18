package com.mrhiles.aos.network

import com.mrhiles.aos.data.KakaoSearchStudyRoomRespnose
import com.mrhiles.aos.data.LoginRequire
import com.mrhiles.aos.data.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitService {
    //카카오 키워드로 장소 검색하기
    // 스터디 장소를 확인하기 위한 api 요청. KakaoAK REST API Key
    @Headers("Authorization: KakaoAK e85398842cfcf70cda90f41067f11aa6")
    @GET("/v2/local/search/keyword.json")
    fun searchStudyRoomToString(@Query("query") query:String="스터디룸", @Query("x") longitute:String="", @Query("y") latitude:String="", @Query("radius") radius:Int=1000, @Query("page") page:Int=1,@Query("sort") sort:String="accuracy") : Call<KakaoSearchStudyRoomRespnose>

    // 서비스 페이지에 요청
    @FormUrlEncoded
    @POST("/sign/login_init.php")
    fun getLogin(@Field("login_type") login_type:String, @Field("access_token") access_token: String="", @Field("email") email: String="", @Field("password") password: String="") : Call<LoginResponse>// POST 방식으로 전달
//    @GET("/sign/login_init.php")
//    fun getLogin(@Query("login_type") login_type:String, @Query("access_token") access_token: String="", @Query("email") email: String="", @Query("password") password: String="") : Call<LoginResponse>// POST 방식으로 전달
//    네아로 회원정보 프로필 api.. 요청
//    @GET("/v1/nid/me")
//    fun getNidUserInfo(@Header("Authorization") authorization:String) : Call<String>
//    네이버 로그인 인증 요청
//    @GET("/auth/naver/token_init.php")
//    fun getNaverLogin(
//        @Query("refresh_token") refreshToken: String?, //값 code 고정
//    ) : Call<String>
}