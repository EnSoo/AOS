package com.mrhiles.aos

import com.mrhiles.aos.data.UserInfo

class G {
    companion object {
        var isLogin: Boolean= false // 로그인 여부
        var userInfo:UserInfo?=null
        var accessToken:String=""
    }
}