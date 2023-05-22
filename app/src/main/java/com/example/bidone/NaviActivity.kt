package com.example.bidone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.bidone.databinding.ActivityNaviBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class NaviActivity : AppCompatActivity() {

    //바인딩 선언
    private lateinit var binding: ActivityNaviBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //바인딩 선언
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //첫 시작화면
        supportFragmentManager.beginTransaction()

            .add(R.id.frame_layout,HomeFragment())
            .commit()

        //의뢰게시판에서 스피너로 전체를 선택했을 때 메인 게시판 화면 뜨게 하기
        val showBoardFragment = intent.getBooleanExtra("showBoardFragment", false)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigationView)

        if (showBoardFragment) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, BoardFragment())
                .commit()

            bottomNavigationView.selectedItemId = R.id.BoardFragment
        }

        //네비게이션 관련 명령어
        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.HomeFragment -> replaceFragment(HomeFragment())
                R.id.BoardFragment -> replaceFragment(BoardFragment())
                R.id.AlarmFragment -> replaceFragment(AlarmFragment())
                R.id.MyFragment -> replaceFragment(MyFragment())
            }
            true

        }
    }

    //replaceFragment 선언
    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()

    }
}