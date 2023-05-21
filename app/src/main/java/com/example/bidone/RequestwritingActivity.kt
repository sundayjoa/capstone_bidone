package com.example.bidone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class RequestwritingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requestwriting)

        //취소 버튼 누르면 다시 게시판 화면으로 전환
        val recancelButton = findViewById<Button>(R.id.recancelbutton)
        recancelButton.setOnClickListener {
            finish()
        }
    }
}