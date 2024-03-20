package com.mrhiles.aos.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.mrhiles.aos.databinding.FragmentBottomListBinding
import com.mrhiles.aos.R
import com.mrhiles.aos.activities.LectureSetActivity
import com.mrhiles.aos.adapter.LectureListRecyclerAdapter
import com.mrhiles.aos.data.LectureInfo

class BottomListFragment : Fragment(){
    private val binding by lazy { FragmentBottomListBinding.inflate(layoutInflater) }
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

        val lecture:MutableList<LectureInfo> = mutableListOf()
        // 예시 강의 데이터
        lecture.add(LectureInfo("혜화) 24 상반기 면접스터디 모집합니다",
            "안녕하세요 혜화 면접스터디 하실분 구합니다! 당장 면접을 위한 단기ddd",
            "3월 15일","오후6시","서울 종로구","3","9","2"))
        lecture.add(LectureInfo("혜화) 24 상반기 면접스터디 모집합니다",
            "안녕하세요 혜화 면접스터디 하실분 구합니다! 당장 면접을 위한 단기ddd",
            "3월 15일","오후6시","서울 종로구","3","9","2"))
        lecture.add(LectureInfo("혜화) 24 상반기 면접스터디 모집합니다",
            "안녕하세요 혜화 면접스터디 하실분 구합니다! 당장 면접을 위한 단기ddd",
            "3월 15일","오후6시","서울 종로구","3","9","2"))
        lecture.add(LectureInfo("혜화) 24 상반기 면접스터디 모집합니다",
            "안녕하세요 혜화 면접스터디 하실분 구합니다! 당장 면접을 위한 단기ddd",
            "3월 15일","오후6시","서울 종로구","3","9","2"))
        lecture.add(LectureInfo("혜화) 24 상반기 면접스터디 모집합니다",
            "안녕하세요 혜화 면접스터디 하실분 구합니다! 당장 면접을 위한 단기ddd",
            "3월 15일","오후6시","서울 종로구","3","9","2"))
        lecture.add(LectureInfo("혜화) 24 상반기 면접스터디 모집합니다",
            "안녕하세요 혜화 면접스터디 하실분 구합니다! 당장 면접을 위한 단기ddd",
            "3월 15일","오후6시","서울 종로구","3","9","2"))
        lecture.add(LectureInfo("혜화) 24 상반기 면접스터디 모집합니다",
            "안녕하세요 혜화 면접스터디 하실분 구합니다! 당장 면접을 위한 단기ddd",
            "3월 15일","오후6시","서울 종로구","3","9","2"))
        binding.listRecycler.adapter=LectureListRecyclerAdapter(requireContext(),lecture)

        binding.addLecture.setOnClickListener {
            //lecture 페이지로 이동
            val intent= Intent(context, LectureSetActivity::class.java)

            intent.putExtra("type","add") // Type이 Item일 경우 1개만 검색
            requireContext().startActivity(intent)
        }
    }
}