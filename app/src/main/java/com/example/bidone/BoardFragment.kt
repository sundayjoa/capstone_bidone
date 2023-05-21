package com.example.bidone

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BoardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BoardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_board, container, false)

        //스피너 관련 명령어
        val spinner: Spinner = view.findViewById(R.id.spinner)
        // 스피너에 들어갈 항목
        val items = arrayOf("전체", "수공예품", "중고물품", "의뢰게시판")
        // 어댑터 생성
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        // 스피너에 어댑터 설정
        spinner.adapter = adapter

        //스피너에서 외뢰 게시판을 클릭했을 때 해당 게시판으로 이동
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                if (selectedItem == "의뢰게시판") {
                    val intent = Intent(activity, RequestboardActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무 항목도 선택되지 않았을 때 실행될 코드
            }
        }


        return view
    }

    //WritingboardActivity 화면 전환 리스너
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val writingButton: ImageButton = view.findViewById(R.id.writingButton)
        writingButton.setOnClickListener {
            activity?.let {
                val intent = Intent(it, WritingboardActivity::class.java)
                it.startActivity(intent)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BoardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BoardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}