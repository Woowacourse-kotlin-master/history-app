package com.balhae.historyapp.network

import com.balhae.historyapp.network.models.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // 카카오 소셜 로그인 (서버 쪽에 카카오 토큰 전달)
    @POST("api/oauth2/login/KAKAO")
    fun kakaoLogin(
        @Body request: KakaoLoginRequest
    ): Call<KakaoLoginResponse>

    // 마이페이지 회원 정보
    @GET("api/member")
    fun getMemberInfo(): Call<MemberResponse>

    // 포인트 조회
    @GET("api/point")
    fun getPoint(): Call<PointResponse>

    // 문화재 이미지 인식
    @Multipart
    @POST("api/heritage")
    fun recognizeHeritage(
        @Part image: MultipartBody.Part
    ): Call<HeritageRecognizeResponse>
}
