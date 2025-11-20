package com.balhae.historyapp.network.models

data class HeritageRecognizeResponse(
    val items: List<HeritageItem> // TODO: 실제 필드명 확인 (ex: histories, heritageList 등)
)

data class HeritageItem(
    val id: Long?,
    val name: String?,
    val description: String?,
    val imageUrl: String?,
    val recognizedAt: String?
)
