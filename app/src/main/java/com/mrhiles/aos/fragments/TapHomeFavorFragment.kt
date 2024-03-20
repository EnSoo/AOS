package com.mrhiles.aos.fragments

import android.content.Context
import android.content.ContextWrapper
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mrhiles.aos.adapter.StudyRoomTapHomeFavorRecyclerAdapter
import com.mrhiles.aos.data.StudyRoom
import com.mrhiles.aos.databinding.FragmentTapHomeFavorBinding
import com.mrhiles.aos.network.studyRoomFaovr

class TapHomeFavorFragment : Fragment(){
    // kakao search API 응답결과 객체 참조변수
    val binding by lazy { FragmentTapHomeFavorBinding.inflate(layoutInflater) }
    private lateinit var db: SQLiteDatabase
    private val items : MutableList<StudyRoom> by lazy { mutableListOf() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // "study.db"라는 이름으로 데이터베이스 파일을 만들거나 열어서 참조하기
        db= ContextWrapper(context).openOrCreateDatabase("study", Context.MODE_PRIVATE,null)

        // "favor"라는 이름의 표(테이블) 만들기 - SQL 쿼리문을 사용하여.. CRUD 작업수행
        db.execSQL("CREATE TABLE IF NOT EXISTS favor(id TEXT PRIMARY KEY, place_name TEXT, category_name TEXT, phone TEXT, address_name TEXT, x TEXT, y TEXT, place_url TEXT)")
        val cursor:Cursor=db.rawQuery("SELECT * FROM favor",null)
        while(cursor.moveToNext()) {
            cursor.apply {
                val item=StudyRoom(getString(0),getString(1),getString(2),"","",
                    getString(3),getString(4),"",getString(5),getString(6),getString(7),"")
                items.add(item)
            }
        }

        binding.homeRecycler.adapter = StudyRoomTapHomeFavorRecyclerAdapter(requireContext(), items)
    }
}