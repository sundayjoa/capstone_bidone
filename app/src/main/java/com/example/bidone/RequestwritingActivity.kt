package com.example.bidone

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class RequestwritingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requestwriting)

        //취소 버튼 누르면 다시 게시판 화면으로 전환
        val recancelButton = findViewById<Button>(R.id.recancelbutton)
        recancelButton.setOnClickListener {
            finish()
        }

        val rwriteButton = findViewById<Button>(R.id.rwriteButton)
        //글쓰기 버튼 큻릭 이벤트
        //글쓰기 버튼 누르면 팝업창 띄우기
        rwriteButton.setOnClickListener {

            val builder = AlertDialog.Builder(this)

            // LayoutInflater 객체 생성
            val inflater = layoutInflater

            // 팝업창에 표시할 뷰 지정
            val view = inflater.inflate(R.layout.activity_requestdialog, null)
            builder.setView(view)

            // AlertDialog 객체 생성 및 표시
            val alertDialog = builder.create()
            alertDialog.show()


            // 팝업창 취소 버튼 클릭 이벤트
            val cancelButton = view.findViewById<Button>(R.id.cbtn)

            cancelButton.setOnClickListener {
                alertDialog.dismiss()
            }

            //의뢰 번호 지정해주기
            val number_url = "http://192.168.219.106/request_number.php"

            val number_queue = Volley.newRequestQueue(this)
            val number_stringRequest = StringRequest(
                Request.Method.GET, number_url,
                Response.Listener<String> { response ->
                    // Display the response string
                    Log.d("Response", response)
                    val request_numberText = view.findViewById<TextView>(R.id.request_number)
                    request_numberText.text = response
                },
                Response.ErrorListener { /* Handle error */ })

            number_queue.add(number_stringRequest)

            val comBtn = view.findViewById<Button>(R.id.combtn)

            comBtn.setOnClickListener(){

                //DB에 데이터 전송 및 저장
                val random_number = view.findViewById<TextView>(R.id.request_number)
                val userID = "userID"
                val userName = "userName"
                val inputtitle = findViewById<TextView>(R.id.inputTitle)
                val inputRequest = findViewById<TextView>(R.id.inputRequest)
                val hope = findViewById<TextView>(R.id.hope_purchase)

                val request_number = random_number.text.toString()
                val title = inputtitle.text.toString()
                val content = inputRequest.text.toString()
                val hope_purchase = hope.text.toString()

                // 업로드 날짜를 저장하기 위한 현재 시간 가져오기
                val currentTime = Calendar.getInstance().time
                // 시간 형식 변환
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val upload = dateFormat.format(currentTime)

                //DB에 데이터 전송하기
                val request = object : StringRequest(
                    //ip 현재 ip 주소로 항상 바꾸기
                    Method.POST, "http://192.168.219.106/requestinfo.php",
                    Response.Listener { response ->
                        //서버에서 전송하는 응답 내용 확인
                        Log.d("Response", response)
                        Log.d("JSON Data", response)

                        val responseData = JSONObject(response)
                        if (responseData.has("success")) {
                            val success = responseData.getBoolean("success")
                            if (success) {
                                // Request succeeded
                                Toast.makeText(this, "요청이 성공했습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                // Request failed
                                Toast.makeText(this, "요청이 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Handle the case where the key "성공!" is not found
                        }
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(
                            this,
                            "An error occurred: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    //서버에 정보 넘겨주기
                    override fun getParams(): MutableMap<String, String>? {
                        val params = HashMap<String, String>()

                        params["request_number"] = request_number
                        params["userID"] = userID
                        params["userName"] = userName
                        params["title"] = title
                        params["content"] = content
                        params["hope_purchase"] = hope_purchase
                        params["upload_date"] = upload
                        return params
                    }
                }

                val queue = Volley.newRequestQueue(this)
                queue.add(request)

                //팝업 창 닫기
                alertDialog.dismiss()
                //게시판 창 닫기
                finish()
            }

        }
    }
}