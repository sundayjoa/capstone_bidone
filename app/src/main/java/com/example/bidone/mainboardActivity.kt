package com.example.bidone

import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
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
import java.text.SimpleDateFormat
import java.util.*
import android.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.sql.DriverManager
import java.sql.SQLException

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
        val starttime = "시작 시간: " + intent.getStringExtra("date") + " " + intent.getStringExtra("time")

        starttimeView.text = starttime

        //판매자 ID 넘겨 받기
        val data = intent.getStringExtra("userID")


        //경매 시작 시간 이후 버튼 활성화 시키기
        val ptBtn = findViewById<Button>(R.id.ptBtn)

        // 현재 시간 가져오기
        val currentTime = Calendar.getInstance().time

        // 시간 포맷 지정
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)

        // 현재 시간과 비교할 시간 생성
        val compareTime = formatter.parse(intent.getStringExtra("date") + " " + intent.getStringExtra("time"))

        // 현재 시간이 비교할 시간 이후인지 확인
        if (currentTime.after(compareTime)) {
            // 버튼 활성화 및 색상 변경
            ptBtn.isEnabled = true
            ptBtn.setBackgroundColor(Color.RED)
        } else {
            // 버튼 비활성화 및 색상 변경
            ptBtn.isEnabled = false
        }


        //경매 참여 버튼 클릭 이벤트
        ptBtn.setOnClickListener {

            //경매 참여 버튼 누르면 팝업창 띄우기
            // AlertDialog.Builder 객체 생성
            val builder = AlertDialog.Builder(this)

            // LayoutInflater 객체 생성
            val inflater = layoutInflater

            // 팝업창에 표시할 뷰 지정
            val view = inflater.inflate(R.layout.activity_boarddialog, null)
            builder.setView(view)

            // AlertDialog 객체 생성 및 표시
            val alertDialog = builder.create()
            alertDialog.show()


            //작품 정보 가져오기
            val numberTextView = view.findViewById<TextView>(R.id.worknumber)
            val worknumberinfo = intent.getStringExtra("worknumber")

            numberTextView.text = worknumberinfo

            //증가 가격 가져오기
            val increaseTextView = view.findViewById<TextView>(R.id.increase)
            val increaseinfo = intent.getStringExtra("increase")

            increaseTextView.text = increaseinfo


            // 팝업창 취소 버튼 클릭 이벤트
            val cancelButton = view.findViewById<Button>(R.id.cbtn)

            cancelButton.setOnClickListener {
                alertDialog.dismiss()
            }


            //MySQL에서 정보 찾아오기
            //work_number를 이용해 찾기
            val workNumber = numberTextView.text.toString()
            val current = view.findViewById<TextView>(R.id.current)


            val url = "http://192.168.219.106/main_auction.php"
            val postData = "workNumber=$workNumber"

            Thread {
                try {
                    val httpClient = OkHttpClient()
                    val request = Request.Builder()
                        .url(url)
                        .post(postData.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull()))
                        .build()

                    httpClient.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        val responseBody = response.body!!.string()
                        if (responseBody == "0") {
                            // 만약 입찰한 기록이 없을 때.
                            current.text = intent.getStringExtra("start")
                        } else {
                            runOnUiThread {
                                current.text = responseBody
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("TAG", "Exception: ${e.message}")
                }
            }.start()


            //입찰 버튼 이벤트
            val completeBtn = view.findViewById<Button>(R.id.combtn)
            completeBtn.setOnClickListener{
                val bid_builder = AlertDialog.Builder(this)
                bid_builder.setMessage("정말로 입찰하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("확인") { dialog, id ->
                        // MySQL에 정보 저장
                        val workNumber = numberTextView.text.toString()
                        val data = intent.getStringExtra("userID").toString()
                        val consumerID = "userID"
                        val increaseinfo = increaseTextView.text.toString()
                        val currentPrice = current.text.toString()
                        val price = (increaseinfo.toInt() + currentPrice.toInt()).toString()

                        //DB에 데이터 전송하기
                        val request = object : StringRequest(
                            Method.POST, "http://192.168.219.106/auction.php",
                            Response.Listener { response ->
                                //서버에서 전송하는 응답 내용 확인
                                Log.d("Response", response)
                                Log.d("JSON Data", response)

                                val responseData = JSONObject(response.replace("<br>", ""))
                                if (responseData.has("success")) {
                                    val success = responseData.getBoolean("success")
                                    if (success) {
                                        // Request succeeded
                                        Toast.makeText(this, "요청이 성공했습니다.", Toast.LENGTH_SHORT)
                                            .show()
                                    } else {
                                        // Request failed
                                        Toast.makeText(this, "요청이 실패했습니다.", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            },
                            Response.ErrorListener { error ->
                                //서버에서 전송하는 응답 내용 확인
                                Log.d("Error", error.toString())
                            }

                        ) {
                            //서버에 정보 넘겨주기
                            override fun getParams(): MutableMap<String, String>? {
                                val params = HashMap<String, String>()

                                params["work_number"] = workNumber
                                params["sellerID"] = data
                                params["consumerID"] = consumerID
                                params["price"] = price

                                return params

                            }
                        }

                        val queue = Volley.newRequestQueue(this)
                        queue.add(request)
                    }
                    .setNegativeButton("취소") { dialog, id ->
                        dialog.cancel()
                    }
                val alert = bid_builder.create()
                alert.show()
            }


        }

    }

}