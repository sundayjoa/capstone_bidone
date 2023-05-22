package com.example.bidone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner

class RealtimeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realtime)

        //스피너 항목 설정
        val Spinner = findViewById<Spinner>(R.id.spinner)

        val items = arrayOf("전체", "수공예품", "중고물품")

        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, items)
        Spinner.adapter = adapter
    }
}

