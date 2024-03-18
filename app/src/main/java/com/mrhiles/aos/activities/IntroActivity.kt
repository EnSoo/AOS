package com.mrhiles.aos.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.mrhiles.aos.G
import com.mrhiles.aos.R
import com.mrhiles.aos.data.UserInfo

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        //getLoingInfo()
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,MainActivity::class.java))
        },3000)
    }

    private fun getLoingInfo() {
        val sharedPreferences = this.getSharedPreferences("logininfo", Context.MODE_PRIVATE)
        sharedPreferences.apply {
            G.isLogin=getBoolean("is_login",false)
            G.accessToken=getString("access_token","")!!
            G.refreshToken=getString("refresh_token","")!!
            G.userInfo= UserInfo(getString("providerId","")!!,getString("email","")!!, getString("login_type","")!!)
        }
    }
}