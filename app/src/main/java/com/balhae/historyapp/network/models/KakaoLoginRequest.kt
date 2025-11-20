package com.balhae.historyapp.network.models

// 카카오/네이버 SDK에서 받아온 accessToken, refreshToken을 서버에 넘기는 용도
data class KakaoLoginRequest(
    val accessToken: String,
    val refreshToken: String?
)
