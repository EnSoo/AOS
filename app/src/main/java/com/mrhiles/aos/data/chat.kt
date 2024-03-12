package com.mrhiles.aos.data
data class ChatRoom(
    val roomId:Int,                     // 채팅방 식별자
    val roomImageUrl:String,            // 채팅방 프로필
    val roomUserList:String,      // 채팅방 유저 리스트
    val lastMessage:String,             // 마지막 메시지
    val lastTime:String,                // 마지막 메시지 작성 시간
    val roomType:Int                    // 채팅방이 1:1인지 아닌지
)