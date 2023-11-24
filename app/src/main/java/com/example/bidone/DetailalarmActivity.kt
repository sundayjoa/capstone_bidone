package com.example.bidone

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DetailalarmActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailalarm)

        //뒤로가기 버튼 클릭 이벤트
        val backBtn = findViewById<ImageView>(R.id.bkButton)

        backBtn.setOnClickListener(){
            finish()
        }

        //리사이클러뷰 코드
        recyclerView = findViewById(R.id.alarmrecyclerView)
        val boardAdapter = BoardAdapter(emptyList())
        recyclerView.adapter = boardAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchBoardData(boardAdapter)
    }

    fun fetchBoardData(adapter: BoardAdapter) {

        val phpUrl = "http://192.168.219.106/alarm_pay.php"
        val boardItems = mutableListOf<BoardItem>()
        this.let { safeContext ->
            val sharedPreferences =
                safeContext.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("ID", null) ?: return

            if (userId.isEmpty()) {
                Log.e("SharedPreferences", "UserID is empty.")
                return@let
            }

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
                    Log.d("NoteActivity", "Response received: $jsonData")

                    if (jsonData.isNullOrEmpty()) {
                        Log.e("HTTP_ERROR", "Empty response.")
                        return
                    }

                    try {
                        val jsonArray = JSONArray(jsonData)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val price = jsonObject.getString("price")
                            val consumerName = jsonObject.getString("consumerName")
                            val consumerID = jsonObject.getString("consumerID")
                            val userphone = jsonObject.getString("userphone")
                            val address = jsonObject.getString("address")
                            val title = jsonObject.getString("title")
                            val worknumber = jsonObject.getString("work_number")
                            val courier = jsonObject.getString("courier")
                            val invoice_number = jsonObject.getString("invoice_number")

                            boardItems.add(
                                BoardItem(
                                    title,
                                    price,
                                    consumerName,
                                    worknumber,
                                    consumerID,
                                    userphone,
                                    address,
                                    userId,
                                    courier,
                                    invoice_number
                                )
                            )
                        }

                        // UI 업데이트는 메인 스레드에서 실행
                        runOnUiThread {
                            // 리사이클러뷰에 데이터 표시하기
                            recyclerView.adapter = BoardAdapter(boardItems)
                        }
                    }catch (e: JSONException) {
                        Log.e("JSON_ERROR", "Error parsing JSON", e)
                    }
                }
            })
        }
    }

    data class BoardItem(
        val title: String,
        val price: String,
        val consumerName: String,
        val worknumber: String,
        val consumerID: String,
        val userphone: String,
        val address: String,
        val userID: String,
        val courier: String,
        val invoice_number: String
    )

    class BoardAdapter(private val items: List<BoardItem>) : RecyclerView.Adapter<BoardAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

            val titleTextView: TextView = itemView.findViewById(R.id.title)
            val priceTextView: TextView = itemView.findViewById(R.id.priceText)
            val consumerTextView: TextView = itemView.findViewById(R.id.consumer)
            val textViewAlert: TextView = itemView.findViewById(R.id.alertTextView)

            //클릭 시 다이얼로그
            init {
                itemView.setOnClickListener{
                    showCustomDialog(itemView.context, adapterPosition)
                }
            }

            //다이얼로그
            private fun showCustomDialog(context: Context, position: Int) {

                //userName 가져오기
                val sharedPreferences = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
                val userName = sharedPreferences.getString("userName", "Unknown User")

                //선택한 아이템 데이터 가져오기
                val selectedItem = items[position]

                // 커스텀 다이얼로그를 띄우기
                val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_detaildialog, null)
                val customDialog = Dialog(context)
                customDialog.setContentView(dialogView)

                val worknumber = customDialog.findViewById<TextView>(R.id.worknumber)
                val title = customDialog.findViewById<TextView>(R.id.title)
                val sellername = customDialog.findViewById<TextView>(R.id.seller_name)
                val consumer_name = customDialog.findViewById<TextView>(R.id.consumer_name)
                val address = customDialog.findViewById<TextView>(R.id.address)
                val phone = customDialog.findViewById<TextView>(R.id.phone)
                val price = customDialog.findViewById<TextView>(R.id.price)

                //데이터 설정
                worknumber.text = "작품번호: " + selectedItem.worknumber
                title.text = "게시글 제목: " + selectedItem.title
                sellername.text = "판매자: " + userName
                consumer_name.text = "구매자: " + selectedItem.consumerName
                address.text = "" + selectedItem.address
                phone.text = "구매자 전화번호:" + selectedItem.userphone
                price.text = "낙찰가: " + selectedItem.price

                customDialog.show()

                //취소 버튼 클릭 리스너
                val cancelbtn = customDialog.findViewById<Button>(R.id.cancelbtn)

                cancelbtn.setOnClickListener(){

                    customDialog.dismiss()

                }

                //확인 버튼 클릭 리스너
                val combtn = customDialog.findViewById<Button>(R.id.combtn)

                combtn.setOnClickListener(){
                    val courier = customDialog.findViewById<TextView>(R.id.courier)
                    val invoice_number = customDialog.findViewById<EditText>(R.id.invoice_number)


                    val work_number = selectedItem.worknumber
                    val courierText = courier.text.toString()
                    val invoiceText = invoice_number.text.toString()

                    // 로그 추가
                    Log.d("Debug", "work_number: $work_number")
                    Log.d("Debug", "courier: $courierText")
                    Log.d("Debug", "invoice_number: $invoiceText")


                    //정보 저장
                    if (courierText.isEmpty()) {
                        Toast.makeText(context, "택배사를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    } else if (invoiceText.isEmpty()) {
                        Toast.makeText(context, "송장번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    } else {

                        val formBody = FormBody.Builder()
                            .add("work_number", work_number)
                            .add("courier", courierText)
                            .add("invoice_number", invoiceText)
                            .build()

                        val request = Request.Builder()
                            .url("http://192.168.219.106/payinfo_update.php")
                            .post(formBody)
                            .build()

                        val client = OkHttpClient()
                        client.newCall(request).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                (context as Activity).runOnUiThread {
                                    Toast.makeText(context, "요청이 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onResponse(call: Call, response: Response) {
                                (context as Activity).runOnUiThread {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "요청이 성공했습니다.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "요청에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        })

                        customDialog.dismiss()

                    }

                }

            }
            override fun onClick(v: View?) {

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detailalarm, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            // 데이터를 뷰에 바인딩
            holder.titleTextView.text = items[position].title
            holder.priceTextView.text = items[position].price
            holder.consumerTextView.text = items[position].consumerName

            //운송정보 입력 확인
            if (item.courier.isNullOrBlank() || item.courier == "null" ||
                item.invoice_number.isNullOrBlank() || item.invoice_number == "null") {

                holder.itemView.setBackgroundColor(Color.RED)
                holder.textViewAlert.visibility = View.VISIBLE
            } else{
                holder.textViewAlert.visibility = View.GONE
            }

        }

        override fun getItemCount(): Int {
            return items.size
        }
    }


}