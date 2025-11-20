package com.balhae.historyapp.network.models

// 서버가 내려주는 JWT 토큰 응답 (예시)
data class KakaoLoginResponse(
    val accessToken: String,
    val refreshToken: String?,
    val member: MemberInfo?
)

data class MemberInfo(
    val id: Long?,
    val name: String?,
    val profileImageUrl: String?
)
