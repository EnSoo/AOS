package com.mrhiles.aos

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "6158aa373d49c04e09a5954a812ea741")
    }
}