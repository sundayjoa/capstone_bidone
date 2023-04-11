package com.example.bidone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    // 아이디 찾기 부분 버튼
    private Button findIdbutton;

    // 비밀번호 찾기 부분 버튼
    private Button findpasswdbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 아이디 찾기 부분 Find_Id로 액티비티 이동하기
        findIdbutton = findViewById(R.id.findIdbutton);
        findIdbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  // (현재,이동하고 싶은 클래스)
                Intent intent = new Intent(LoginActivity.this, Find_Id.class);
                startActivity(intent);

                                           }
                                       }

        );

        // 비밀번호 찾기 부분 Find_Pw 액티비티로 이동하기
        findpasswdbutton = findViewById(R.id.findpasswdbutton);
        findpasswdbutton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,Find_Pw.class);
                startActivity(intent);
            }
        });

    }
}