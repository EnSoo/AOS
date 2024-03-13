package com.mrhiles.aos.data

data class NaverLogin(
    val resultcode:String,
    val message:String,
    val response:NaverResponse
)
data class NaverResponse(val id:String, val email:String)
data class NaverAuthorize(val code:String, val state:String)

// error 200 토큰 얻기 성공, 300 토큰 얻기 실패, 400 토큰 만료
data class GetToken(val response: NaverResponse, val accessToken:String, val refreshToken:String, val loginType:String, val error:String)