package com.mrhiles.aos.data

//data class NaverLogin(
//    val resultcode:String,
//    val message:String,
//    val response:NaverResponse
//)
//data class NaverResponse(val id:String, val email:String)
data class LoginRequire(val login_type:String, val access_token: String="", val email: String="", val password: String="")
data class LoginResponse(var id:String, var email:String, var access_token:String, var refresh_token:String, var error:String="400")