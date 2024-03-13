package com.mrhiles.aos.network

import com.mrhiles.aos.data.GetToken
import com.mrhiles.aos.data.KakaoSearchStudyRoomRespnose
import com.mrhiles.aos.data.NaverAuthorize
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitService {
    //카카오 키워드로 장소 검색하기
    // 스터디 장소를 확인하기 위한 api 요청. KakaoAK REST API Key
    @Headers("Authorization: KakaoAK e85398842cfcf70cda90f41067f11aa6")
    @GET("/v2/local/search/keyword.json")
    fun searchStudyRoomToString(@Query("query") query:String="스터디룸", @Query("x") longitute:String="", @Query("y") latitude:String="", @Query("radius") radius:Int=1000, @Query("page") page:Int=1,@Query("sort") sort:String="accuracy") : Call<KakaoSearchStudyRoomRespnose>

    //네아로 회원정보 프로필 api.. 요청
    @GET("/v1/nid/me")
    fun getNidUserInfo(@Header("Authorization") authorization:String) : Call<String>

    //네이버 로그인 인증 요청
    @GET("/auth/naver/token_init.php")
    fun getNaverLoginauthorize( //회원가입
        @Query("refresh_token") refresh_token: String?
    ) : Call<String>

    //네이버 로그인 토큰을 발급받기 위해 redirect_uri에 요청
    @FormUrlEncoded
    @POST("login/naver/getToken.php")
    fun getNaverToken(
        @Field("grant_type") responseType: String, // 발급 : authorization_code
        @Field("code") code : String,
        @Field("state") state: String
    ) : Call<GetToken>

    //네이버 로그인 토큰을 갱신
    @FormUrlEncoded
    @POST("login/naver/RefreshToken.php")
    fun refreshNaverToken(
        @Field("grant_type") responseType: String, // 갱신 : refresh_token
        @Field("refresh_token") refreshToken: String
    ) : Call<GetToken>

    //네이버 로그인 토큰을 삭제
    @FormUrlEncoded
    @POST("login/naver/deleteToken.php")
    fun deleteNaverToken(
        @Field("grant_type") responseType: String, // 삭제 : delete
        @Field("access_token") accessToken: String
    ) : Call<GetToken>
}