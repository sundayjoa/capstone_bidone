package com.example.bidone

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class Find_Id : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_id)
        // name과 birth와 phonenumber를 onCreate 메소드 밖에서 선언
        val name = findViewById<TextView>(R.id.name)
        val birth = findViewById<TextView>(R.id.birth)
        val phonenumber = findViewById<TextView>(R.id.phonenumber)


        //아이디 찾기
        val findBtn = findViewById<Button>(R.id.findid_Btn)


        findBtn.setOnClickListener {
            val nameText = name.text.toString()
            val birthText = birth.text.toString()

            //DB에 데이터 전송하기
            val request = object : StringRequest(
                //ip 현재 ip 주소로 항상 바꾸기
                Method.POST, "http://192.168.219.106/find_id.php",
                com.android.volley.Response.Listener { response ->
                    //서버에서 전송하는 응답 내용 확인
                    Log.d("Response", response)
                    Log.d("JSON Data", response)

                    val message = response

                    if (response.contains("false") || response.startsWith("false")
                        || response.endsWith("false")) {

                        showPopup("존재하지 않는 id 입니다.")

                    } else {

                        showPopup("당신의 id는 $message 입니다.")
                    }

                },
                com.android.volley.Response.ErrorListener { error ->
                    Toast.makeText(
                        this,
                        "An error occurred: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                }) {
                // 요청에 id 값을 전달합니다.
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["name"] = nameText
                    params["birth"] = birthText

                    return params
                }
            }
            val queue = Volley.newRequestQueue(this)
            queue.add(request)
        }
    }

    // 팝업창을 띄우는 함수
    fun showPopup(message: String) {
        val builder = AlertDialog.Builder(this) // 팝업창 빌더 생성
        builder.setTitle("아이디 찾기") // 팝업창 제목 설정
        builder.setMessage(message) // 팝업창 내용 설정
        builder.setPositiveButton("확인") { dialog, which ->
            // 팝업창의 확인 버튼을 누르면 실행할 내용
            dialog.dismiss() // 팝업창 닫기
        }
        builder.show() // 팝업창 띄우기

    }
}