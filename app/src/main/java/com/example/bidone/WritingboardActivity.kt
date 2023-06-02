package com.example.bidone

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class WritingboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writingboard)

        //취소 버튼 누르면 다시 게시판 화면으로 전환
        val cancelButton = findViewById<Button>(R.id.cancelbutton)
        cancelButton.setOnClickListener {
            finish()
        }

        //완료 버튼 누르면 팝업 창
        val completeBtn = findViewById<Button>(R.id.completebutton)
        completeBtn.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.activity_writingdialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            //경매 시작 날짜를 정하는 스피너
            val dateTextView = dialog.findViewById<TextView>(R.id.dateTextView)
            val datePicker = DatePicker(this)
            datePicker.minDate = System.currentTimeMillis() - 1000
            val datealertDialog = AlertDialog.Builder(this)
                .setView(datePicker)
                .setPositiveButton("OK") { _, _ ->
                    val calendar = Calendar.getInstance()
                    calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dateTextView.text = dateFormat.format(calendar.time)
                }
                .create()

            dateTextView.setOnClickListener {
                datealertDialog.show()
            }

            //경매 시작 시간을 정하는 스피너
            val timeTextView = dialog.findViewById<TextView>(R.id.timeTextView)
            val timePicker = TimePicker(this)
            val timealertDialog = AlertDialog.Builder(this)
                .setView(timePicker)
                .setPositiveButton("OK") { _, _ ->
                    val hour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) timePicker.hour else timePicker.currentHour
                    val minute = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) timePicker.minute else timePicker.currentMinute
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    timeTextView.text = timeFormat.format(calendar.time)
                }
                .create()

            timeTextView.setOnClickListener {
                timealertDialog.show()
            }

            //Text에 아무것도 입력되지 않았다면 오류 메시지 띄우기
            val datetext = dialog.findViewById<TextView>(R.id.dateTextView)
            val timetext = dialog.findViewById<TextView>(R.id.timeTextView)
            val starteditText = dialog.findViewById<EditText>(R.id.startText)
            val increaseeditText = dialog.findViewById<EditText>(R.id.increaseText)
            val nextButton = dialog.findViewById<Button>(R.id.completebtn)
            nextButton.setOnClickListener {
                val datetextView = datetext.text.toString()
                val timetextView = timetext.text.toString()
                val starttext = starteditText.text.toString()
                val increasetext = increaseeditText.text.toString()

                //날짜 선택 if문
                if (datetextView.isEmpty()){
                    //textView가 비어 있는 경우 경고 메시지를 표시.
                    Toast.makeText(this, "날짜를 반드시 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                } else {
                    //textView가 비어 있지 않은 경우 다음 단계로 진행
                }

                //시간 선택 if문
                if (timetextView.isEmpty()){
                    //textView가 비어 있는 경우 경고 메시지를 표시
                    Toast.makeText(this, "시간을 반드시 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                } else {
                    //textView가 비어 있지 않은 경우 다음 단계로 진행
                }

                //시작 가격 if문
                if (starttext.isEmpty()) {
                    // EditText가 비어 있는 경우 경고 메시지를 표시.
                    Toast.makeText(this, "시작 가격을 반드시 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // EditText가 비어 있지 않은 경우 다음 단계로 진행
                }

                //증가 가격 if문
                if(increasetext.isEmpty()){
                    // EditText가 비어 있는 경우 경고 메시지를 표시.
                    Toast.makeText(this, "증가 가격을 반드시 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                } else{
                    // EditText가 비어 있지 않은 경우 다음 단계로 진행
                }
            }

           //취소 버튼 누르면 팝업 창 닫기
            val cancelbtn = dialog.findViewById<Button>(R.id.cancelbtn)
            cancelbtn.setOnClickListener() {
                dialog.dismiss()
            }
            
            //팝업 창 보여주기
            dialog.show()
        }

        //스피너 관련
        val spinner = findViewById<Spinner>(R.id.spinner2)
        val items = arrayOf("수공예품", "중고물품")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        }

     }