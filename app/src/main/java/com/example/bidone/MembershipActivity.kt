package com.example.bidone

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.Response
import org.json.JSONObject

class MembershipActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership)


        //id 중복 확인
        val checkbutton = findViewById<Button>(R.id.checkbutton)
        val idText = findViewById<EditText>(R.id.idText)
        val checkText = findViewById<TextView>(R.id.checkText)

        checkbutton.setOnClickListener {

            val id = idText.text.toString()

            // 텍스트가 비어있지 않은지 확인
            if (id.isNotEmpty()) {
                //DB에 데이터 전송하기
                val request = object : StringRequest(
                    //ip 현재 ip 주소로 항상 바꾸기
                    Method.POST, "http://192.168.219.106/check_id.php",
                    com.android.volley.Response.Listener { response ->
                        //서버에서 전송하는 응답 내용 확인
                        Log.d("Response", response)
                        Log.d("JSON Data", response)

                        if (response.contains("exist") || response.startsWith("exist")
                            || response.endsWith("exist")) {
                            Toast.makeText(
                                this,
                                "이미 존재하는 id 입니다.",
                                Toast.LENGTH_SHORT
                            ).show()

                            checkText.setTextColor(Color.parseColor("#969696"))

                        } else if (response.contains("true") || response.startsWith("true")
                            || response.endsWith("true")) {
                            Toast.makeText(
                                this,
                                "사용할 수 있는 id 입니다.",
                                Toast.LENGTH_SHORT
                            ).show()

                            checkText.setTextColor(Color.BLUE)
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
                        return params
                    }
                }
                val queue = Volley.newRequestQueue(this)
                queue.add(request)

            } else {
                // 텍스트가 비어있다면 입력하라는 메시지를 출력합니다.
                Toast.makeText(this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show()
            }

        }

        val passwdText = findViewById<EditText>(R.id.passwdText)
        val passwdcheckText = findViewById<EditText>(R.id.passwdcheckText)
        val pwcheckText = findViewById<TextView>(R.id.pwcheckText)

        //비밀번호 중복 확인
        val pwcheckBtn = findViewById<Button>(R.id.pwcheckBtn)
        pwcheckBtn.setOnClickListener{

            val password = passwdText.text.toString()
            val confirmPassword = passwdcheckText.text.toString()

            if (password == confirmPassword) {

                Toast.makeText(this, "중복 확인 완료", Toast.LENGTH_SHORT).show()
                pwcheckText.setTextColor(Color.BLUE)

            } else {
                Toast.makeText(this, "입력하신 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }

        }


        // 팝업창 취소 버튼 클릭 이벤트
        val membershipBtn = findViewById<Button>(R.id.membershipBtn)

        membershipBtn.setOnClickListener {

            //textView에 있는 정보 String으로 바꾸기
            val name = findViewById<TextView>(R.id.nameText)
            val id = findViewById<TextView>(R.id.idText)
            val password = findViewById<TextView>(R.id.passwdText)
            val birth = findViewById<TextView>(R.id.birthText)
            val phone = findViewById<TextView>(R.id.phoneText)
            //회원가입 조건 검사하기
            val check = findViewById<TextView>(R.id.checkText)
            val pwcheckText = findViewById<TextView>(R.id.pwcheckText)

            val nameText = name.text.toString()
            val idText = id.text.toString()
            val passwdText = password.text.toString()
            val birthText = birth.text.toString()
            val phoneText = phone.text.toString()



                //전체 확인 후 저장
                if (nameText.length in 2..20) { // 이름이 2~20글자인 경우
                    if (check.getCurrentTextColor() == Color.BLUE) { // checkText의 색이 파란색인 경우
                        if (pwcheckText.getCurrentTextColor() == Color.BLUE) { // pwcheckText의 색이 파란색인 경우
                            if (birthText.length == 6) { // birthText의 길이가 6인 경우
                                // 회원가입을 진행
                                //DB에 데이터 전송하기
                                val request = object : StringRequest(
                                    //ip 현재 ip 주소로 항상 바꾸기
                                    Method.POST, "http://192.168.219.106/membership.php",
                                    com.android.volley.Response.Listener { response ->
                                        //서버에서 전송하는 응답 내용 확인
                                        Log.d("Response", response)
                                        Log.d("JSON Data", response)

                                        val responseData = JSONObject(response)
                                        if (responseData.has("success")) {
                                            val success = responseData.getBoolean("success")
                                            if (success) {
                                                // Request succeeded
                                                Toast.makeText(
                                                    this,
                                                    "요청이 성공했습니다.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                // Request failed
                                                Toast.makeText(
                                                    this,
                                                    "요청이 실패했습니다.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            // Handle the case where the key "성공!" is not found
                                        }
                                    },
                                    com.android.volley.Response.ErrorListener { error ->
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

                                        params["userName"] = nameText
                                        params["userID"] = idText
                                        params["userPW"] = passwdText
                                        params["userBirth"] = birthText
                                        params["userphone"] = phoneText

                                        return params

                                    }
                                }

                                val queue = Volley.newRequestQueue(this)
                                queue.add(request)
                            } else { // birthText의 길이가 6이 아닌 경우
                                // 알림창을 띄우고 이전 화면으로 돌아가기
                                Toast.makeText(
                                    this,
                                    "생년월일을 올바르게 입력해주세요. 예)980101",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else { // pwcheckText의 색이 파란색이 아닌 경우
                            // 알림창을 띄우고 이전 화면으로 돌아가기
                            Toast.makeText(this, "비밀번호 중복 확인을 하지 않았습니다.", Toast.LENGTH_SHORT).show()

                        }
                    } else { // checkText의 색이 파란색이 아닌 경우
                        // 알림창을 띄우고 이전 화면으로 돌아가기
                        Toast.makeText(this, "id 중복 확인을 해주세요.", Toast.LENGTH_SHORT).show()
                    }
                } else { // 이름이 2~20글자가 아닌 경우
                    // 알림창을 띄우고 이전 화면으로 돌아가기
                    Toast.makeText(this, "이름은 2~20글자로 설정해주세요.", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

