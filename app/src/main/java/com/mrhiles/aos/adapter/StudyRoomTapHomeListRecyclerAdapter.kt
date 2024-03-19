package com.mrhiles.aos.adapter

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.gson.Gson
import com.mrhiles.aos.G
import com.mrhiles.aos.activities.MapActivity
import com.mrhiles.aos.activities.StudyRoomDetailActivity
import com.mrhiles.aos.data.StudyRoom
import com.mrhiles.aos.databinding.RecyclerAdapterStudyRoomListBinding
import com.mrhiles.aos.network.Service
import com.mrhiles.aos.network.studyRoomFaovr

class StudyRoomTapHomeListRecyclerAdapter(val context:Context, val documents:List<StudyRoom>) : Adapter<StudyRoomTapHomeListRecyclerAdapter.VH>(){
    inner class VH(val binding:RecyclerAdapterStudyRoomListBinding) : ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=VH(RecyclerAdapterStudyRoomListBinding.inflate(LayoutInflater.from(context),parent,false))
    override fun getItemCount()=documents.size

    //SQLite Database를 제어하는 객체 참조변수
    private lateinit var db:SQLiteDatabase

    override fun onBindViewHolder(holder: VH, position: Int) {
        val studyRoom=documents[position]
        if(studyRoom.distance=="") holder.binding.tvDistance.text=""
        else holder.binding.tvDistance.text="${studyRoom.distance}m"
        holder.binding.placeName.text=studyRoom.place_name
        holder.binding.tvCategory.text=studyRoom.category_name
        holder.binding.phoneNumber.text="+82 ${studyRoom.phone}"
        // 로그인 한 상태여야만 찜 가능
        if(G.isLogin) holder.binding.favor.visibility= View.VISIBLE
        // StudyRoom 클릭 시 웹뷰 출력
        holder.binding.homeItem.setOnClickListener{
            val intent = Intent(context,StudyRoomDetailActivity::class.java)

            //StudyRoom에 대한 데이터를 추가로 보내기
            val gson= Gson()
            val s=gson.toJson(studyRoom)
            intent.putExtra("studyRoom",s)

            context.startActivity(intent)
        }

        // 맵 아이콘 클릭시 맵 지도 출력
        holder.binding.map.setOnClickListener{
            val intent=Intent(context,MapActivity::class.java)

            //StudyRoom에 대한 데이터를 보내기
            val gson=Gson()
            val s=gson.toJson(studyRoom)
            intent.putExtra("studyRoom",s)
            intent.putExtra("type","Item") // Type이 Item일 경우 1개만 검색
            context.startActivity(intent)
        }

        // "study.db"라는 이름으로 데이터베이스 파일을 만들거나 열어서 참조하기
        db=ContextWrapper(context).openOrCreateDatabase("study", Context.MODE_PRIVATE,null)

        // "favor"라는 이름의 표(테이블) 만들기 - SQL 쿼리문을 사용하여.. CRUD 작업수행
        db.execSQL("CREATE TABLE IF NOT EXISTS favor(id TEXT PRIMARY KEY, place_name TEXT, category_name TEXT, phone TEXT, address_name TEXT, x TEXT, y TEXT, place_url TEXT)")
        holder.binding.favor.setOnClickListener {
            studyRoom.also { sr->
                val cursor: Cursor = db.rawQuery("SELECT * FROM favor WHERE id=?", arrayOf(sr.id))
                if(cursor.count>0){ // sql 조회 시 있을 경우 -> 삭제
                    val studyRoomFaovr= studyRoomFaovr(sr.id,sr.place_name, sr.category_name, sr.phone, sr.address_name, sr.x, sr.y, sr.place_url,"remove")
                    val service= Service(context,"/user/favor.php",studyRoomFaovr)
                    service.serviceRequest(it)
                } else { // sql 조회 시 없을 경우 -> 추가
                    val studyRoomFaovr= studyRoomFaovr(sr.id,sr.place_name, sr.category_name, sr.phone, sr.address_name, sr.x, sr.y, sr.place_url,"add")
                    val service= Service(context,"/user/favor.php",studyRoomFaovr)
                    service.serviceRequest(it)
                }
            }
        }
    }

}