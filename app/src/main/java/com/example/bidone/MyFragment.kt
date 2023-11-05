package com.example.bidone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView

    private lateinit var imageButton: ImageButton
    private lateinit var badgeTextView: TextView

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
        val view = inflater.inflate(R.layout.fragment_my, container, false)

        // 리사이클러뷰 초기화
        val recyclerView = view?.findViewById<RecyclerView>(R.id.myboard_recyclerView)
        val board_adapter = BoardAdapter(emptyList()) // 초기에 빈 리스트로 어댑터 생성
        recyclerView?.adapter = board_adapter
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        // MySQL 데이터 가져오기 및 리사이클러뷰에 표시
        fetchBoardData(board_adapter)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //내가 쓴 게시글
        recyclerView = view.findViewById(R.id.myboard_recyclerView)

        //확인하지 않은 메시지 수 표시
        // 레이아웃에서 이미지 버튼과 뱃지 텍스트 뷰를 초기화합니다.
        imageButton = view.findViewById(R.id.detail_alarm)
        badgeTextView = view.findViewById(R.id.counter)

        // SharedPreferences에서 userID 가져오기
        val userId = getUserId(requireContext()) ?: return

        // 서버에서 미확인 메시지 수를 가져옵니다.
        getUnconfirmedMessagesCount(userId)
    }

    //내가 쓴 게시글 업데이트
    fun fetchBoardData(adapter: BoardAdapter) {

        val phpUrl = "http://192.168.219.106/my_board.php"
        val boardItems = mutableListOf<BoardItem>()
        context?.let { safeContext ->
            val sharedPreferences =
                safeContext.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("ID", null) ?: return

            // MySQL 데이터 가져오기
            // (네트워크 요청 등을 위해 별도의 스레드에서 실행)
            val requestBody = FormBody.Builder()
                .add("userID", userId)
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

                            boardItems.add(
                                BoardItem(
                                    title, simple_explanation, upload_date, userName, worknumber,
                                    detail_explanation, start, increase, date, time, finish, userID
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
                        recyclerView.adapter = BoardAdapter(boardItems)
                    }
                }
            })
        }
    }

    //데이터 클래스
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
        val userID: String
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

            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())

            // 현재 날짜와 비교하기 위해 Date 객체로 파싱
            val finishDate = try {
                dateFormat.parse(item.finish)
            } catch (e: ParseException) {
                e.printStackTrace()
                null
            }

            // finishDate가 현재 시간 이전인지 비교
            if (finishDate != null && finishDate.before(Date())) {
                // 현재 시간 이후이면 배경을 회색으로 변경
                holder.itemView.setBackgroundColor(Color.GRAY)
            } else {
                // 그렇지 않으면 기본 배경색(여기서는 흰색으로 가정)으로 설정
                holder.itemView.setBackgroundColor(Color.WHITE)
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

    //확인하지 않은 메시지 표시

    // 사용자 ID를 가져오는 함수
    private fun getUserId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
        return sharedPreferences.getString("ID", null)
    }

    // 서버에서 미확인 메시지 수를 가져오는 함수
    private fun getUnconfirmedMessagesCount(userId: String) {
        lifecycleScope.launch {
            val count = withContext(Dispatchers.IO) { fetchUnconfirmedMessagesCount(userId) }
            if (isAdded) {
                badgeTextView.text = count.toString()
                // 뱃지를 보여주는 로직을 추가합니다. 만약 count가 0이면 숨길 수 있습니다.
                badgeTextView.visibility = if (count > 0) View.VISIBLE else View.GONE
            }
        }
    }

    // 서버와 통신하여 미확인 메시지 수를 가져오는 함수
    private suspend fun fetchUnconfirmedMessagesCount(userId: String): Int {
        val url = URL("http://192.168.219.106/check_payinfo.php")

        with(url.openConnection() as HttpURLConnection) {
            try {
                // POST 메소드와 필요한 헤더 설정
                requestMethod = "POST"
                doOutput = true // POST 데이터를 OutputStream에 쓸 수 있게 합니다.
                setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                // POST 데이터 전송
                val postData = "userID=$userId".toByteArray(Charsets.UTF_8)
                outputStream.use { it.write(postData) }

                // 서버 응답 받기
                return if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream.bufferedReader().use { reader ->
                        val response = reader.readLine() // JSON 응답을 한 줄로 받는다고 가정합니다.
                        // JSON 응답을 파싱하여 count 값을 추출하고 반환합니다.
                        JSONObject(response).optInt("count", 0)
                    }
                } else {
                    0
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                disconnect()
            }
        }

        return 0
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}