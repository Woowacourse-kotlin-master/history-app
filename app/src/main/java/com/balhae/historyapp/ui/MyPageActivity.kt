package com.balhae.historyapp.ui

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.balhae.historyapp.R
import com.balhae.historyapp.network.RetrofitClient
import com.balhae.historyapp.network.models.HeritageDto
import com.balhae.historyapp.network.models.MemberResponse
import com.balhae.historyapp.network.models.PointResponse
import com.balhae.historyapp.ui.adapters.HeritageGridAdapter
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageActivity : AppCompatActivity() {

    private lateinit var btnMypageBack: ImageButton
    private lateinit var ivMypageProfile: ImageView
    private lateinit var tvMypageName: TextView
    private lateinit var tvMypagePoint: TextView
    private lateinit var tvMypageHeritageCount: TextView
    private lateinit var tvMypageEmpty: TextView
    private lateinit var rvMypageHeritage: RecyclerView
    private lateinit var adapter: HeritageGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        initializeViews()
        setupListeners()
        loadMypageData()
    }

    private fun initializeViews() {
        btnMypageBack = findViewById(R.id.btnMypageBack)
        ivMypageProfile = findViewById(R.id.ivMypageProfile)
        tvMypageName = findViewById(R.id.tvMypageName)
        tvMypagePoint = findViewById(R.id.tvMypagePoint)
        tvMypageHeritageCount = findViewById(R.id.tvMypageHeritageCount)
        tvMypageEmpty = findViewById(R.id.tvMypageEmpty)
        rvMypageHeritage = findViewById(R.id.rvMypageHeritage)

        adapter = HeritageGridAdapter(emptyList())
        rvMypageHeritage.layoutManager = GridLayoutManager(this, 2)
        rvMypageHeritage.adapter = adapter
    }

    private fun setupListeners() {
        btnMypageBack.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        adapter.setOnItemClickListener { heritage ->
            showHeritageDetailDialog(heritage)
        }
    }

    private fun loadMypageData() {
        loadMemberInfo()
        loadPoint()
    }

    private fun loadMemberInfo() {
        val api = RetrofitClient.getApiService(this)
        api.getMemberInfo().enqueue(object : Callback<MemberResponse> {
            override fun onResponse(
                call: Call<MemberResponse>,
                response: Response<MemberResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        // 사용자명 표시
                        tvMypageName.text = body.userName ?: "사용자"

                        // 프로필 이미지 로드
                        if (!body.profile.isNullOrEmpty()) {
                            Picasso.get()
                                .load(body.profile)
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .error(R.drawable.ic_profile_placeholder)
                                .into(ivMypageProfile)
                        }

                        // Heritage 목록 업데이트
                        if (body.heritageDtos != null && body.heritageDtos.isNotEmpty()) {
                            adapter.updateData(body.heritageDtos)
                            tvMypageHeritageCount.text = "${body.heritageDtos.size}개"
                            rvMypageHeritage.visibility = android.view.View.VISIBLE
                            tvMypageEmpty.visibility = android.view.View.GONE
                        } else {
                            // 빈 목록 메시지 표시
                            tvMypageHeritageCount.text = "0개"
                            rvMypageHeritage.visibility = android.view.View.GONE
                            tvMypageEmpty.visibility = android.view.View.VISIBLE
                        }
                    } else {
                        Toast.makeText(this@MyPageActivity, "응답 데이터가 없습니다", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        this@MyPageActivity,
                        "회원 정보 조회 실패: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                Toast.makeText(
                    this@MyPageActivity,
                    "회원 정보 오류: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun loadPoint() {
        val api = RetrofitClient.getApiService(this)
        api.getPoint().enqueue(object : Callback<PointResponse> {
            override fun onResponse(
                call: Call<PointResponse>,
                response: Response<PointResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    tvMypagePoint.text = "${body?.point ?: 0}P"
                } else {
                    tvMypagePoint.text = "0P"
                }
            }

            override fun onFailure(call: Call<PointResponse>, t: Throwable) {
                tvMypagePoint.text = "0P"
            }
        })
    }

    private fun showHeritageDetailDialog(heritage: HeritageDto) {
        val dialog = Dialog(this, R.style.TransparentDialog)
        dialog.setContentView(R.layout.dialog_heritage_detail)

        // 다이얼로그 윈도우 설정
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)

            // 화면 크기의 90% 너비, 최대 높이 제한 (스크롤 가능하도록)
            val maxHeight = (resources.displayMetrics.heightPixels * 0.8).toInt()
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                maxHeight
            )

            // 배경 dim 추가 (70% 어두움)
            val lp = attributes
            lp.dimAmount = 0.7f
            attributes = lp
            addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

        val ivDialogImage = dialog.findViewById<ImageView>(R.id.ivDialogImage)
        val tvDialogDescription = dialog.findViewById<TextView>(R.id.tvDialogDescription)
        val tvDialogDate = dialog.findViewById<TextView>(R.id.tvDialogDate)
        val btnDialogClose = dialog.findViewById<Button>(R.id.btnDialogClose)
        val btnDialogShare = dialog.findViewById<Button>(R.id.btnDialogShare)

        // 이미지 로드
        if (!heritage.heritageImage.isNullOrEmpty()) {
            Picasso.get()
                .load(heritage.heritageImage)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(ivDialogImage)
        }

        // 설명 표시
        tvDialogDescription.text = heritage.heritageText ?: "문화재 정보"

        // 날짜 표시 (현재 날짜)
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        tvDialogDate.text = dateFormat.format(java.util.Date())

        // 닫기 버튼
        btnDialogClose.setOnClickListener {
            dialog.dismiss()
        }

        // 공유 버튼
        btnDialogShare.setOnClickListener {
            val shareText = "문화재: ${heritage.heritageText}"
            val shareIntent = android.content.Intent().apply {
                action = android.content.Intent.ACTION_SEND
                putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(android.content.Intent.createChooser(shareIntent, "공유하기"))
            dialog.dismiss()
        }

        dialog.show()
    }
}