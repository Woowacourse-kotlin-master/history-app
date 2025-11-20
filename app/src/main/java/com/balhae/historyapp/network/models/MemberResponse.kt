package com.balhae.historyapp.network.models

data class MemberResponse(
    val id: Long?,
    val name: String?,
    val profileImageUrl: String?
    // TODO: Swagger에 추가 필드 있으면 여기에 확장
)
