package com.mrhiles.aos.network

import com.mrhiles.aos.data.KakaoSearchStudyRoomRespnose
import com.mrhiles.aos.data.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

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
    @FormUrlEncoded
    @POST("/sign/email_signup.php")
    fun getSignUp(@Field("email") email:String, @Field("password") password:String, @Field("name") name:String, @Field("nickname") nickname: String) : Call<String>// POST 방식으로 전달
    // 리프레쉬 토큰을 이용하여 액세스 토큰 새로 발급 요청
    @POST("/user/token_generation.php")
    fun tokenGenrate(@Field("refresh_token") refreshToken:String, @Field("token_check_type") tokenCheckType:String) : Call<UserCheck>//액세스 토큰 요청

    // 서비스 별 url 값 달라지며, requrestData는 accessToken을 포함하고 있으며, 그 외 데이터는 서비스별로 보내는 갯수가 다름 getResponseData은 error는 그대로이나, 서비스 별로 오는 데이터가 다름(Json) 입맛에 맞게 사용 가능하며, 서비스별 data 클래스는 추가할 예정
    @POST
    fun serviceRequest(@Url url: String, @Body requestData:requestData) : Call<responseData>

    @FormUrlEncoded
    @POST("/user/logout.php")
    fun logout(@Field("refresh_token") refreshToken: String): Call<String>
}