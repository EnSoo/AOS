package com.mrhiles.aos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mrhiles.aos.data.LectureInfo
import com.mrhiles.aos.databinding.RecyclerAdapterLectureListBinding
import com.mrhiles.aos.databinding.RecyclerAdapterStudyRoomListBinding

class LectureListRecyclerAdapter(val context: Context, val documents:List<LectureInfo>) : Adapter<LectureListRecyclerAdapter.VH>() {
    inner class VH(val binding: RecyclerAdapterLectureListBinding) :ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=VH(RecyclerAdapterLectureListBinding.inflate(LayoutInflater.from(context),parent,false))
    override fun getItemCount()=documents.size
    override fun onBindViewHolder(holder: VH, position: Int) {
        val lecture=documents[position]
        holder.binding.lectureName.text=lecture.lectureName
        holder.binding.lectureContent.text=lecture.lectureContent
        holder.binding.reserveDay.text=lecture.reserveDay
        holder.binding.reserveTime.text=lecture.reserveTime
        holder.binding.reserveLocation.text=lecture.reserveLocation
        holder.binding.reserveJoinNum.text=lecture.reserveJoinNum
        holder.binding.reserveJoinMax.text=lecture.reserveJoinMax
        holder.binding.reserveJoinMin.text="(최소 ${lecture.reserveJoinMin}명)"
        holder.binding.lectureItem.setOnClickListener {  }
    }
}