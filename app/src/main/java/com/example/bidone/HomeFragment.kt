package com.example.bidone

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 홈 fragment -> 현재 관심도가 높은 작품 버튼 이벤트
        val favButton: ImageButton = view.findViewById(R.id.favButton)
        favButton.setOnClickListener {
            activity?.let {
                val intent = Intent(it,FavoriteActivity::class.java)
                it.startActivity(intent)
            }
        }

        // 홈 fragment -> 실시간 경매 작품 버튼 이벤트
        val rtimeButton: ImageButton = view.findViewById(R.id.rtimeButton)
        rtimeButton.setOnClickListener {
            activity?.let {
                val intent = Intent(it,RealtimeActivity::class.java)
                it.startActivity(intent)
            }
        }

        // 홈 fragment -> 의뢰게시판 버튼 이벤트
        val requestboardButton: ImageButton = view.findViewById(R.id.requestboardButton)
        requestboardButton.setOnClickListener {
            activity?.let {
                val intent = Intent(it, RequestboardActivity::class.java)
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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}