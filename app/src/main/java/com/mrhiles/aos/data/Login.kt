package com.mrhiles.aos.data

data class LoginResponse(var id:String, var email:String, var access_token:String, var refresh_token:String, var error:String="400")