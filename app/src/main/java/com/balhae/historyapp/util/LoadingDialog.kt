package com.balhae.historyapp.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.balhae.historyapp.R

class LoadingDialog(context: Context) : Dialog(context) {

    private var tvLoadingMessage: TextView? = null
    private var isInitialized = false

    init {
        // Dialog 생성 시 바로 초기화
        setContentView(R.layout.dialog_loading)

        // UI 초기화 (null-safe)
        try {
            tvLoadingMessage = findViewById(R.id.tvLoadingMessage)
            isInitialized = true
        } catch (e: Exception) {
            isInitialized = false
        }

        // 다이얼로그 설정
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        // 배경을 투명하게 설정 (테두리 없음)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // init block에서 이미 처리됨
    }

    fun showDialog(message: String = "로딩 중...") {
        if (isInitialized && tvLoadingMessage != null) {
            tvLoadingMessage?.text = message
            if (!isShowing) {
                show()
            }
        }
    }

    override fun dismiss() {
        try {
            if (isShowing) {
                super.dismiss()
            }
        } catch (e: Exception) {
            // 이미 dismiss 되었거나 문제가 있을 때 무시
        }
    }
}