package com.mrhiles.aos.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.gson.Gson
import com.mrhiles.aos.R
import com.mrhiles.aos.activities.LectureDetailActivity
import com.mrhiles.aos.data.ResponseLecture
import com.mrhiles.aos.databinding.RecyclerAdapterLectureListBinding
import java.util.Calendar

class LectureListRecyclerAdapter(val context: Context, val documents: List<ResponseLecture>) : Adapter<LectureListRecyclerAdapter.VH>() {
    inner class VH(val binding: RecyclerAdapterLectureListBinding) :ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=VH(RecyclerAdapterLectureListBinding.inflate(LayoutInflater.from(context),parent,false))
    override fun getItemCount(): Int {
        var count=0;
        documents.forEach { entry ->
            //START 이거나, STOP인데 내 강의 인경우 리스트에 출력
            if(entry.state != "STOP" || (entry.state == "STOP" && entry.myLecture=="1")) count++
        }
        return count
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val lecture=documents[position]
        if(lecture.state=="STOP") { // 만약 강의가 마감 되었으면,
            if(lecture.myLecture=="1") { // 하지만 내 강의 인 경우
                holder.binding.lectureItem.setStrokeColor(Color.parseColor("#FF0000FF"))
                holder.binding.lectureItem.setCardBackgroundColor(Color.parseColor("#FFB3B2B2"))
            } else {
                return
            }
        } else {
            if(lecture.myLecture=="1") { // 강의가 진행중이고 내 강의 인 경우
                holder.binding.lectureItem.setStrokeColor(Color.parseColor("#FF0000FF"))
            }
        }
        val start_date=lecture.start_date.split(" ");
        val start_days=start_date[0]
        val start_parts = start_days.split("-")
        val start_year = start_parts[0].toInt()
        val start_month = start_parts[1].toInt()
        val start_day = start_parts[2].toInt()
        val start_time=start_date[1]
        val start_hour = start_time.split(":")[0].toInt()
        val start_minute = start_time.split(":")[1].toInt()
        val start_amPm = if (start_hour < 12) "오전" else "오후"

        val end_date=lecture.end_date.split(" ");
        val end_days=start_date[0]
        val end_parts = end_days.split("-")
        val end_year = start_parts[0].toInt()
        val end_month = end_parts[1].toInt()
        val end_day = end_parts[2].toInt()
        val end_time=start_date[1]
        val end_hour = end_time.split(":")[0].toInt()
        val end_minute = end_time.split(":")[1].toInt()
        val end_amPm = if (end_hour < 12) "오전" else "오후"

        val isToday=isToday(start_year.toString(),start_month.toString(),start_day.toString())
        if(isToday) holder.binding.today.visibility= View.VISIBLE
        holder.binding.lectureName.text=lecture.title
        holder.binding.lectureContent.text=lecture.introduction
        holder.binding.reserveDay.text="${start_month}월 ${start_day}일"
        holder.binding.reserveTime.text="${start_amPm} ${start_hour}시"
        holder.binding.reserveLocation.text=lecture.location
        holder.binding.reserveJoinNum.text=lecture.join_num
        holder.binding.reserveJoinMax.text=lecture.join_max
        holder.binding.reserveJoinMin.text="(최소 ${lecture.join_min}명)"
        holder.binding.lectureItem.setOnClickListener {
            val intent= Intent(context, LectureDetailActivity::class.java)
            //StudyRoom에 대한 데이터를 보내기
            val gson= Gson()
            val s=gson.toJson(lecture)
            intent.putExtra("lecture",s)
            context.startActivity(intent)
        }
    }
    fun isToday(year: String, month: String, day: String): Boolean {
        val today = Calendar.getInstance()
        val todayYear = today.get(Calendar.YEAR)
        val todayMonth = today.get(Calendar.MONTH) + 1 // Calendar month is 0-based
        val todayDay = today.get(Calendar.DAY_OF_MONTH)

        return year.toInt() == todayYear && month.toInt() == todayMonth && day.toInt() == todayDay
    }
}