package com.balhae.historyapp.network.models

import com.google.gson.annotations.SerializedName

// 서버가 내려주는 JWT 토큰 응답
data class KakaoLoginResponse(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String,

    @SerializedName("nameFlag")
    val nameFlag: Boolean = false
)
