package com.balhae.historyapp.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.balhae.historyapp.R
import com.balhae.historyapp.network.RetrofitClient
import com.balhae.historyapp.network.models.MemberResponse
import com.balhae.historyapp.network.models.PointResponse
import com.balhae.historyapp.ui.adapters.HeritageGridAdapter
import com.balhae.historyapp.util.HeritageRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnBackHome: ImageButton
    private lateinit var tvName: TextView
    private lateinit var tvPointProfile: TextView
    private lateinit var rvHeritage: RecyclerView
    private lateinit var adapter: HeritageGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        btnBackHome = findViewById(R.id.btnProfileBackHome)
        tvName = findViewById(R.id.tvProfileName)
        tvPointProfile = findViewById(R.id.tvProfilePoint)
        rvHeritage = findViewById(R.id.rvHeritage)

        btnBackHome.setOnClickListener {
            finish()
        }

        adapter = HeritageGridAdapter(HeritageRepository.lastRecognized)
        rvHeritage.layoutManager = GridLayoutManager(this, 2)
        rvHeritage.adapter = adapter

        loadMemberInfo()
        loadPoint()
    }

    private fun loadMemberInfo() {
        val api = RetrofitClient.getApiService(this)
        api.getMemberInfo().enqueue(object : Callback<MemberResponse> {
            override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    tvName.text = body?.name ?: "사용자"
                } else {
                    Toast.makeText(this@ProfileActivity, "회원 정보 조회 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "회원 정보 오류: ${t.message}", Toast.LENGTH_SHORT).show()
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
                    tvPointProfile.text = "${body?.point ?: 0}P"
                } else {
                    tvPointProfile.text = "0P"
                }
            }

            override fun onFailure(call: Call<PointResponse>, t: Throwable) {
                tvPointProfile.text = "0P"
            }
        })
    }
}
