package com.balhae.historyapp

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class HistoryApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // 카카오 SDK 초기화 (AndroidManifest.xml의 meta-data에서 AppKey를 읽음)
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)
    }
}
