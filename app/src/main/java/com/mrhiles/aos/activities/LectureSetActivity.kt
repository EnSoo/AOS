package com.mrhiles.aos.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mrhiles.aos.databinding.ActivityLectureSetBinding


// 강의 생성, 수정
class LectureSetActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLectureSetBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}