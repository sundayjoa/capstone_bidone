package com.example.bidone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //회원가입 창 전환
        val membershipbutton=findViewById<Button>(R.id.membershipbutton)

        membershipbutton.setOnClickListener(){
            val intent=Intent(this@LoginActivity, MembershipActivity::class.java)
            startActivity(intent)
        }

        //아이디 찾기 창 전환
        val findIdbutton=findViewById<Button>(R.id.findIdbutton)

        findIdbutton.setOnClickListener(){
            val intent=Intent(this@LoginActivity, Find_Id::class.java)
            startActivity(intent)
        }

        //비밀번호 찾기 창 전환
        val findpasswdbutton=findViewById<Button>(R.id.findpasswdbutton)

        findpasswdbutton.setOnClickListener(){
            val intent=Intent(this@LoginActivity, Find_Pw::class.java)
            startActivity(intent)
        }

        //메인화면 창 전환(임시:현재 게시판 화면과 연결. 메인화면으로 수정해야 함.)
        //비밀번호 찾기 창 전환
        val loginbutton=findViewById<Button>(R.id.loginbutton)

        loginbutton.setOnClickListener(){
            val intent=Intent(this@LoginActivity, NaviActivity::class.java)
            startActivity(intent)
        }

        }
    }