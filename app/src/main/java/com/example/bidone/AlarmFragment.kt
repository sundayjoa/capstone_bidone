package com.example.bidone

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AlarmFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AlarmFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var bk_recyclerView: RecyclerView
    private lateinit var pt_recyclerView: RecyclerView
    private lateinit var sc_recyclerView: RecyclerView

  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_alarm, container, false)

        //북마크 리사이클러뷰
        // 리사이클러뷰 초기화
        val bk_recyclerView = view?.findViewById<RecyclerView>(R.id.bk_recyclerView)
        val board_adapter = BoardAdapter(emptyList()) // 초기에 빈 리스트로 어댑터 생성
        bk_recyclerView?.adapter = board_adapter
        bk_recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        // MySQL 데이터 가져오기 및 리사이클러뷰에 표시
        fetchBoardData(board_adapter)

        //북마크 리사이클러뷰를 위로 올려 새로고침
        val bk_refresh = view.findViewById<SwipeRefreshLayout>(R.id.bk_refresh)

        //북마크 리사이클러뷰 reload 함수
        fun bk_reload(board_adapter: BoardAdapter) {
            //데이터 로드
            fetchBoardData(board_adapter)

        }

        bk_refresh.setOnRefreshListener {
            bk_reload(board_adapter)

            // 작업이 완료되면 아래 코드를 호출하여 새로고침을 종료
            bk_refresh.isRefreshing = false

        }


        //경매 참여 리사이클러뷰
        //리사이클러뷰 초기화
        val pt_recyclerView = view?.findViewById<RecyclerView>(R.id.pt_recyclerView)

        val part_adapter = PartAdapter(emptyList()) // 초기에 빈 리스트로 어댑터 생성
        pt_recyclerView?.adapter = part_adapter
        pt_recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        // MySQL 데이터 가져오기 및 리사이클러뷰에 표시
        fetchPartData(part_adapter)

        //경매 참여 리사이클러뷰를 위로 올려 새로고침
        val pt_refresh = view.findViewById<SwipeRefreshLayout>(R.id.pt_refresh)

        //경매 참여 리사이클러뷰 reload 함수
        fun pt_reload(part_adapter: PartAdapter){
            fetchPartData(part_adapter)
        }

        pt_refresh.setOnRefreshListener {
            pt_reload(part_adapter)

            pt_refresh.isRefreshing = false
        }


        //낙찰 리사이클러뷰
        //리사이클러뷰 초기화
        val sc_recyclerView = view?.findViewById<RecyclerView>(R.id.sc_recyclerView)

        val success_adapter = SuccessAdapter(emptyList())
        sc_recyclerView?.adapter = success_adapter
        sc_recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        // MySQL 데이터 가져오기 및 리사이클러뷰에 표시
        fetchSuccessData(success_adapter)

        //낙찰 리사이클러뷰를 위로 올려 새로고침
        val sc_refresh = view.findViewById<SwipeRefreshLayout>(R.id.sc_refresh)

        //경매 참여 리사이클러뷰 reload 함수
        fun sc_reload(success_adapter: SuccessAdapter){
            fetchSuccessData(success_adapter)
        }

        sc_refresh.setOnRefreshListener {
            sc_reload(success_adapter)

            sc_refresh.isRefreshing = false
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //북마크 리사이클러뷰
        bk_recyclerView = view.findViewById(R.id.bk_recyclerView)
        //경매 참여 리사이클러뷰
        pt_recyclerView = view.findViewById(R.id.pt_recyclerView)
        //낙찰 리사이클러뷰
        sc_recyclerView = view.findViewById(R.id.sc_recyclerView)

    }

    //북마크인리사이클러뷰 정의
    fun fetchBoardData(adapter: BoardAdapter) {

        val phpUrl = "http://192.168.219.108/my_bookmark.php"
        val boardItems = mutableListOf<BoardItem>()
        context?.let { safeContext ->
            val sharedPreferences =
                safeContext.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("ID", null) ?: return

            // MySQL 데이터 가져오기
            // (네트워크 요청 등을 위해 별도의 스레드에서 실행)
            val requestBody = FormBody.Builder()
                .add("consumerID", userId)
                .build()

            val request = Request.Builder()
                .url(phpUrl)
                .post(requestBody)
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // 요청 실패 시 처리할 내용
                }

                override fun onResponse(call: Call, response: Response) {
                    val jsonData = response.body?.string()

                    if (jsonData?.startsWith("{") == true || jsonData?.startsWith("[") == true) {
                        // Valid JSON, proceed with parsing
                        // JSON 데이터 파싱
                        val jsonArray = JSONArray(jsonData)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val title = jsonObject.getString("title")
                            val simple_explanation = jsonObject.getString("simple_explanation")
                            val upload_date = jsonObject.getString("upload_date")
                            val userName = jsonObject.getString("userName")
                            val worknumber = jsonObject.getString("work_number")
                            val detail_explanation = jsonObject.getString("detail_explanation")
                            val start = jsonObject.getString("start")
                            val increase = jsonObject.getString("increase")
                            val date = jsonObject.getString("date")
                            val time = jsonObject.getString("time")
                            val finish = jsonObject.getString("finish_date")
                            val userID = jsonObject.getString("userID")
                            val thumbnail = jsonObject.getString("thumbnail")

                            boardItems.add(
                                BoardItem(
                                    title, simple_explanation, upload_date, userName, worknumber,
                                    detail_explanation, start, increase, date, time, finish, userID, thumbnail
                                )
                            )

                        }
                    } else {
                        // Invalid JSON or error from server
                        Log.e("JSON_ERROR", "Received invalid JSON: $jsonData")
                    }

                    // UI 업데이트는 메인 스레드에서 실행
                    activity?.runOnUiThread {
                        // 리사이클러뷰에 데이터 표시하기
                        bk_recyclerView.adapter = BoardAdapter(boardItems)
                    }
                }
            })
        }
    }


    //북마크 리사이클러뷰
    data class BoardItem(
        val title: String,
        val simple_explanation: String,
        val uploadData: String,
        val userName: String,
        val worknumber: String,
        val detailExplanation: String,
        val start: String,
        val increase: String,
        val date: String,
        val time: String,
        val finish: String,
        val userID: String,
        val thumbnail: String
    )

    class BoardAdapter(private val items: List<BoardItem>) : RecyclerView.Adapter<BoardAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            // item_board.xml에서 정의한 뷰들과 매칭되는 변수들 선언
            val titleTextView: TextView = itemView.findViewById(R.id.title)
            val simple_explanationTextView: TextView =
                itemView.findViewById(R.id.simple_explanation)
            val uploadDataTextView: TextView = itemView.findViewById(R.id.time)
            val userNameTextView: TextView = itemView.findViewById(R.id.userName)
            val worknumberTextView: TextView = itemView.findViewById(R.id.worknumber)
            val thumbnailImageView: ImageView = itemView.findViewById(R.id.thumbnail)

            //게시글을 누르면 mainboardAcitivity로 이동
            init {
                itemView.setOnClickListener(this)
            }
            override fun onClick(v: View?) {
                val position = adapterPosition
                val item = items[position]

                val intent = Intent(itemView.context, mainboardActivity::class.java)
                //작품 정보 넘겨주기
                intent.putExtra("worknumber", item.worknumber)
                intent.putExtra("title", item.title)
                intent.putExtra("userName", item.userName)
                intent.putExtra("detail_explanation", item.detailExplanation)
                intent.putExtra("start", item.start)
                intent.putExtra("increase", item.increase)
                intent.putExtra("date", item.date)
                intent.putExtra("time", item.time)
                intent.putExtra("finish", item.finish)
                intent.putExtra("userID", item.userID)

                itemView.context.startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_board, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            // 데이터를 뷰에 바인딩
            holder.titleTextView.text = items[position].title
            holder.simple_explanationTextView.text = items[position].simple_explanation
            holder.uploadDataTextView.text = items[position].uploadData
            holder.userNameTextView.text = items[position].userName
            holder.worknumberTextView.text = items[position].worknumber

            //썸네일
            val imageBase64 = items[position].thumbnail
            val decodedByte = Base64.decode(imageBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
            holder.thumbnailImageView.setImageBitmap(bitmap)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())

            // 경매 참가 시간이 지났으면 회색으로 변환
            val finishDate = try {
                dateFormat.parse(item.finish)
            } catch (e: ParseException) {
                e.printStackTrace()
                null
            }


            if (finishDate != null && finishDate.before(Date())) {
                holder.itemView.setBackgroundColor(Color.GRAY)
            } else {
                holder.itemView.setBackgroundColor(Color.WHITE)
            }

        }

        override fun getItemCount(): Int {
            return items.size
        }
    }


    //경매 참가 리사이클러뷰
    fun fetchPartData(adapter: PartAdapter) {

        val phpUrl = "http://192.168.219.108/part_board.php"
        val boardItems = mutableListOf<BoardItem>()
        context?.let { safeContext ->
            val sharedPreferences =
                safeContext.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("ID", null) ?: return

            // MySQL 데이터 가져오기
            // (네트워크 요청 등을 위해 별도의 스레드에서 실행)
            val requestBody = FormBody.Builder()
                .add("consumerID", userId)
                .build()

            val request = Request.Builder()
                .url(phpUrl)
                .post(requestBody)
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // 요청 실패 시 처리할 내용
                }

                override fun onResponse(call: Call, response: Response) {
                    val jsonData = response.body?.string()

                    if (jsonData?.startsWith("{") == true || jsonData?.startsWith("[") == true) {
                        // Valid JSON, proceed with parsing
                        // JSON 데이터 파싱
                        val jsonArray = JSONArray(jsonData)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val title = jsonObject.getString("title")
                            val simple_explanation = jsonObject.getString("simple_explanation")
                            val upload_date = jsonObject.getString("upload_date")
                            val userName = jsonObject.getString("userName")
                            val worknumber = jsonObject.getString("work_number")
                            val detail_explanation = jsonObject.getString("detail_explanation")
                            val start = jsonObject.getString("start")
                            val increase = jsonObject.getString("increase")
                            val date = jsonObject.getString("date")
                            val time = jsonObject.getString("time")
                            val finish = jsonObject.getString("finish_date")
                            val userID = jsonObject.getString("userID")
                            val thumbnail = jsonObject.getString("thumbnail")

                            boardItems.add(
                                BoardItem(
                                    title, simple_explanation, upload_date, userName, worknumber,
                                    detail_explanation, start, increase, date, time, finish, userID, thumbnail
                                )
                            )

                        }
                    } else {
                        // Invalid JSON or error from server
                        Log.e("JSON_ERROR", "Received invalid JSON: $jsonData")
                    }

                    // UI 업데이트는 메인 스레드에서 실행
                    activity?.runOnUiThread {
                        // 리사이클러뷰에 데이터 표시하기
                        pt_recyclerView.adapter = PartAdapter(boardItems)
                    }
                }
            })
        }
    }

    class PartAdapter(private val items: List<BoardItem>) : RecyclerView.Adapter<PartAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            // item_board.xml에서 정의한 뷰들과 매칭되는 변수들 선언
            val titleTextView: TextView = itemView.findViewById(R.id.title)
            val simple_explanationTextView: TextView =
                itemView.findViewById(R.id.simple_explanation)
            val uploadDataTextView: TextView = itemView.findViewById(R.id.time)
            val userNameTextView: TextView = itemView.findViewById(R.id.userName)
            val worknumberTextView: TextView = itemView.findViewById(R.id.worknumber)
            val thumbnailImageView: ImageView = itemView.findViewById(R.id.thumbnail)

            //게시글을 누르면 mainboardAcitivity로 이동
            init {
                itemView.setOnClickListener(this)
            }
            override fun onClick(v: View?) {
                val position = adapterPosition
                val item = items[position]

                val intent = Intent(itemView.context, mainboardActivity::class.java)
                //작품 정보 넘겨주기
                intent.putExtra("worknumber", item.worknumber)
                intent.putExtra("title", item.title)
                intent.putExtra("userName", item.userName)
                intent.putExtra("detail_explanation", item.detailExplanation)
                intent.putExtra("start", item.start)
                intent.putExtra("increase", item.increase)
                intent.putExtra("date", item.date)
                intent.putExtra("time", item.time)
                intent.putExtra("finish", item.finish)
                intent.putExtra("userID", item.userID)

                itemView.context.startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_board, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // 데이터를 뷰에 바인딩
            holder.titleTextView.text = items[position].title
            holder.simple_explanationTextView.text = items[position].simple_explanation
            holder.uploadDataTextView.text = items[position].uploadData
            holder.userNameTextView.text = items[position].userName
            holder.worknumberTextView.text = items[position].worknumber

            //썸네일
            val imageBase64 = items[position].thumbnail
            val decodedByte = Base64.decode(imageBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
            holder.thumbnailImageView.setImageBitmap(bitmap)
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }
    
    
    //낙찰 리사이클러뷰
    data class SuccessItem(
        val title: String,
        val simple_explanation: String,
        val uploadData: String,
        val userName: String,
        val worknumber: String,
        val userID: String,
        val price: String
        )

    fun fetchSuccessData(adapter: SuccessAdapter) {

        val phpUrl = "http://192.168.219.108/success_auction.php"
        val successItems = mutableListOf<SuccessItem>()
        context?.let { safeContext ->
            val sharedPreferences =
                safeContext.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("ID", null) ?: return

            // MySQL 데이터 가져오기
            // (네트워크 요청 등을 위해 별도의 스레드에서 실행)
            val requestBody = FormBody.Builder()
                .add("consumerID", userId)
                .build()

            val request = Request.Builder()
                .url(phpUrl)
                .post(requestBody)
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // 요청 실패 시 처리할 내용
                }

                override fun onResponse(call: Call, response: Response) {
                    val jsonData = response.body?.string()

                    if (jsonData?.startsWith("{") == true || jsonData?.startsWith("[") == true) {
                        // Valid JSON, proceed with parsing
                        // JSON 데이터 파싱
                        val jsonArray = JSONArray(jsonData)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val title = jsonObject.getString("title")
                            val simple_explanation = jsonObject.getString("simple_explanation")
                            val upload_date = jsonObject.getString("upload_date")
                            val userName = jsonObject.getString("userName")
                            val worknumber = jsonObject.getString("work_number")
                            val userID = jsonObject.getString("userID")
                            val price = jsonObject.getString("price")

                            successItems.add(
                                SuccessItem(
                                    title, simple_explanation, upload_date, userName, worknumber,
                                    userID, price
                                )
                            )

                        }
                    } else {
                        // Invalid JSON or error from server
                        Log.e("JSON_ERROR", "Received invalid JSON: $jsonData")
                    }

                    // UI 업데이트는 메인 스레드에서 실행
                    activity?.runOnUiThread {
                        // 리사이클러뷰에 데이터 표시하기
                        sc_recyclerView.adapter = SuccessAdapter(successItems)
                    }
                }
            })
        }
    }

    class SuccessAdapter(private val items: List<SuccessItem>) : RecyclerView.Adapter<SuccessAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // item_board.xml에서 정의한 뷰들과 매칭되는 변수들 선언
            val titleTextView: TextView = itemView.findViewById(R.id.title)
            val simple_explanationTextView: TextView =
                itemView.findViewById(R.id.simple_explanation)
            val uploadDataTextView: TextView = itemView.findViewById(R.id.time)
            val userNameTextView: TextView = itemView.findViewById(R.id.userName)
            val worknumberTextView: TextView = itemView.findViewById(R.id.worknumber)

            val textViewAlert: TextView = itemView.findViewById(R.id.alertTextView)



            init {
                itemView.setOnClickListener{
                    showCustomDialog(itemView.context, adapterPosition)
                }
            }

            //낙찰 다이얼로그
            private fun showCustomDialog(context: Context, position: Int) {
                //선택한 아이템 데이터 가져오기
                val selectedItem = items[position]
                //userName 가져오기
                val sharedPreferences = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
                val userName = sharedPreferences.getString("userName", "Unknown User")

                // 커스텀 다이얼로그를 띄우기
                val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_successdialog, null)
                val customDialog = Dialog(context)
                customDialog.setContentView(dialogView)

                val worknumber = customDialog.findViewById<TextView>(R.id.worknumber)
                val title = customDialog.findViewById<TextView>(R.id.title)
                val sellername = customDialog.findViewById<TextView>(R.id.seller_name)
                val name = customDialog.findViewById<TextView>(R.id.name)
                val price = customDialog.findViewById<TextView>(R.id.price)

                //데이터 설정
                worknumber.text = "작품번호: " + selectedItem.worknumber
                title.text = "게시글 제목: " + selectedItem.title
                sellername.text = "판매자: " + selectedItem.userName
                name.text = "구매자: " + userName
                price.text = "낙찰가: " + selectedItem.price


                customDialog.show()

                //확인 버튼 클릭 리스너
                val comBtn = customDialog.findViewById<Button>(R.id.combtn)

                comBtn.setOnClickListener(){
                    val address = customDialog.findViewById<TextView>(R.id.address)
                    val detail_address = customDialog.findViewById<TextView>(R.id.detail_address)

                    val addressText = address.text.toString()
                    val detail_addressText = detail_address.text.toString()

                    val comaddress = addressText + " " + detail_addressText

                    val info_sharedPreferences = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
                    val savedUserId = info_sharedPreferences.getString("ID", null) ?: "" // null일 경우 빈 문자열 반환
                    val savedUserName = info_sharedPreferences.getString("userName", null) ?: ""

                    val worknumberText = selectedItem.worknumber
                    val sellerIDText = selectedItem.userID
                    val sellernameText = selectedItem.userName
                    val price = selectedItem.price
                    val comaddressText = comaddress

                    val formBody = FormBody.Builder()
                        .add("worknumber", worknumberText)
                        .add("sellerID", sellerIDText)
                        .add("sellerName", sellernameText)
                        .add("consumerID", savedUserId)
                        .add("consumerName", savedUserName)
                        .add("price", price)
                        .add("comaddress", comaddressText)
                        .build()

                    val request = Request.Builder()
                        .url("http://192.168.219.108/payinfo.php")
                        .post(formBody)
                        .build()

                    val client = OkHttpClient()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            // Handle the error
                        }

                        override fun onResponse(call: Call, response: Response) {
                            // Handle the response
                        }
                    })

                    customDialog.dismiss()

                }

            }

        }

        // 사용자가 해당 정보를 입력했는지 확인
        private fun checkWorkNumberAndSetBackground(worknumber: String, holder: ViewHolder) {
            val formBody = FormBody.Builder()
                .add("work_number", worknumber)
                .build()

            val request = Request.Builder()
                .url("http://192.168.219.108/check_pay.php")
                .post(formBody)
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // 오류 처리
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        // 서버로부터 응답을 받음
                        val responseBody = response.body?.string()
                        // 서버 응답을 JSON 객체로 변환
                        val jsonObject = JSONObject(responseBody)
                        val exists = jsonObject.getBoolean("exists")

                        // UI 스레드에서 뷰의 배경색을 변경
                        (holder.itemView.context as Activity).runOnUiThread {
                            if (!exists) {
                                // work_number가 테이블에 없으면 배경색을 붉은색으로 설정
                                holder.itemView.setBackgroundColor(Color.RED)
                                holder.textViewAlert.visibility = View.VISIBLE

                            } else {
                                // 존재한다면 원래의 배경색으로 설정
                                holder.itemView.setBackgroundColor(Color.WHITE) // 원하는 색상으로 변경
                            }
                        }
                    } else {
                        // 서버 응답에 문제가 있을 경우 처리
                    }
                }
            })
        }


      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_board, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]

            // 데이터를 뷰에 바인딩
            holder.titleTextView.text = items[position].title
            holder.simple_explanationTextView.text = items[position].simple_explanation
            holder.uploadDataTextView.text = items[position].uploadData
            holder.userNameTextView.text = items[position].userName
            holder.worknumberTextView.text = items[position].worknumber

            //사용자 입력 확인
            checkWorkNumberAndSetBackground(item.worknumber, holder)
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AlarmkFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AlarmFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}