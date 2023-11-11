package com.example.bidone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class NoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        //뒤로 가기 버튼
        val cancelBtn = findViewById<ImageView>(R.id.bkButton)

        cancelBtn.setOnClickListener(){
            finish()
        }

        //title 넘겨받기
        val titleText = findViewById<TextView>(R.id.title)
        val title = intent.getStringExtra("title")

        titleText.text = title + " 에서 온 메시지 입니다."
    }
}