package com.example.bidone

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.telecom.Call
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Response
import org.w3c.dom.Text
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.provider.Settings
import android.Manifest
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request

class WritingboardActivity : AppCompatActivity() {
    //썸네일 이미지 버튼을 눌러 이미지 추가
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri ->
            uri?.let {
                val imageButton = findViewById<ImageButton>(R.id.imageButton)
                imageButton.scaleType = ImageView.ScaleType.FIT_CENTER

                imageButton.setImageURI(uri)
            }
        }

    //리사이클러 뷰에 이미지 추가
    private val REQUEST_CODE = 1
    private val imageList = mutableListOf<Uri>()
    private lateinit var imageAdapter: ImageAdapter
    private val getimageContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageList.add(uri)
            imageAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writingboard)

        //썸네일 추가 버튼 동작
        val imageButton = findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    getContent.launch("image/*")
                } else {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            } else {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    getContent.launch("image/*")
                } else {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
                }
            }
        }

        //간단 설명 50자 이상 입력받지 못하게 하기
        val simpleText = findViewById<EditText>(R.id.simple_explanation)
        simpleText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length > 50) {
                    simpleText.setText(s.subSequence(0, 50))
                    simpleText.setSelection(50)
                    Toast.makeText(this@WritingboardActivity, "간단 설명은 50자 이내로 적어주세요.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //설명에 이미지 추가 버튼(리사이클러뷰)
        val imageButton2 = findViewById<ImageButton>(R.id.imageButton2)
        imageButton2.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    getimageContent.launch("image/*")
                } else {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            } else {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    getimageContent.launch("image/*")
                } else {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
                }
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.imagerecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imageAdapter = ImageAdapter(imageList)
        imageAdapter.onItemClick = { position ->
            imageList.removeAt(position)
            imageAdapter.notifyItemRemoved(position)
        }
        recyclerView.adapter = imageAdapter
        recyclerView.addItemDecoration(SpacesItemDecoration(0))


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
                    val hour =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) timePicker.hour else timePicker.currentHour
                    val minute =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) timePicker.minute else timePicker.currentMinute
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
            //DB 연결 위해 설정

            //user는 나중에 mysql에 회원정보 DB가 생겼을 떄 연동
            var user = "userID"
            val titletext = findViewById<EditText>(R.id.title)
            val simple_explain = findViewById<EditText>(R.id.simple_explanation)
            val detail_explain = findViewById<EditText>(R.id.editText)
            val datetext = dialog.findViewById<TextView>(R.id.dateTextView)
            val timetext = dialog.findViewById<TextView>(R.id.timeTextView)
            val starteditText = dialog.findViewById<EditText>(R.id.startText)
            val increaseeditText = dialog.findViewById<EditText>(R.id.increaseText)
            val nextButton = dialog.findViewById<Button>(R.id.completebtn)

            //팝업창에서 확인 버튼 눌렀을 때
            nextButton.setOnClickListener {

                //작품 번호를 위해 랜덤한 숫자를 생성해서 넘겨주는 코드
                //mysql work_number에 존재하지 않는 숫자를 반환
                //서버에 넘겨주는 부분까지 동작
                val number_queue = Volley.newRequestQueue(this)
                //본인 ip 주소로 바꾸기
                val number_url = "http://ip 주소/random_number.php"
                val params = HashMap<String, String>()

                val stringRequest = StringRequest(Request.Method.GET, number_url,
                    Response.Listener<String> { response ->
                        val worknumber = response.toLong()
                        params["worknumber"] = worknumber.toString()
                        // 랜덤한 숫자 사용
                    },
                    Response.ErrorListener { error ->
                        // 오류 처리
                    })
                number_queue.add(stringRequest)


                val userID = user
                val title = titletext.text.toString()
                val simple_explanation = simple_explain.text.toString()
                val detail_explanation = detail_explain.text.toString()
                val datetextView = datetext.text.toString()
                val timetextView = timetext.text.toString()
                val starttext = starteditText.text.toString()
                val increasetext = increaseeditText.text.toString()


                //카테고리에서 정한 품목을 전송하는 코드


                //날짜 선택 if문
                if (datetextView.isEmpty()) {
                    //textView가 비어 있는 경우 경고 메시지를 표시.
                    Toast.makeText(this, "날짜를 반드시 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                } else {
                    //textView가 비어 있지 않은 경우 다음 단계로 진행
                }

                //시간 선택 if문
                if (timetextView.isEmpty()) {
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
                if (increasetext.isEmpty()) {
                    // EditText가 비어 있는 경우 경고 메시지를 표시.
                    Toast.makeText(this, "증가 가격을 반드시 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // EditText가 비어 있지 않은 경우 다음 단계로 진행
                }

                //DB에 데이터 전송하기(test)
                val request = object : StringRequest(
                    //본인 ip 주소로 바꾸기
                    Method.POST, "http://ip 주소/boardinfo.php",
                    Response.Listener { response ->
                        //서버에서 전송하는 응답 내용 확인
                        Log.d("Response", response)
                        Log.d("JSON Data", response)

                        val responseData = JSONObject(response)
                        if (responseData.has("성공!")) {
                            val success = responseData.getBoolean("성공!")
                            if (success) {
                                // Request succeeded
                                Toast.makeText(this, "요청이 성공했습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                // Request failed
                                Toast.makeText(this, "요청이 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Handle the case where the key "성공!" is not found
                        }
                    },
                    Response.ErrorListener { error ->
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
                        //아직 추가 안함
                        params["thumbnail"] = ""
                        params["category"] = ""
                        params["detail_image"] = ""
                        params["upload"] = ""

                        //추가 완료
                        params["userID"] = userID
                        params["title"] = title
                        params["simple_explanation"] = simple_explanation
                        params["detail_explanation"] = detail_explanation
                        params["datetextView"] = datetextView
                        params["timetextView"] = timetextView
                        params["starttext"] = starttext
                        params["increasetext"] = increasetext
                        return params
                    }
                }

                val queue = Volley.newRequestQueue(this)
                queue.add(request)
                //팝업 창 닫기
                dialog.dismiss()
                //게시판 창 닫기
                finish()
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

    //썸네일 이미지 버튼을 입력받기 위한 추가 코드
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getContent.launch("image/*")
        }
    }
}

//이미지 어댑터
class ImageAdapter(private val imageList: MutableList<Uri>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    var onItemClick: ((Int) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

        //리사이클러뷰에서 사진을 클릭하면 삭제
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = imageList[position]
        holder.imageView.setImageURI(uri)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}

//리사이클러뷰 사진 간격 조절
class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = space
        outRect.right = space
        outRect.bottom = space
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = space
        }
    }
}
