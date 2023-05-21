package com.example.bidone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

class WritingboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writingboard)

        //취소 버튼 누르면 다시 게시판 화면으로 전환
        val cancelButton = findViewById<Button>(R.id.cancelbutton)
        cancelButton.setOnClickListener {
            finish()
        }

        //스피너 관련
        val spinner = findViewById<Spinner>(R.id.spinner2)
        val items = arrayOf("수공예품", "중고물품")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        }
    }