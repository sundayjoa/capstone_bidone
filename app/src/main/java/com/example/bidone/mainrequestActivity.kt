package com.example.bidone

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class mainrequestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainrequest)

        //title 넘겨받기
        val titleView = findViewById<TextView>(R.id.titleView)
        val title = intent.getStringExtra("title")

        titleView.text = title

        //userName 넘겨받기
        val userNameView = findViewById<TextView>(R.id.userView)
        val userName = intent.getStringExtra("userName")

        userNameView.text = userName

        //content 넘겨 받기
        val contentView = findViewById<TextView>(R.id.content)
        val content = intent.getStringExtra("content")

        contentView.text = content

        //hope_purchase 넘겨 받기
        val hopeView = findViewById<TextView>(R.id.hope_purchase)
        val hope = "구매 희망가: " + intent.getStringExtra("hope_purchase")

        hopeView.text = hope

        //uploadData 넙겨받기
        val uploadView = findViewById<TextView>(R.id.upload_date)
        val upload_date = intent.getStringExtra("upload_date")

        uploadView.text = "업로드 시간:" + upload_date

        //userID 넘겨받기
        val userID = intent.getStringExtra("userID")

        //request_number 넘겨받기
        val request_number = intent.getStringExtra("request_number")

        //뒤로 가기 버튼
        val bkBtn = findViewById<ImageView>(R.id.bkButton)

        bkBtn.setOnClickListener{
            finish()
        }

        //쪽지 버튼
        val chatBtn = findViewById<ImageView>(R.id.chattingBtn)

        chatBtn.setOnClickListener{

            //쪽지 보내기 다이얼로그 띄우기
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
                        //의뢰 번호
                        val request_number = request_numberText.text.toString()
                        //title
                        val title = intent.getStringExtra("title").toString()
                        //sender_id, sender_name
                        val (userId, userName) = getUserInfo(this)
                        //receiver_id, receiver_name
                        val receiver_id = intent.getStringExtra("userID").toString()
                        val receiver_name = intent.getStringExtra("userName").toString()
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
                            Request.Method.POST, "http://192.168.219.108/request_note.php",
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