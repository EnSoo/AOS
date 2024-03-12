package com.mrhiles.aos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mrhiles.aos.data.StudyRoom
import com.mrhiles.aos.databinding.RecyclerAdapterStudyRoomListBinding

class StudyRoomRecyclerAdapter(val context:Context, val documents:List<StudyRoom>) : Adapter<StudyRoomRecyclerAdapter.VH>(){
    inner class VH(val binding:RecyclerAdapterStudyRoomListBinding) : ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=VH(RecyclerAdapterStudyRoomListBinding.inflate(LayoutInflater.from(context),parent,false))
    override fun getItemCount()=documents.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val studyRoom=documents[position]
        if(studyRoom.distance=="") holder.binding.tvDistance.text=""
        else holder.binding.tvDistance.text=studyRoom.distance
        holder.binding.placeName.text=studyRoom.place_name
        holder.binding.tvCategory.text=studyRoom.category_name
        holder.binding.phoneNumber.text="+82 ${studyRoom.phone}"

        holder.binding.homeItem.setOnClickListener{}
    }

}