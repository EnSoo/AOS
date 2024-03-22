package com.mrhiles.aos.network

import com.mrhiles.aos.data.LoadStudyRoomFaovr
import com.mrhiles.aos.data.ResponseLecture

interface ServiceFavorRequestCallback {
    fun onServiceFavorResponseSuccess(response:List<LoadStudyRoomFaovr>? = null)
    fun onServiceFavorResponseFailure()
}

interface ServiceLectureRequestCallback {
    fun onServiceLectureResponseSuccess(response:List<ResponseLecture>? = null)
    fun onServiceLectureResponseFailure()
}
