package com.example.bidone

import android.content.Intent
import android.os.Bundle
import android.view.View
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

        }
    }