package com.mrhiles.aos.network

data class UserCheck(val access_token:String, val error:String)
data class responseData(val params:String, val code:String, val error:String) // code 7200 서비스 작업 성공, 7203 서비스 작업 실패
data class requestData(val params:String, val access_token:String) // requestData은 보낼 데이터 json 값

// requestData params 데이터 리스트
data class studyRoomFaovr (
    var id:String,                   //장소 ID
    var place_name:String,           // 장소명, 업체명
    var category_name:String,        // 카테고리 이름
    var phone:String,                // 전화번호
    var address_name:String,         // 전체 지번 주소
    var x:String,                    // X 좌표값, 경위도인 경우 longitude(경도)
    var y:String,                    // Y 좌표값, 경위도인 경우 latitue(위도)
    var place_url:String,            // 장소 상세페이지 URL
)

// responseData params 데이터 리스트
