package com.mrhiles.aos.data

data class NaverLogin(
    val resultcode:String,
    val message:String,
    val response:NaverResponse
)
data class NaverResponse(val id:String, val email:String)