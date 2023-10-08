package com.example.bidone

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.Request.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class mainboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainboard)

        //작품번호 넘겨받기
        val workNumberTextView = findViewById<TextView>(R.id.work_number)
        val workNumber = intent.getStringExtra("worknumber")

        workNumberTextView.text = workNumber

        //title 넘겨받기
        val titleView = findViewById<TextView>(R.id.titleView)
        val title = intent.getStringExtra("title")

        titleView.text = title

        //username 넘겨받기
        val userNameView = findViewById<TextView>(R.id.userView)
        val userName = intent.getStringExtra("userName")

        userNameView.text = userName

        //detail_explanation 넘겨받기
        val detailExView = findViewById<TextView>(R.id.detail_explanation)
        val detailEx = intent.getStringExtra("detail_explanation")

        detailExView.text = detailEx

        //start 넘겨받기
        val startView = findViewById<TextView>(R.id.start)
        val start = "시작 가격: " + intent.getStringExtra("start")

        startView.text = start

        //increase 넘겨받기
        val increaseView = findViewById<TextView>(R.id.increase)
        val increase = "증가 가격: " + intent.getStringExtra("increase")

        increaseView.text = increase

        //경매시작시간 넘겨 받기
        val starttimeView = findViewById<TextView>(R.id.start_time)
        val starttime = "시작 시간: " + intent.getStringExtra("date") + intent.getStringExtra("time")

        starttimeView.text = starttime

    }

}