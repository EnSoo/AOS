package com.mrhiles.aos.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.gson.Gson
import com.mrhiles.aos.G
import com.mrhiles.aos.activities.StudyRoomDetailActivity
import com.mrhiles.aos.data.StudyRoom
import com.mrhiles.aos.databinding.RecyclerAdapterStudyRoomListBinding

class StudyRoomTapHomeListRecyclerAdapter(val context:Context, val documents:List<StudyRoom>) : Adapter<StudyRoomTapHomeListRecyclerAdapter.VH>(){
    inner class VH(val binding:RecyclerAdapterStudyRoomListBinding) : ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=VH(RecyclerAdapterStudyRoomListBinding.inflate(LayoutInflater.from(context),parent,false))
    override fun getItemCount()=documents.size

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
            Toast.makeText(context, "${studyRoom.x} ${studyRoom.y}", Toast.LENGTH_SHORT).show()
        }
    }

}