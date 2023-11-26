package com.example.bidone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //회원가입 창 전환
        val membershipbutton = findViewById<Button>(R.id.membershipbutton)

        membershipbutton.setOnClickListener() {
            val intent = Intent(this@LoginActivity, MembershipActivity::class.java)
            startActivity(intent)
        }

        //아이디 찾기 창 전환
        val findIdbutton = findViewById<Button>(R.id.findIdbutton)

        findIdbutton.setOnClickListener() {
            val intent = Intent(this@LoginActivity, Find_Id::class.java)
            startActivity(intent)
        }

        //비밀번호 찾기 창 전환
        val findpasswdbutton = findViewById<Button>(R.id.findpasswdbutton)

        findpasswdbutton.setOnClickListener() {
            val intent = Intent(this@LoginActivity, Find_Pw::class.java)
            startActivity(intent)
        }

        //로그인 버튼 클릭 이벤트
        val loginbutton = findViewById<Button>(R.id.loginbutton)

        loginbutton.setOnClickListener() {
            val idText = findViewById<TextView>(R.id.textId)
            val passwordText = findViewById<TextView>(R.id.textPassword)

            val id = idText.text.toString()
            val password = passwordText.text.toString()

            //DB에 데이터 전송하기
            val request = object : StringRequest(
                //ip 현재 ip 주소로 항상 바꾸기
                Method.POST, "http://192.168.219.108/login.php",
                com.android.volley.Response.Listener { response ->
                    //서버에서 전송하는 응답 내용 확인
                    Log.d("Response", response)
                    Log.d("JSON Data", response)

                    if (response.contains("true") || response.startsWith("true")
                        || response.endsWith("true")
                    ) {

                        val jsonObject = JSONObject(response)
                        val userName = jsonObject.getString("userName")

                        saveUserInfo(this, id, userName)

                        val intent = Intent(this@LoginActivity, NaviActivity::class.java)
                        startActivity(intent)

                    } else {

                        showPopup("id 또는 비밀번호가 잘못되었습니다.")
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
                    params["id"] = id
                    params["password"] = password

                    return params
                }
            }

            val queue = Volley.newRequestQueue(this)
            queue.add(request)
        }
    }

    fun showPopup(message: String) {
        val builder = AlertDialog.Builder(this) // 팝업창 빌더 생성
        builder.setTitle("로그인") // 팝업창 제목 설정
        builder.setMessage(message) // 팝업창 내용 설정
        builder.setPositiveButton("확인") { dialog, which ->
            // 팝업창의 확인 버튼을 누르면 실행할 내용
            dialog.dismiss() // 팝업창 닫기
        }
        builder.show() // 팝업창 띄우기

    }

    fun saveUserInfo(context: Context, userId: String, userName: String) {
        val sharedPreferences = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("ID", userId)
        editor.putString("userName", userName)
        editor.apply()
    }


}