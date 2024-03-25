package com.mrhiles.aos.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mrhiles.aos.G
import com.mrhiles.aos.databinding.FragmentBottomListBinding
import com.mrhiles.aos.R
import com.mrhiles.aos.activities.LectureSetActivity
import com.mrhiles.aos.adapter.LectureListRecyclerAdapter
import com.mrhiles.aos.data.Lecture
import com.mrhiles.aos.data.ResponseLecture
import com.mrhiles.aos.network.ServiceLectureRequestCallback
import com.mrhiles.aos.network.ServiceRequest

class BottomListFragment : Fragment() {
    private val binding by lazy { FragmentBottomListBinding.inflate(layoutInflater) }
    //검색 종류 : 범위, 검색키워드
    private var location=""
    private var searchQuery=""
    private val lectureList:MutableList<ResponseLecture> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val regionArray= resources.getStringArray(R.array.lecture_location_list)
        val arrayAdapter=ArrayAdapter<String>(requireContext(),R.layout.lecture_dropdown_item, regionArray)
        binding.dropdownMenu.setAdapter(arrayAdapter)
        if(G.isLogin) binding.editLecture.visibility=View.VISIBLE
        else binding.editLecture.visibility=View.INVISIBLE

        lectureLoad()

        binding.editLecture.setOnClickListener {
            //lecture 페이지로 이동
            val intent= Intent(context, LectureSetActivity::class.java)

            intent.putExtra("type","add") // Type이 Item일 경우 1개만 검색
            resultLauncher.launch(intent)
        }

        // 범위 드롭다운에서 아이템 클릭 시
        binding.dropdownMenu.setOnItemClickListener { parent, view, position, id ->
            location=(view as TextView).text.toString()
            searchLectureList()
            false
        }
        binding.inputEditSearch.setOnEditorActionListener { v, actionId, event ->
            searchQuery=binding.inputEditSearch.text.toString()
            binding.inputEditSearch.setText("")
            Toast.makeText(context, "$searchQuery", Toast.LENGTH_SHORT).show()
            searchLectureList()
            false
        }

    }
    // 글 추가 완료 후 새로고침
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), {lectureLoad() })

    private fun searchLectureList() {
        val request= Lecture(type="search", search_location = location, search_string = searchQuery, page = "1")
        ServiceRequest(requireContext(),"/user/lecture.php",request, callbackLecture = object : ServiceLectureRequestCallback{
            override fun onServiceLectureResponseSuccess(response: List<ResponseLecture>?) {
                lectureList.clear()
                response?.forEach{
                    lectureList.add(it)
                }
                binding.listRecycler.adapter= LectureListRecyclerAdapter(requireContext(),lectureList)
                binding.listRecycler.adapter!!.notifyDataSetChanged()
            }
            override fun onServiceLectureResponseFailure() {}
        }).serviceRequest()
    }
    private fun lectureLoad() {
        val request= Lecture(type="load", page = "1")
        ServiceRequest(requireContext(),"/user/lecture.php",request,callbackLecture = object : ServiceLectureRequestCallback{
            override fun onServiceLectureResponseSuccess(response: List<ResponseLecture>?) {
                response?.forEach{
                    lectureList.add(it)
                }
                binding.listRecycler.adapter= LectureListRecyclerAdapter(requireContext(),lectureList)
                binding.listRecycler.adapter!!.notifyDataSetChanged()
            }

            override fun onServiceLectureResponseFailure() {
                TODO("Not yet implemented")
            }

        }).serviceRequest("")
    }
}