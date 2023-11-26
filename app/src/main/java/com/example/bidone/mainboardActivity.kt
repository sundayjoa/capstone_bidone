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
import okhttp3.Request
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
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.sql.DriverManager
import java.sql.SQLException
import android.util.Base64
import android.view.MotionEvent


class mainboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private var workNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainboard)

        //작품번호 넘겨주기
        workNumber = intent.getStringExtra("worknumber")

        //상세 이미지 리사이클러뷰
        recyclerView = findViewById<RecyclerView>(R.id.dt_imagerecyclerView) // 클래스 멤버로 선언한 변수 사용
        imageAdapter = ImageAdapter() // 빈 어댑터 생성
        recyclerView.adapter = imageAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        fetchImageData()

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

        //경매종료시간 넘겨 받기
        val finishdateView = findViewById<TextView>(R.id.finish_time)
        val finishdate = "종료 시간: " + intent.getStringExtra("finish")

        finishdateView.text = finishdate

        //판매자 ID 넘겨 받기
        val data = intent.getStringExtra("userID")


        //경매 시작 시간 이후 버튼 활성화 시키기
        val ptBtn = findViewById<Button>(R.id.ptBtn)

        // 현재 시간 가져오기
        val currentTime = Calendar.getInstance().time

        // 시간 포맷 지정
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)

        // 경매 시작 시간 파싱
        val auctionStartTime = formatter.parse(intent.getStringExtra("date") + " " + intent.getStringExtra("time"))

        // 경매 종료 시간 파싱
        val auctionEndTime = formatter.parse(intent.getStringExtra("finish"))

        // 현재 시간이 경매 시작 시간 이후이며 종료 시간 이전인지 확인
        if (currentTime.after(auctionStartTime) && currentTime.before(auctionEndTime)) {
            // 버튼 활성화 및 색상 변경
            ptBtn.isEnabled = true
            ptBtn.setBackgroundColor(Color.RED)
        } else {
            // 버튼 비활성화 및 색상 변경
            ptBtn.isEnabled = false
        }

        //즐겨찾기
        val bookmarkBtn = findViewById<ImageView>(R.id.bookmarkButton)

        //즐겨찾기 여부 확인
        checkBookmarkStatus()

        bookmarkBtn.setOnClickListener{
            val work_number = intent.getStringExtra("worknumber").toString()
            val uploader_id = intent.getStringExtra("userID").toString()
            val (userId, _) = getUserInfo(this)

            //DB에 데이터 전송하기
            val bookmark_request = object : StringRequest(
                Method.POST, "http://192.168.219.108/bookmark.php",
                Response.Listener { response ->
                    //서버에서 전송하는 응답 내용 확인
                    Log.d("Response", response)
                    Log.d("JSON Data", response)

                    when (response) {
                        "bookmark_added" -> {
                            bookmarkBtn.setImageResource(R.drawable.ic_star_yellow)
                            updateBookmarkCount(1) // 1을 추가
                        }
                        "bookmark_removed" -> {
                            bookmarkBtn.setImageResource(R.drawable.ic_star)
                            updateBookmarkCount(-1) // 1을 감소
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

                    params["work_number"] = work_number
                    params["uploader_id"] = uploader_id
                    userId?.let {
                        params["consumerID"] = it
                    }

                    return params

                }
            }

            val boookmark_queue = Volley.newRequestQueue(this)
            boookmark_queue.add(bookmark_request)

        }

        //전체 즐겨찾기 수
        fetchBookmarkCount()


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


            val url = "http://192.168.219.108/main_auction.php"
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
                        val (userId, userName) = getUserInfo(this)
                        val increaseinfo = increaseTextView.text.toString()
                        val currentPrice = current.text.toString()
                        val price = (increaseinfo.toInt() + currentPrice.toInt()).toString()
                        val finish = intent.getStringExtra("finish").toString()

                        //DB에 데이터 전송하기
                        val request = object : StringRequest(
                            Method.POST, "http://192.168.219.108/auction.php",
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
                                userId?.let {
                                    params["consumerID"] = it
                                }

                                userName?.let {
                                    params["consumerName"] = it
                                }
                                params["price"] = price
                                params["finish_date"] = finish

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

    //상세 이미지 여러 장 가져오기
    // 네트워크 요청 및 데이터 처리
    fun fetchImageData() {
        val url = "http://192.168.219.108/detail_image.php?worknumber=$workNumber"

        val imageItems = mutableListOf<ImageItem>()
        // OkHttpClient 등을 사용하여 HTTP 요청 실행
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 요청 실패 시 처리할 내용
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {

                val jsonData = response.body?.string()
                Log.d("JSON Response", "Response data: $jsonData")

                // JSON 데이터 파싱
                // jsonData가 null이 아닌 경우에만 JSONArray 생성
                if (jsonData != null) {
                    try {

                        val jsonArray = JSONArray(jsonData)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            // 키의 존재 여부와 값의 null 여부를 체크
                            val base64Images = jsonObject.getString("detail_image").split(",")

                            // 로그를 통한 검증
                            base64Images.forEachIndexed { index, base64 ->
                                Log.d("Base64 Image Data", "Image $index: $base64")
                            }


                            imageItems.add(ImageItem(base64Images))
                        }

                        this@mainboardActivity.runOnUiThread { // activity?. 대신에 this@MainActivity를 사용
                            imageAdapter.clear()
                            // 리사이클러뷰에 데이터 추가하기
                            imageAdapter.addItems(imageItems)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        // JSON 파싱 오류 처리
                        Log.e("Error:", "Error parsing JSON data", e)
                        Log.e("JSON Parsing Error", "Error parsing JSON data: $jsonData", e)
                    }
                }

            }
        })

    }


    data class ImageItem(val imagesBase64: List<String>, var currentIndex: Int = 0)


    class ImageAdapter() : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

        // 데이터를 저장할 리스트 선언
        private val items = mutableListOf<ImageItem>()

        //리사이클러뷰 비우기
        fun clear() {
            val size = items.size
            items.clear()
            notifyItemRangeRemoved(0, size)
        }

        // 데이터를 추가하는 메소드 정의
        fun addItems(newItems: List<ImageItem>) {
            items.addAll(newItems)
            notifyDataSetChanged() // 데이터가 변경되었음을 알림
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            // item_board.xml에서 정의한 뷰들과 매칭되는 변수들 선언
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
            var imageIndex = 0
            var bitmaps: List<Bitmap> = emptyList()


            // 이미지를 표시하는 함수
            fun displayImage() {
                if (imageIndex in bitmaps.indices) {
                    imageView.setImageBitmap(bitmaps[imageIndex])
                    Log.d("ImageAdapter", "Displaying image at index: $imageIndex")
                }
            }

            init {
                imageView.setOnTouchListener(object : View.OnTouchListener {
                    var x1: Float = 0f
                    var x2: Float = 0f
                    val MIN_DISTANCE = 50 // 최소 스와이프 거리

                    override fun onTouch(v: View, event: MotionEvent): Boolean {
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                x1 = event.x
                                return true
                            }
                            MotionEvent.ACTION_UP -> {
                                x2 = event.x
                                val deltaX = x2 - x1

                                if (Math.abs(deltaX) > MIN_DISTANCE) {
                                    // 오른쪽에서 왼쪽으로 스와이프
                                    if (x2 < x1 && imageIndex < bitmaps.size - 1) {
                                        imageIndex++
                                    }
                                    // 왼쪽에서 오른쪽으로 스와이프
                                    else if (x2 > x1 && imageIndex > 0) {
                                        imageIndex--
                                    }
                                    displayImage()
                                    items[adapterPosition].currentIndex = imageIndex
                                }
                                return true
                            }
                        }
                        return false
                    }
                })
            }

            override fun onClick(v: View?) {

            }


        }

        //리사이클러뷰 viewholder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.imageIndex = item.currentIndex

            if (item.imagesBase64.isNotEmpty()) {
                holder.bitmaps = item.imagesBase64.map { base64String ->
                    val decodedByte = Base64.decode(base64String, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
                }

                holder.displayImage()
            }
        }


        override fun getItemCount(): Int {
            return items.size
        }
    }


   fun getUserInfo(context: Context): Pair<String?, String?> {
        val sharedPreferences = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("ID", null)
        val userName = sharedPreferences.getString("userName", null)
        return Pair(userId, userName)
    }

    //즐겨찾기
    //즐겨찾기 여부 확인
    fun checkBookmarkStatus() {

        val bookmarkBtn = findViewById<ImageView>(R.id.bookmarkButton)
        val work_number = intent.getStringExtra("worknumber").toString()
        val (userId, _) = getUserInfo(this)

        val checkBookmarkRequest = object : StringRequest(
            Method.POST, "http://192.168.219.108/checkBookmark.php",
            Response.Listener { response ->
                when (response.trim()) {
                    "exists" -> {
                        bookmarkBtn.setImageResource(R.drawable.ic_star_yellow)
                    }
                    "not_exists" -> {
                        bookmarkBtn.setImageResource(R.drawable.ic_star)
                    }
                }
            },
            Response.ErrorListener { error ->
                Log.d("Error", error.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["work_number"] = work_number
                userId?.let {
                    params["consumerID"] = it
                }

                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(checkBookmarkRequest)
    }

    //bookmark 수 가져오기
    private fun fetchBookmarkCount() {

        val workNumber = intent.getStringExtra("worknumber").toString()

        val count_request = StringRequest(
            com.android.volley.Request.Method.GET, "http://192.168.219.108/count_bookmark.php?work_number=$workNumber",
            Response.Listener { response ->
                val bkmarkView = findViewById<TextView>(R.id.bkmarkView)
                bkmarkView.text = response
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        val count_queue = Volley.newRequestQueue(this)
        count_queue.add(count_request)

    }

    //즐겨찾기 클릭 시 추가 및 삭제
    fun updateBookmarkCount(change: Int) {
        val bkmarkView = findViewById<TextView>(R.id.bkmarkView)
        val currentCount = bkmarkView.text.toString().toIntOrNull() ?: 0
        bkmarkView.text = (currentCount + change).toString()
    }

}