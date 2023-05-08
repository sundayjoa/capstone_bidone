package com.example.bidone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.bidone.databinding.ActivityNaviBinding


class NaviActivity : AppCompatActivity() {

    //바인딩 선언
    private lateinit var binding: ActivityNaviBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //바인딩 선언
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)

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