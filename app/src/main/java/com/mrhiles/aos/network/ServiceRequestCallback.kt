package com.mrhiles.aos.network

import com.mrhiles.aos.data.ResponseLecture

interface ServiceRequestCallback {
    fun onServiceRequesetSuccess(response:List<ResponseLecture>? = null)
    fun onServiceRequesetFailure()
}