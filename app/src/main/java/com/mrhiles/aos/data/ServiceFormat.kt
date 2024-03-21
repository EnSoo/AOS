package com.mrhiles.aos.data

data class UserCheck(val access_token:String, val error:String)
data class responseData(val params:String, val code:String, val error:String) // code 200 서비스 작업 성공, 201 서비스 작업 실패
data class requestData(val params:Any, val access_token:String) // requestData은 보낼 데이터 json 값

// 1. requestData params 데이터 리스트
// 1.1 studyroom favor
data class studyRoomFaovr (
    var id:String="",                   //장소 ID
    var place_name:String="",           // 장소명, 업체명
    var category_name:String="",        // 카테고리 이름
    var phone:String="",                // 전화번호
    var address_name:String="",         // 전체 지번 주소
    var x:String="",                    // X 좌표값, 경위도인 경우 longitude(경도)
    var y:String="",                    // Y 좌표값, 경위도인 경우 latitue(위도)
    var place_url:String="",            // 장소 상세페이지 URL
    var type:String                     // 삭제(remove), 추가(add), 목록(load)
)

// 1.2 lecture 개설
data class Lecture (
    val search_location:String="",      // 검색 지역
    val search_string:String="",        // 검색어
    val lecture_id:String="",           // 강의 id
    val title:String="",                // 강의 제목
    val introduction:String="",         // 강의 내용
    val start_date:String="",           // 강의 시작 날짜
    val end_date:String="",             // 강의 종료 날짜
    val studyroom_id:String="",         // 스터디룸 id
    val location:String="",             // 강의 지역
    val place_name:String="",           // 강의 장소명
    var x:String="",                    // X 좌표값, 경위도인 경우 longitude(경도)
    var y:String="",                    // Y 좌표값, 경위도인 경우 latitue(위도)
    val place_nickname:String="",       // 강의 별명
    val join_max:String="",             // 최대인원
    val join_min:String="",             // 최소인원
    val join_num:String="",             // 참여자수
    val notification:String="",         // 알릴내용
    val contract:String="",             // 연락처
    val page:String="",                 // 페이지 번호. 목록(load) 시 필요
    var type:String                     // 삭제(remove), 생성(add), 목록(load), 수정(modify), 마감(deadline)
                                        // 학생 목록(studentlist), 학생 참여(studentjoin), 학생 참여제외(withdraw)
                                        // 검색(search)
)

// 2. responseData params 데이터 리스트
// 2.1 studyroom favor
data class LoadStudyRoomFaovr (
    var id:String,                   //장소 ID
    var place_name:String,           // 장소명, 업체명
    var category_name:String,        // 카테고리 이름
    var phone:String,                // 전화번호
    var address_name:String,         // 전체 지번 주소
    var x:String,                    // X 좌표값, 경위도인 경우 longitude(경도)
    var y:String,                    // Y 좌표값, 경위도인 경우 latitue(위도)
    var place_url:String,            // 장소 상세페이지 URL
)

// 2.2 lecture
data class ResponseLecture (
    val lecture_id:String="",           // 강의 id
    val title:String="",                // 강의 제목
    val introduction:String="",         // 강의 내용
    val start_date:String="",           // 강의 시작 날짜
    val end_date:String="",             // 강의 종료 날짜
    val studyroom_id:String="",         // 스터디룸 id
    val location:String="",             // 강의 지역
    val place_name:String="",           // 스터디룸 장소명
    var longitude:String="",                    // X 좌표값, 경위도인 경우 longitude(경도)
    var latitue:String="",                    // Y 좌표값, 경위도인 경우 latitue(위도)
    val place_nickname:String="",       // 스터디룸 별명
    val join_max:String="",             // 최대인원
    val join_min:String="",             // 최소인원
    val join_num:String="",             // 참여인원
    val notification:String="",         // 알릴내용
    val contract:String="",             // 연락처
    val state:String="",                // 강의 모집 진행중일 경우 start, 아닐 경우 stop
    val myLecture:String="",            // 내 강의일 경우 TRUE, 아닐 경우 FALSE
    val studentList:String="",          // 학생 리스트
)