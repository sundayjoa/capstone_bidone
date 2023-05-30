package com.example.bidone

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
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
            val alertDialog = AlertDialog.Builder(this)
                .setView(datePicker)
                .setPositiveButton("OK") { _, _ ->
                    val calendar = Calendar.getInstance()
                    calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dateTextView.text = dateFormat.format(calendar.time)
                }
                .create()

            dateTextView.setOnClickListener {
                alertDialog.show()
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