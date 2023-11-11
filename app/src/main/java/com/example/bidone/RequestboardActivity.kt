package com.example.bidone

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bidone.databinding.ActivityRequestboardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException



class RequestboardActivity : AppCompatActivity() {

    // 바인딩 선언
    // 클래스 멤버로 선언
    private  lateinit var binding : ActivityRequestboardBinding

    // 리사이클러뷰 선언
    // 클래스 멤버로 선언
    private lateinit var recyclerView: RecyclerView

    // 리사이클러뷰 어댑터 선언
    // 클래스 멤버로 선언
    private lateinit var boardAdapter: BoardAdapter

    //쪽지함 리사이클러뷰 선언
    private lateinit var note_recyclerView: RecyclerView
    //쪽지함 어댑터 선언
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_requestboard) // setContentView() 메소드를 가장 먼저 호출

        // 데이터 바인딩
        binding = DataBindingUtil.setContentView(this, R.layout.activity_requestboard)

        // 바인딩 이동  의뢰게시판 -> 글쓰기
        binding.rwriteButton.setOnClickListener {
            val intent = Intent(this, RequestwritingActivity::class.java)
            startActivity(intent)
        }

        //스피너 항목 설정
        val Spinner = findViewById<Spinner>(R.id.spinner)

        val items = arrayOf("의뢰게시판", "전체", "수공예품", "중고물품")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        Spinner.adapter = adapter

        //스피너에서 전체를 클릭했을 때 메인 게시판 화면 전환
        Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val item = parent.getItemAtPosition(position).toString()
                if (item == "전체") {
                    val intent = Intent(this@RequestboardActivity, NaviActivity::class.java)
                    intent.putExtra("showBoardFragment", true)

                    startActivity(intent)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        //슬라이드 화면
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val rchatButton = findViewById<ImageView>(R.id.rchatButton)

        rchatButton.setOnClickListener{
            if(drawerLayout.isDrawerOpen(GravityCompat.END)){
                drawerLayout.closeDrawer(GravityCompat.END)
            } else{
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }

        //리사이클러뷰 관련 코드

        // 리사이클러뷰 초기화
        recyclerView = findViewById<RecyclerView>(R.id.requestrecyclerView) // 클래스 멤버로 선언한 변수 사용
        boardAdapter = BoardAdapter() // 빈 어댑터 생성
        recyclerView.adapter = boardAdapter
        recyclerView.layoutManager = LinearLayoutManager(this) // requireContext()를 this로 변경

        // MySQL 데이터 가져오기 및 리사이클러뷰에 표시
        fetchDataFromMySQL()

        //리사이클러뷰를 위로 올려 새로고침

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout) // setContentView() 메소드 다음으로 옮김


        //리사이클러뷰 reload 함수
        fun reload() {

            //데이터 로드
            fetchDataFromMySQL()

            // 작업이 완료되면 아래 코드를 호출하여 새로고침을 종료
            swipeRefreshLayout.isRefreshing = false

        }

        swipeRefreshLayout.setOnRefreshListener {
            reload()
        }

        //쪽지함 리사이클러뷰
        note_recyclerView = findViewById<RecyclerView>(R.id.noterecyclerView)
        noteAdapter = NoteAdapter()
        note_recyclerView.adapter = noteAdapter
        note_recyclerView.layoutManager = LinearLayoutManager(this)

        fetchNoteFromMySQL()



    }

    // 데이터를 저장할 모델 클래스 정의
    data class BoardItem(
        val title: String,
        val content: String,
        val uploadData: String,
        val userName: String,
        val request_number: String,
        val userID: String,
        val hope_purchase: String
    )


    //리사이클러뷰 설정
    // 1. MySQL 데이터를 가져오기 위한 PHP 파일의 URL
    val phpUrl = "http://192.168.219.108/requestboard.php"

    //리사이클러뷰에 Mysql 연동
    private fun fetchDataFromMySQL() {

        // 3. 데이터를 가져와서 리사이클러뷰에 표시하는 함수


        // MySQL 데이터 가져오기
        // (네트워크 요청 등을 위해 별도의 스레드에서 실행)
        val boardItems = mutableListOf<BoardItem>()
        val request = Request.Builder().url(phpUrl).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 요청 실패 시 처리할 내용
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData = response.body?.string()

                // JSON 데이터 파싱
                // jsonData가 null이 아닌 경우에만 JSONArray 생성
                if (jsonData != null) {
                    val jsonArray = JSONArray(jsonData)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        // 키의 존재 여부와 값의 null 여부를 체크
                        val title = jsonObject.optString("title")
                        val content = jsonObject.optString("content")
                        val uploadData = jsonObject.optString("upload_date")
                        val userName = jsonObject.optString("userName")
                        val request_number = jsonObject.optString("request_number")
                        val userID = jsonObject.optString("userID")
                        val hope_purchase = jsonObject.optString("hope_purchase")

                        boardItems.add(
                            BoardItem(
                                title, content, uploadData, userName, request_number, userID,
                                hope_purchase
                            )
                        )
                    }

                    this@RequestboardActivity.runOnUiThread { // activity?. 대신에 this@MainActivity를 사용
                        boardAdapter.clear()
                        // 리사이클러뷰에 데이터 추가하기
                        boardAdapter.addItems(boardItems)
                    }
                }
            }
        })
    }

    // 4. 리사이클러뷰 어댑터 클래스 정의
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
            val titleTextView: TextView = itemView.findViewById(R.id.title)
            val contentTextView: TextView =
                itemView.findViewById(R.id.content)
            val uploadDataTextView: TextView = itemView.findViewById(R.id.time)
            val userNameTextView: TextView = itemView.findViewById(R.id.userName)
            val request_numberTextView: TextView = itemView.findViewById(R.id.request_number)

            //게시글을 누르면 mainboardAcitivity로 이동
            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                val position = adapterPosition
                val item = items[position]

                val intent = Intent(itemView.context, mainrequestActivity::class.java)
                //작품 정보 넘겨주기
                intent.putExtra("request_number", item.request_number)
                intent.putExtra("title", item.title)
                intent.putExtra("content", item.content)
                intent.putExtra("userName", item.userName)
                intent.putExtra("upload_date", item.uploadData)
                intent.putExtra("hope_purchase", item.hope_purchase)
                intent.putExtra("userID", item.userID)

                itemView.context.startActivity(intent)
            }

        }

        //리사이클러뷰 viewholder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // 데이터를 뷰에 바인딩
            holder.titleTextView.text = items[position].title
            holder.contentTextView.text = items[position].content
            holder.uploadDataTextView.text = items[position].uploadData
            holder.userNameTextView.text = items[position].userName
            holder.request_numberTextView.text = items[position].request_number
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

    //쪽지함 모델 클래스 정의
    data class NoteItem(
        val request_number: String,
        val title: String,
        val sender_id: String,
        val sender_name: String,
        val receiver_id: String,
        val receiver_name: String,
        val content: String,
        val hope_purchase: String,
        val send_time: String,
        val unreadCount: Int
    )



    private fun fetchNoteFromMySQL(){
        val sharedPreferences = this.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("ID", "")

        val noteItems = mutableListOf<NoteItem>()
        val note_phpUrl = "http://192.168.219.108/noteinfo.php?userId=$userId"

        val request = Request.Builder().url(note_phpUrl).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 요청 실패 시 처리할 내용
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData = response.body?.string()

                if (jsonData != null){
                    try {
                        val jsonArray = JSONArray(jsonData)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)


                            val request_number = jsonObject.optString("request_number")
                            val title = jsonObject.optString("title")
                            val sender_id = jsonObject.optString("sender_id")
                            val sender_name = jsonObject.optString("sender_name")
                            val receiver_id = jsonObject.optString("receiver_id")
                            val receiver_name = jsonObject.optString("receiver_name")
                            val content = jsonObject.optString("content")
                            val hope_purchase = jsonObject.optString("hope_purchase")
                            val send_time = jsonObject.optString("send_time")
                            val unreadCount = jsonObject.optInt("unreadCount")

                            noteItems.add(
                                NoteItem(
                                    request_number,
                                    title,
                                    sender_id,
                                    sender_name,
                                    receiver_id,
                                    receiver_name,
                                    content,
                                    hope_purchase,
                                    send_time,
                                    unreadCount
                                )
                            )
                        }
                        this@RequestboardActivity.runOnUiThread {
                            noteAdapter.clear()
                            noteAdapter.addItems(noteItems)

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        // JSON 파싱 오류 처리
                    }
                }
            }
        })
    }

    class NoteAdapter() : RecyclerView.Adapter<NoteAdapter.ViewHolder>(){
        private val note_items = mutableListOf<NoteItem>()

        //리사이클러뷰 비우기
        fun clear() {
            val size = note_items.size
            note_items.clear()
            notifyItemRangeRemoved(0, size)
        }

        // 데이터를 추가하는 메소드 정의
        fun addItems(newItems: List<NoteItem>) {
            note_items.addAll(newItems)
            notifyDataSetChanged() // 데이터가 변경되었음을 알림
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

            val sender: TextView = itemView.findViewById(R.id.sender)
            val sent_time: TextView = itemView.findViewById(R.id.sent_time)
            val content: TextView = itemView.findViewById(R.id.content)

            //게시글을 누르면 mainboardAcitivity로 이동
            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                val position = adapterPosition
                val item = note_items[position]

                val intent = Intent(itemView.context, NoteActivity::class.java)

                //정보 넘겨주기
                intent.putExtra("request_number", item.request_number)
                intent.putExtra("title", item.title)
                intent.putExtra("sender_id", item.sender_id)
                intent.putExtra("sender_name", item.sender_name)
                intent.putExtra("receiver_id", item.receiver_id)
                intent.putExtra("request_name", item.receiver_name)
                intent.putExtra("content", item.content)
                intent.putExtra("hope_purchase", item.hope_purchase)
                intent.putExtra("send_time", item.send_time)

                itemView.context.startActivity(intent)
            }
        }

        //리사이클러뷰 viewholder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_notebox, parent,false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int){
            holder.sender.text = note_items[position].sender_name
            holder.sent_time.text = note_items[position].send_time
            holder.content.text = note_items[position].content

            // 해당 아이템의 읽지 않은 메시지 수
            val unreadCount = note_items[position].unreadCount

            // 각 아이템 내의 counter TextView 찾기
            val counterTextView = holder.itemView.findViewById<TextView>(R.id.counter)
            if (unreadCount > 0) {
                counterTextView.text = unreadCount.toString()
                counterTextView.visibility = View.VISIBLE
            } else {
                counterTextView.visibility = View.INVISIBLE
            }

        }

        override fun getItemCount(): Int {
            return note_items.size
        }

    }


}
