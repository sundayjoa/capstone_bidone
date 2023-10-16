package com.example.bidone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class mainrequestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainrequest)

        //title 넘겨받기
        val titleView = findViewById<TextView>(R.id.titleView)
        val title = intent.getStringExtra("title")

        titleView.text = title

        //userName 넘겨받기
        val userNameView = findViewById<TextView>(R.id.userView)
        val userName = intent.getStringExtra("userName")

        userNameView.text = userName

        //content 넘겨 받기
        val contentView = findViewById<TextView>(R.id.content)
        val content = intent.getStringExtra("content")

        contentView.text = content

        //hope_purchase 넘겨 받기
        val hopeView = findViewById<TextView>(R.id.hope_purchase)
        val hope = "구매 희망가: " + intent.getStringExtra("hope_purchase")

        hopeView.text = hope

        //uploadData 넙겨받기
        val uploadView = findViewById<TextView>(R.id.upload_date)
        val upload_date = intent.getStringExtra("upload_date")

        uploadView.text = "업로드 시간:" + upload_date

        //userID 넘겨받기
        val userID = intent.getStringExtra("userID")

        //request_number 넘겨받기
        val request_number = intent.getStringExtra("request_number")

        //뒤로 가기 버튼
        val bkBtn = findViewById<ImageView>(R.id.bkButton)

        bkBtn.setOnClickListener{
            finish()
        }

        //채팅 버튼
        val chatBtn = findViewById<ImageView>(R.id.chattingBtn)

        chatBtn.setOnClickListener{
            val intent = Intent(this, RequestchatActivity::class.java)
            startActivity(intent)
        }


    }
}