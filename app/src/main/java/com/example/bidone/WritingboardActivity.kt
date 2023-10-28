package com.example.bidone

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
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
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
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
import okhttp3.Callback
import okhttp3.OkHttpClient
import java.io.ByteArrayOutputStream
import android.util.Base64
import org.json.JSONException

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



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

        }
    }

    //리사이클러 뷰에 이미지 추가
    private val REQUEST_CODE = 1
    private val imageList = mutableListOf<Uri>()
    private lateinit var imageAdapter: ImageAdapter
    private val getimageContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            if (imageList.size >= 7) {
                Toast.makeText(this, "이미지는 7장 이상 첨부할 수 없습니다!", Toast.LENGTH_SHORT).show()
            } else {
                imageList.add(uri)
                imageAdapter.notifyDataSetChanged()
            }
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
                    Toast.makeText(
                        this@WritingboardActivity,
                        "간단 설명은 50자 이내로 적어주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
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
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imageAdapter = ImageAdapter(imageList)
        imageAdapter.onItemClick = { position ->
            imageList.removeAt(position)
            imageAdapter.notifyItemRemoved(position)
        }
        recyclerView.adapter = imageAdapter



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

            //작품 번호 지정해주기
            //ip 주소 현재 ip 주소로 항상 바꾸기
            val number_url = "http://192.168.219.106/random_number.php"

            val number_queue = Volley.newRequestQueue(this)
            val number_stringRequest = StringRequest(Request.Method.GET, number_url,
                Response.Listener<String> { response ->
                    // Display the response string
                    Log.d("Response", response)
                    val worknumberText = dialog.findViewById<TextView>(R.id.worknumberText)
                    worknumberText.text = response
                },
                Response.ErrorListener { /* Handle error */ })

            number_queue.add(number_stringRequest)

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

            val titletext = findViewById<EditText>(R.id.title)
            val simple_explain = findViewById<EditText>(R.id.simple_explanation)
            val spinner = findViewById<Spinner>(R.id.spinner2)
            val detail_explain = findViewById<EditText>(R.id.editText)
            val datetext = dialog.findViewById<TextView>(R.id.dateTextView)
            val timetext = dialog.findViewById<TextView>(R.id.timeTextView)
            val starteditText = dialog.findViewById<EditText>(R.id.startText)
            val increaseeditText = dialog.findViewById<EditText>(R.id.increaseText)
            val nextButton = dialog.findViewById<Button>(R.id.completebtn)

            //팝업창에서 확인 버튼 눌렀을 때
            nextButton.setOnClickListener {

                //리사이클러뷰 이미지들 압축하기 위한 코드
                fun compressImage(bitmap: Bitmap, quality: Int): ByteArray {
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
                    return stream.toByteArray()
                }

                fun uriToBitmap(uri: Uri): Bitmap? {
                    return try {
                        val inputStream = contentResolver.openInputStream(uri)
                        BitmapFactory.decodeStream(inputStream)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        null
                    }
                }

                //썸네일 압축 저장
                fun compressAndEncodeThumbnail(bitmap: Bitmap, quality: Int): String {
                    val thumbnailByteArray = compressImage(bitmap, quality)
                    return Base64.encodeToString(thumbnailByteArray, Base64.DEFAULT)
                }

                val thumbnailBitmap = (imageButton.drawable as BitmapDrawable).bitmap
                val thumbnail = compressAndEncodeThumbnail(thumbnailBitmap, 100)


                // 추가
                // 리사이클러뷰 이미지들을 압축해서 Base64로 인코딩
                val compressedImageList = mutableListOf<String>()

                for (uri in imageList) {
                    val recyclerViewBitmap = uriToBitmap(uri)
                    recyclerViewBitmap?.let {
                        val compressedByteArray = compressImage(it, 50) // 리사이클러뷰 이미지만 압축
                        val imageString = Base64.encodeToString(compressedByteArray, Base64.DEFAULT)
                        compressedImageList.add(imageString)
                    }
                }


                //DB 연동을 위해 쓰는 코드
                    //회원가입 시스템 완성하면 변경
                    val (userId, userName) = getUserInfo(this)

                    val title = titletext.text.toString()
                    val simple_explanation = simple_explain.text.toString()
                    val category = spinner.selectedItem.toString()
                    val detail_explanation = detail_explain.text.toString()
                    val datetextView = datetext.text.toString()
                    val timetextView = timetext.text.toString()
                    val starttext = starteditText.text.toString()
                    val increasetext = increaseeditText.text.toString()

                    // SimpleDateFormat을 사용하여 문자열 날짜를 Date 객체로 변환
                    val fullDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    val userSelectedDate = fullDateFormat.parse("$datetextView $timetextView")

                    // Calendar 객체를 생성하고 사용자가 선택한 날짜와 시간으로 설정
                    val calendar = Calendar.getInstance()
                    calendar.time = userSelectedDate!!

                    // Calendar 객체에 일주일 더하기
                    calendar.add(Calendar.WEEK_OF_YEAR, 1)

                    // finish_date 변수 생성
                    val finish_date = fullDateFormat.format(calendar.time)

                    // 업로드 날짜를 저장하기 위한 현재 시간 가져오기
                    val currentTime = Calendar.getInstance().time
                    // 시간 형식 변환
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val upload = dateFormat.format(currentTime)

                    // 종료시간 정의
                    val finishCalendar = Calendar.getInstance()
                    finishCalendar.time = currentTime
                    finishCalendar.add(Calendar.WEEK_OF_YEAR, 1) // 현재 시간에서 일주일 더하기
                    val finish = dateFormat.format(finishCalendar.time)


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

                    //DB에 데이터 전송하기
                    val request = object : StringRequest(
                        //ip 현재 ip 주소로 항상 바꾸기
                        Method.POST, "http://192.168.219.106/boardinfo.php",
                        Response.Listener { response ->
                            //서버에서 전송하는 응답 내용 확인
                            Log.d("Response", response)
                            Log.d("JSON Data", response)

                            val responseData = JSONObject(response)
                            if (responseData.has("success")) {
                                val success = responseData.getBoolean("success")
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

                            //추가 완료
                            //작품 번호 지정
                            val worknumberText = dialog.findViewById<TextView>(R.id.worknumberText)
                            val workNumber = worknumberText.text.toString()
                            Log.d("WorkNumber", workNumber)


                            params["worknumber"] = workNumber

                            userId?.let {
                                params["userID"] = it
                            }

                            userName?.let {
                                params["userName"] = it
                            }
                            params["title"] = title
                            params["thumbnail"] = thumbnail
                            params["simple_explanation"] = simple_explanation
                            params["category"] = category
                            params["detail_explanation"] = detail_explanation
                            params["detail_image"] = compressedImageList.joinToString(",")
                            params["datetextView"] = datetextView
                            params["timetextView"] = timetextView
                            params["starttext"] = starttext
                            params["increasetext"] = increasetext
                            params["upload"] = upload
                            params["finish"] = finish
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
    class ImageAdapter(private val imageList: MutableList<Uri>) :
        RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
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
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
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

    fun getUserInfo(context: Context): Pair<String?, String?> {
        val sharedPreferences = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("ID", null)
        val userName = sharedPreferences.getString("userName", null)
        return Pair(userId, userName)
    }


    //리사이클러뷰 사진 간격 조절
    class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.left = space
            outRect.right = space
            outRect.bottom = space
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space
            }
        }
    }


