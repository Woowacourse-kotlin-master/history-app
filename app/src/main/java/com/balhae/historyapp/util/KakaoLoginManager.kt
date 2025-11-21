package com.balhae.historyapp.util

import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

object KakaoLoginManager {
    private const val TAG = "KakaoLoginManager"

    /**
     * 카카오 로그인 콜백
     */
    interface KakaoLoginCallback {
        fun onLoginSuccess(accessToken: String, refreshToken: String?)
        fun onLoginFailure(error: String)
    }

    /**
     * 카카오 로그인 수행
     */
    fun login(context: Context, callback: KakaoLoginCallback) {
        val callback = object : (OAuthToken?, Throwable?) -> Unit {
            override fun invoke(token: OAuthToken?, error: Throwable?) {
                if (error != null) {
                    Log.e(TAG, "카카오 로그인 실패", error)
                    callback.onLoginFailure(error.message ?: "알 수 없는 오류")
                } else if (token != null) {
                    Log.d(TAG, "카카오 로그인 성공")
                    Log.d(TAG, "Access Token: ${token.accessToken}")
                    Log.d(TAG, "Refresh Token: ${token.refreshToken}")

                    // 백엔드로 전송할 토큰
                    callback.onLoginSuccess(
                        accessToken = token.accessToken,
                        refreshToken = token.refreshToken
                    )
                }
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 없으면 카카오 계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    /**
     * 카카오 로그아웃
     */
    fun logout(callback: (Boolean) -> Unit) {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e(TAG, "로그아웃 실패", error)
                callback(false)
            } else {
                Log.d(TAG, "로그아웃 성공")
                callback(true)
            }
        }
    }

    /**
     * 사용자 정보 조회 (선택사항: 로그인 후 사용자 정보가 필요한 경우)
     */
    fun getUserInfo(callback: (String?, String?, String?) -> Unit) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 조회 실패", error)
                callback(null, null, null)
            } else if (user != null) {
                val email = user.kakaoAccount?.email
                val nickname = user.kakaoAccount?.profile?.nickname
                val profileImageUrl = user.kakaoAccount?.profile?.profileImageUrl
                Log.d(TAG, "사용자 정보: email=$email, nickname=$nickname")
                callback(email, nickname, profileImageUrl)
            }
        }
    }
}
