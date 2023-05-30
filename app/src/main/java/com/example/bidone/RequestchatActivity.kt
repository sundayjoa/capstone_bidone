package com.example.bidone

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView




class RequestchatActivity : AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requestchat)

        // 뒤로가기 버튼 클릭 시 뒤로가기
        val rchatbkButton = findViewById<ImageView>(R.id.rchatbkButton)
        rchatbkButton.setOnClickListener {
            finish()
        }

        // 채팅 설정 대화 상대 보기 채틍 우측 레이아웃 코드 수정 x

        val btn_chat_setting = findViewById<ImageView>(R.id.btn_chat_setting)
        btn_chat_setting.setOnClickListener {
            val layout_drawer = findViewById<DrawerLayout>(R.id.layout_drawer)
            layout_drawer.openDrawer(GravityCompat.END)   // START = left / END = right

        }
        val naviView_chat_set = findViewById<NavigationView>(R.id.naviView_chat_set)
        naviView_chat_set.setNavigationItemSelectedListener(this)// 네비게이션 아이템에 클릭 속성 부여



    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // 네비게이션 아이템 클릭 시 수행
        when (item.itemId) {
            R.id.person_requester -> Toast.makeText(applicationContext, "의뢰자", Toast.LENGTH_SHORT).show()
            R.id.person_seller -> Toast.makeText(applicationContext, "판매자", Toast.LENGTH_SHORT).show()
            //R.id.chat_exit -> Dialog
        }

        val layout_drawer = findViewById<DrawerLayout>(R.id.layout_drawer)
        layout_drawer.closeDrawers() // 네비게이션 뷰 닫기
        return false

    }

    override fun onBackPressed() {  // 백버튼을 눌렀을때 실행
        // drawer가 켜져있을때 백버튼 누르면 닫아줌 .. 이게 없으면 뒤로가기로 앱종료됨
        val layout_drawer = findViewById<DrawerLayout>(R.id.layout_drawer)
        if(layout_drawer.isDrawerOpen(GravityCompat.END))
        {
            layout_drawer.closeDrawers()
        }
        else{
            super.onBackPressed() // finsih

        }
    }
}