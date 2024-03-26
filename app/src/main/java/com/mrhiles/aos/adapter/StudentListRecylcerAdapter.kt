package com.mrhiles.aos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mrhiles.aos.data.StudentList
import com.mrhiles.aos.databinding.RecyclerAdapterLectureStudentListBinding

class StudentListRecylcerAdapter (val context: Context, val documents: List<StudentList>) : RecyclerView.Adapter<StudentListRecylcerAdapter.VH>() {
    inner class VH(val binding: RecyclerAdapterLectureStudentListBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=VH(
        RecyclerAdapterLectureStudentListBinding.inflate(LayoutInflater.from(context),parent,false))
    override fun getItemCount()=documents.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val student=documents[position]

        Glide.with(context).load(student.profile_image).into(holder.binding.profileImage)
        holder.binding.nickname.text=student.nickname
        holder.binding.applidateDay.text=dayFormat(student.applidate_day)
    }
    private fun dayFormat(dates:String) : String {
        val date=dates.split(" ")
        val date_days=date[0]
        val date_parts = date_days.split("-")
        val date_year = date_parts[0].toInt()
        val date_month = date_parts[1].toInt()
        val date_day = date_parts[2].toInt()
        val date_time=date[1]
        val date_hour = date_time.split(":")[0].toInt()
        val date_minute = date_time.split(":")[1].toInt()
        val date_amPm = if (date_hour < 12) "오전" else "오후"

        return "${date_month}월 ${date_day}일 ${date_amPm}${date_hour}시 ${date_minute}분"
    }
}