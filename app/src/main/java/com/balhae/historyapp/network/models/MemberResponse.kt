package com.balhae.historyapp.network.models

import com.google.gson.annotations.SerializedName

data class MemberResponse(
    @SerializedName("userName")
    val userName: String,

    @SerializedName("profile")
    val profile: String,

    @SerializedName("heritageDtos")
    val heritageDtos: List<HeritageDto> = emptyList()
)

data class HeritageDto(
    @SerializedName("heritageImage")
    val heritageImage: String,

    @SerializedName("heritageText")
    val heritageText: String
)
