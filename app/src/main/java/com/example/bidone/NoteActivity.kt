package com.example.bidone

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

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

        //쪽지보내기 버튼
        val noteBtn = findViewById<ImageView>(R.id.sendButton)
        noteBtn.setOnClickListener(){
            val builder = AlertDialog.Builder(this)

            // LayoutInflater 객체 생성
            val inflater = layoutInflater

            // 팝업창에 표시할 뷰 지정
            val view = inflater.inflate(R.layout.activity_notedialog, null)
            builder.setView(view)

            // AlertDialog 객체 생성 및 표시
            val alertDialog = builder.create()
            alertDialog.show()

            //의뢰 번호 표시하기
            val request_numberText = view.findViewById<TextView>(R.id.request_number)
            val request_numberinfo = intent.getStringExtra("request_number")

            request_numberText.text = request_numberinfo

            //취소 버튼 이벤트
            val cancelBtn = view.findViewById<Button>(R.id.cbtn)

            cancelBtn.setOnClickListener(){
                alertDialog.dismiss()
            }

//확인 버튼 이벤트
            val comBtn = view.findViewById<Button>(R.id.combtn)


            comBtn.setOnClickListener(){
                val bid_builder = AlertDialog.Builder(this)
                bid_builder.setMessage("쪽지를 보내시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("확인") { dialog, id ->

                        //MySQL에 정보 저장
                        val request_number = request_numberText.text.toString()
                        val title = intent.getStringExtra("title").toString()
                        //sender_id, sender_name
                        val (userId, userName) = getUserInfo(this)
                        val receiver_id = intent.getStringExtra("sender_id").toString()
                        val receiver_name = intent.getStringExtra("sender_name").toString()
                        //content
                        val contentText = view.findViewById<EditText>(R.id.content)
                        val content = contentText.text.toString()
                        //purchase
                        val hope_purchase = intent.getStringExtra("hope_purchase").toString()
                        //sender_react
                        val sender_react = "true"
                        //send_time
                        val currentTime = Calendar.getInstance().time
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val send_time = dateFormat.format(currentTime)

                        //DB에 데이터 전송하기
                        val request = object : StringRequest(
                            Request.Method.POST, "http://192.168.219.106/request_note.php",
                            Response.Listener { response ->
                                //서버에서 전송하는 응답 내용 확인
                                Log.d("Response", response)
                                Log.d("JSON Data", response)

                            },
                            Response.ErrorListener { error ->
                                //서버에서 전송하는 응답 내용 확인
                                Log.d("Error", error.toString())
                            }

                        ){
                            //서버에 정보 넘겨주기
                            override fun getParams(): MutableMap<String, String>? {
                                val params = HashMap<String, String>()

                                params["request_number"] = request_number
                                params["title"] = title
                                userId?.let {
                                    params["sender_id"] = it
                                }

                                userName?.let {
                                    params["sender_name"] = it
                                }
                                params["receiver_id"] = receiver_id
                                params["receiver_name"] = receiver_name
                                params["content"] = content
                                params["hope_purchase"] = hope_purchase
                                params["sender_react"] = sender_react
                                params["send_time"] = send_time

                                return params

                            }
                        }

                        val queue = Volley.newRequestQueue(this)
                        queue.add(request)
                        alertDialog.dismiss()
                    }
                    .setNegativeButton("취소") { dialog, id ->
                        dialog.cancel()
                    }

                val alert = bid_builder.create()
                alert.show()
            }
        }
    }

    fun getUserInfo(context: Context): Pair<String?, String?> {
        val sharedPreferences = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("ID", null)
        val userName = sharedPreferences.getString("userName", null)
        return Pair(userId, userName)
    }
}