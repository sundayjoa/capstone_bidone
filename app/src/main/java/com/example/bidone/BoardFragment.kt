package com.example.bidone

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.util.Base64
import android.util.Log
import android.view.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BoardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class BoardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView

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
        val view = inflater.inflate(R.layout.fragment_board, container, false)

        //스피너 관련 명령어
        val spinner: Spinner = view.findViewById(R.id.spinner)
        // 스피너에 들어갈 항목
        val items = arrayOf("전체", "수공예품", "중고물품", "의뢰게시판")
        // 어댑터 생성
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        // 스피너에 어댑터 설정
        spinner.adapter = adapter

        //스피너에서 외뢰 게시판을 클릭했을 때 해당 게시판으로 이동
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                if (selectedItem == "의뢰게시판") {
                    val intent = Intent(activity, RequestboardActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무 항목도 선택되지 않았을 때 실행될 코드
            }
        }

    //리사이클러뷰 관련 코드

    // 리사이클러뷰 초기화
    val recyclerView = view.findViewById<RecyclerView>(R.id.boardrecyclerView)
    val board_adapter = BoardAdapter(emptyList()) // 초기에 빈 리스트로 어댑터 생성
    recyclerView.adapter = board_adapter
    recyclerView.layoutManager = LinearLayoutManager(requireContext())

    // MySQL 데이터 가져오기 및 리사이클러뷰에 표시
    fetchDataFromMySQL(board_adapter)

    //리사이클러뷰를 위로 올려 새로고침

    val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

    //리사이클러뷰 reload 함수
    fun reload(board_adapter: BoardAdapter) {
        //데이터 로드
        fetchDataFromMySQL(board_adapter)

    }

    swipeRefreshLayout.setOnRefreshListener {
        reload(board_adapter)

        // 작업이 완료되면 아래 코드를 호출하여 새로고침을 종료
        swipeRefreshLayout.isRefreshing = false

    }

    return view
    }

    // 1. MySQL 데이터를 가져오기 위한 PHP 파일의 URL
    val phpUrl = "http://192.168.219.108/auctionboard.php"

    // 2. 데이터를 저장할 모델 클래스 정의
    data class BoardItem(
        val title: String,
        val simple_explanation: String,
        val uploadData: String,
        val userName: String,
        val thumbnail: String,
        val worknumber: String,
        val detailExplanation: String,
        val start: String,
        val increase: String,
        val date: String,
        val time: String,
        val finish: String,
        val userID: String

    )


    //리사이클러뷰에 Mysql 연동
    private fun fetchDataFromMySQL(adapter: BoardAdapter) {

        // 3. 데이터를 가져와서 리사이클러뷰에 표시하는 함수
            val boardItems = mutableListOf<BoardItem>()

            // MySQL 데이터 가져오기
            // (네트워크 요청 등을 위해 별도의 스레드에서 실행)
            val request = Request.Builder().url(phpUrl).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // 요청 실패 시 처리할 내용
                }

                override fun onResponse(call: Call, response: Response) {
                    val jsonData = response.body?.string()

                    // JSON 데이터 파싱
                    val jsonArray = JSONArray(jsonData)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val title = jsonObject.getString("title")
                        val simple_explanation = jsonObject.getString("simple_explanation")
                        val upload_date = jsonObject.getString("upload_date")
                        val userName = jsonObject.getString("userName")
                        val thumbnail = jsonObject.getString("thumbnail")
                        val worknumber = jsonObject.getString("work_number")
                        val detail_explanation = jsonObject.getString("detail_explanation")
                        val start = jsonObject.getString("start")
                        val increase = jsonObject.getString("increase")
                        val date = jsonObject.getString("date")
                        val time = jsonObject.getString("time")
                        val finish = jsonObject.getString("finish_date")
                        val userID = jsonObject.getString("userID")

                        boardItems.add(BoardItem(title, simple_explanation, upload_date, userName, thumbnail, worknumber,
                            detail_explanation, start, increase, date, time, finish, userID))
                    }

                    // UI 업데이트는 메인 스레드에서 실행
                    activity?.runOnUiThread {
                        // 리사이클러뷰에 데이터 표시하기
                        recyclerView.adapter = BoardAdapter(boardItems)
                    }
                }
            })
    }



    // 4. 리사이클러뷰 어댑터 클래스 정의
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



    //리사이클러뷰 viewholder
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



        //WritingboardActivity 화면 전환 리스너
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            recyclerView = view.findViewById(R.id.boardrecyclerView)

        // 작성 버튼 코드 작성
        val writingButton: Button = view.findViewById(R.id.writingButton)
        writingButton.setOnClickListener {
            activity?.let {
                val intent = Intent(it, WritingboardActivity::class.java)
                it.startActivity(intent)
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BoardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BoardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}