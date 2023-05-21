package com.example.bidone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.example.bidone.databinding.ActivityRequestboardBinding

class RequestboardActivity : AppCompatActivity() {

    // 바인딩 선언

    private  lateinit var binding : ActivityRequestboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 데이터 바인딩
        binding = DataBindingUtil.setContentView(this,R.layout.activity_requestboard)

        // 바인딩 이동  의뢰게시판 -> 글쓰기
        binding.rwriteButton.setOnClickListener {
            val intent = Intent(this,RequestwritingActivity::class.java)
            startActivity(intent)
        }
        // 바인딩 이동 의뢰게시판 -> 채팅
        binding.rchatButton.setOnClickListener {
            val intent = Intent(this,RequestchatActivity::class.java)
            startActivity(intent)
        }
        
        //스피너 항목 설정
        val Spinner = findViewById<Spinner>(R.id.spinner)
        
        val items = arrayOf("의뢰게시판", "전체", "수공예품", "중고물품")
        
        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, items)
        Spinner.adapter = adapter


    }
}