package com.example.bidone

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var fv_recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        //인기 게시글 리사이클러뷰
        // 리사이클러뷰 초기화
        val fv_recyclerView = view?.findViewById<RecyclerView>(R.id.fv_recyclerView)
        val board_adapter = BoardAdapter(emptyList()) // 초기에 빈 리스트로 어댑터 생성
        fv_recyclerView?.adapter = board_adapter
        fv_recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        // MySQL 데이터 가져오기 및 리사이클러뷰에 표시
        fetchBoardData(board_adapter)

        return view

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //인기 게시글 리사이클러뷰
        fv_recyclerView = view.findViewById(R.id.fv_recyclerView)
    }

    //인기 게시글 리사이클러뷰 정의
    fun fetchBoardData(adapter: BoardAdapter) {

        val phpUrl = "http://192.168.219.108/popular_board.php"
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
                    fv_recyclerView.adapter = BoardAdapter(boardItems)
                }
            }
        })
    }


    //인기 게시글 리사이클러뷰
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



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}