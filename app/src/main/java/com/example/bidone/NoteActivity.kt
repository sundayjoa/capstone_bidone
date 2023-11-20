package com.example.bidone

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class NoteActivity : AppCompatActivity() {

    // 리사이클러뷰 선언
    // 클래스 멤버로 선언
    private lateinit var recyclerView: RecyclerView

    // 리사이클러뷰 어댑터 선언
    // 클래스 멤버로 선언
    private lateinit var boardAdapter: BoardAdapter

    private var requestNumber: String? = null
    private var senderId: String? = null
    private var receiverId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        //리사이클러뷰 코드
        //정보 넘겨주기
        requestNumber = intent.getStringExtra("request_number")
        senderId = intent.getStringExtra("sender_id")
        receiverId = intent.getStringExtra("receiver_id")

        // 리사이클러뷰 초기화
        recyclerView = findViewById<RecyclerView>(R.id.boardrecyclerView) // 클래스 멤버로 선언한 변수 사용
        boardAdapter = BoardAdapter() // 빈 어댑터 생성
        recyclerView.adapter = boardAdapter
        recyclerView.layoutManager = LinearLayoutManager(this) // requireContext()를 this로 변경

        // MySQL 데이터 가져오기 및 리사이클러뷰에 표시
        fetchDataFromMySQL()


        //뒤로 가기 버튼
        val cancelBtn = findViewById<ImageView>(R.id.bkButton)

        cancelBtn.setOnClickListener(){
            finish()
        }

        //거래하기 버튼
        val findBtn = findViewById<ImageView>(R.id.find_Button)
        findBtn.setOnClickListener(){

            val builder = AlertDialog.Builder(this)

            // LayoutInflater 객체 생성
            val inflater = layoutInflater

            // 팝업창에 표시할 뷰 지정
            val view = inflater.inflate(R.layout.activity_finddialog, null)
            builder.setView(view)

            // AlertDialog 객체 생성 및 표시
            val alertDialog = builder.create()
            alertDialog.show()

            val hopePurchaseEditText = view.findViewById<EditText>(R.id.hope_purchase)
            val hope = intent.getStringExtra("hope_purchase") ?: ""

            hopePurchaseEditText.setText(hope)

            val findBtn = view.findViewById<Button>(R.id.Findbutton)

            findBtn.setOnClickListener(){

                val hope = view.findViewById<TextView>(R.id.hope_purchase)

                val request_number = intent.getStringExtra("request_number").toString()
                val (userId, userName) = getUserInfo(this)
                val hope_purchase = hope.text.toString()

                val request = object : StringRequest(
                    //ip 현재 ip 주소로 항상 바꾸기
                    Method.POST, "http://192.168.219.106/note_pay.php",
                    Response.Listener { response ->
                        //서버에서 전송하는 응답 내용 확인
                        Log.d("Response", response)
                        Log.d("JSON Data", response)

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
                        userId?.let {
                            params["sellerID"] = it
                        }

                        userName?.let {
                            params["sellerName"] = it
                        }
                        params["hope_purchase"] = hope_purchase

                        return params
                    }
                }

                val queue = Volley.newRequestQueue(this)
                queue.add(request)

                //팝업 창 닫기
                alertDialog.dismiss()
            }

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

    //리사이클러뷰 설정
    // 1. MySQL 데이터를 가져오기 위한 PHP 파일의 URL
    val phpUrl = "http://192.168.219.106/note_content.php"

    data class BoardItem(
        val title: String,
        val sender_id: String,
        val sender_name: String,
        val receiver_id: String,
        val receiver_name: String,
        val content: String,
        val picture: String,
        val hope_purchase: String,
        val send_time: String
    )

    //리사이클러뷰에 Mysql 연동
    private fun fetchDataFromMySQL() {

        // MySQL 데이터 가져오기
        // (네트워크 요청 등을 위해 별도의 스레드에서 실행)
        val boardItems = mutableListOf<BoardItem>()
        val phpUrlWithParams = "$phpUrl?requestNumber=$requestNumber&senderId=$senderId&receiverId=$receiverId"
        val request = okhttp3.Request.Builder().url(phpUrlWithParams).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 요청 실패 시 처리할 내용
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                val jsonData = response.body?.string()

                // JSON 데이터 파싱
                // jsonData가 null이 아닌 경우에만 JSONArray 생성
                if (jsonData != null) {
                    try {

                    val jsonArray = JSONArray(jsonData)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        // 키의 존재 여부와 값의 null 여부를 체크
                        val title = jsonObject.optString("title")
                        val sender_id = jsonObject.optString("sender_id")
                        val sender_name = jsonObject.optString("sender_name")
                        val receiver_id = jsonObject.optString("receiver_id")
                        val receiver_name = jsonObject.optString("receiver_name")
                        val content = jsonObject.optString("content")
                        val picture = jsonObject.optString("picture")
                        val hope_purchase = jsonObject.optString("hope_purchase")
                        val send_time = jsonObject.optString("send_time")


                        boardItems.add(
                            BoardItem(
                                title, sender_id, sender_name, receiver_id, receiver_name, content, picture, hope_purchase, send_time
                            )
                        )
                    }

                    this@NoteActivity.runOnUiThread { // activity?. 대신에 this@MainActivity를 사용
                        boardAdapter.clear()
                        // 리사이클러뷰에 데이터 추가하기
                        boardAdapter.addItems(boardItems)
                    }
                } catch (e: JSONException) {
                        e.printStackTrace()
                        // JSON 파싱 오류 처리
                    }
                }
            }
        })
    }

    class BoardAdapter() : RecyclerView.Adapter<BoardAdapter.ViewHolder>() {

        // 데이터를 저장할 리스트 선언
        private val items = mutableListOf<BoardItem>()

        //리사이클러뷰 비우기
        fun clear() {
            val size = items.size
            items.clear()
            notifyItemRangeRemoved(0, size)
        }

        // 데이터를 추가하는 메소드 정의
        fun addItems(newItems: List<BoardItem>) {
            items.addAll(newItems)
            notifyDataSetChanged() // 데이터가 변경되었음을 알림
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            // item_board.xml에서 정의한 뷰들과 매칭되는 변수들 선언
            val sender: TextView = itemView.findViewById(R.id.sender)
            val content: TextView = itemView.findViewById(R.id.content)
            val sent_time: TextView = itemView.findViewById(R.id.sent_time)


            //게시글을 누르면 mainboardAcitivity로 이동
            init {

            }

            override fun onClick(v: View?) {

            }


        }

        //리사이클러뷰 viewholder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // 현재 사용자 ID 가져오기
            val (userId, _) = getUserInfo(holder.itemView.context)

            val item = items[position]
            holder.content.text = item.content
            holder.sent_time.text = item.send_time

            // 현재 사용자의 ID와 sender_id가 일치하면 TextView를 숨김
            if (item.sender_id == userId) {
                holder.sender.visibility = View.GONE
            } else {
                holder.sender.text = item.sender_name
                holder.sender.visibility = View.VISIBLE
            }

        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

    //거래 요청 리사이클러뷰
    val Url = "http://192.168.219.106/note_content.php"

    data class FindItem(
        val work_number: String,
        val sellerID: String,
        val sellerName: String,
        val consumerID: String,
        val consumerName: String,
        val price: String,
        val address: String,
        val invoice_number: String,
    )


    fun getUserInfo(context: Context): Pair<String?, String?> {
        val sharedPreferences = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("ID", null)
        val userName = sharedPreferences.getString("userName", null)
        return Pair(userId, userName)
    }
}