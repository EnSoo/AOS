package com.mrhiles.aos.data

// 삭제 예정
data class LectureInfo(
    val lectureName : String,           // 강의 제목
    val lectureContent : String,        // 강의 컨텐츠
    val reserveDay : String,            // 강의 예약 날짜
    val reserveTime : String,           // 강의 예약 시간
    val reserveLocation : String,       // 강의 위치
    val reserveJoinNum : String,        // 강의 가입 인원
    val reserveJoinMax : String,        // 강의 최대 인원
    val reserveJoinMin : String         // 강의 최소 인원

)