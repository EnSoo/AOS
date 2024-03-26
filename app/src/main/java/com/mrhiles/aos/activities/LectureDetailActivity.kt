package com.mrhiles.aos.activities
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.mrhiles.aos.G
import com.mrhiles.aos.R
import com.mrhiles.aos.adapter.StudentListRecylcerAdapter
import com.mrhiles.aos.adapter.StudyRoomTapHomeFavorRecyclerAdapter
import com.mrhiles.aos.data.Lecture
import com.mrhiles.aos.data.ResponseLecture
import com.mrhiles.aos.data.StudentList
import com.mrhiles.aos.data.StudentListResponse
import com.mrhiles.aos.databinding.ActivityLectureDetailBinding
import com.mrhiles.aos.fragments.BottomListFragment
import com.mrhiles.aos.network.ServiceLectureRequestCallback
import com.mrhiles.aos.network.ServiceRequest
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import kotlin.concurrent.thread

class LectureDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private val binding by lazy { ActivityLectureDetailBinding.inflate(layoutInflater) }
    private lateinit var naverMap : NaverMap
    private lateinit var responseLecture : ResponseLecture
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val s=intent.getStringExtra("lecture")
        responseLecture= Gson().fromJson(s, ResponseLecture::class.java)

        binding.title.text=responseLecture.title
        binding.createDate.text="개설일자: ${responseLecture.created_at}"
        binding.reserveJoinNum.text=responseLecture.join_num
        binding.reserveJoinMax.text=responseLecture.join_max
        binding.reserveJoinMin.text="최소 ${responseLecture.join_min}명 모임시작"
        binding.startDate.text=dayFormat(responseLecture.start_date,"full")
        binding.endDate.text=dayFormat(responseLecture.end_date,"full")
        binding.placeName.text=responseLecture.place_name
        binding.introduction.text=responseLecture.introduction
        binding.notification.text=responseLecture.notification
        binding.contract.text=responseLecture.contract
        binding.bottomPlaceName.text=responseLecture.place_name
        binding.bottomLocation.text=responseLecture.location

        // 수정 버튼
        binding.btnEdit.setOnClickListener {
            //lecture 페이지로 이동
            val intent= Intent(this, LectureSetActivity::class.java)
            intent.putExtra("lecture",s)
            intent.putExtra("type","modify") // Type이 Item일 경우 1개만 검색
            startActivity(intent)
            closeActivity()
        }

        //학생 리스트 불러오기
        studentList(responseLecture.myLecture, responseLecture.lecture_id)

        //학생참여버튼
        binding.btnJoin.setOnClickListener { clickJoin(responseLecture.lecture_id) }

        //학생취소버튼
        binding.btnWithdraw.setOnClickListener { clickWithdraw(responseLecture.lecture_id)  }

        //마감버튼
        binding.btnDeadline.setOnClickListener { clickDeadline(responseLecture.lecture_id) }

        //버튼 출력 처리
        responseLecture.apply {
            btnProcess(myLecture,state,lecture_join)
        }

        // 뒤로가기 종료
        binding.arrowBack.setOnClickListener { closeActivity() }

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        // fragment의 getMapAsync() 메서드로 OnMapReadyCallback 콜백을 등록하면 비동기로 NaverMap 객체를 얻을 수 있다.
        mapFragment.getMapAsync(this)
    }

    private fun dayFormat(dates:String, type:String) : String {
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

        if (type=="full") {
            return "${date_year}년 ${date_month}월 ${date_day}일 ${date_amPm}${date_hour}시 ${date_minute}분"
        } else {
            return "${date_year}년 ${date_month}월 ${date_day}일"
        }

    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap=naverMap

        val latLng=LatLng(responseLecture.latitude.toDouble(),responseLecture.longitude.toDouble())

        setMap(latLng, responseLecture)
    }

    private fun setMap(latLng:LatLng, responseLecture:ResponseLecture) {
        val marker=Marker()
        marker.apply {
            position=latLng
            map=naverMap
            captionText=responseLecture.place_name
            captionRequestedWidth=200
            setCaptionAligns(Align.Bottom)
        }

        // 정보창
        val infoWindow = InfoWindow()          // 윈도우창 객체 생성
        infoWindow.adapter=object : InfoWindow.DefaultTextAdapter(this) { // 정보창 내용
            override fun getText(p0: InfoWindow): CharSequence {
                return "${responseLecture.place_name}\n\n${responseLecture.location}"
            }
        }

        // 마커 클릭 시
        val listener = Overlay.OnClickListener { overlay ->
            val type=intent.getStringExtra("type")
            val marker=overlay as Marker
            if (marker.infoWindow == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.open(marker)
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close()
            }
            true
        }
        marker.onClickListener=listener

        // 카메라 이동
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng,15.0)
        //내 위치로 카메라 이동
        this.naverMap.moveCamera(cameraUpdate)
    }

    private fun btnProcess(myLecture:String,state:String, lecture_join:String) {
        if(myLecture=="1") {
            if(state=="START") {
                //내가 생성한 강의 이면서 현재 진행중인 경우
                binding.btnEdit.visibility= View.VISIBLE
                binding.btnDeadline.visibility= View.VISIBLE
            } else {
                //내 강의 이면서 현재 종료된 경우
            }
        } else {
            if(state=="START") {
                //현재 진행중인 경우
                if(lecture_join=="1") binding.btnWithdraw.visibility= View.VISIBLE // 내가 참여중인 경우
                else binding.btnJoin.visibility= View.VISIBLE
            } else {
                //현재 종료된 경우
            }
        }
    }

    private fun clickJoin(lecture_id:String) { // 학생 강의 참여
        if(G.userInfo.ProfileUrl=="") {
            val lecture = Lecture(lecture_id = lecture_id, type = "studentjoin")
            ServiceRequest(
                this,
                "/user/lecture.php",
                lecture,
                callbackLecture = object : ServiceLectureRequestCallback {
                    override fun onServiceLectureResponseSuccess(response: List<ResponseLecture>?) {}
                    override fun onServiceLectureResponseFailure() {}
                }).serviceRequest()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("프로필 메뉴에서 사용자 정보를 설정 해주세요")
            builder.setPositiveButton("확인") { dialog, _ ->
                // 확인 버튼을 눌렀을 때 실행할 코드
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun clickWithdraw(lecture_id:String) { // 학생 강의 취소
        val lecture= Lecture(lecture_id = lecture_id, type="withdraw")
        ServiceRequest(this,"/user/lecture.php",lecture, callbackLecture = object : ServiceLectureRequestCallback {
            override fun onServiceLectureResponseSuccess(response: List<ResponseLecture>?) {
                binding.btnJoin.visibility= View.VISIBLE
                binding.btnWithdraw.visibility= View.GONE
            }
            override fun onServiceLectureResponseFailure() {}
        }).serviceRequest()
    }

    private fun clickDeadline(lecture_id:String) { // 강의 마감하기
        val lecture= Lecture(lecture_id = lecture_id, type="deadline")
        ServiceRequest(this,"/user/lecture.php",lecture, callbackLecture = object : ServiceLectureRequestCallback {
            override fun onServiceLectureResponseSuccess(response: List<ResponseLecture>?) {
                binding.btnDeadline.visibility= View.GONE
                binding.btnEdit.visibility= View.GONE
            }
            override fun onServiceLectureResponseFailure() {}
        }).serviceRequest()
    }

    private fun studentList(myLecture: String, lecture_id:String) {
        val lecture= Lecture(lecture_id = lecture_id, type="studentlist")
        if(myLecture == "1")
            ServiceRequest(this,"/user/lecture.php",lecture, callbackLecture = object : ServiceLectureRequestCallback {
            override fun onServiceLectureResponseSuccess(response: List<ResponseLecture>?) {
                val studentList=Gson().fromJson(response?.get(0)?.studentList,Array<StudentList>::class.java).toList()
                binding.studentListRecycler.adapter = StudentListRecylcerAdapter(this@LectureDetailActivity, studentList)
            }
            override fun onServiceLectureResponseFailure() {}
        }).serviceRequest()
    }
    private fun closeActivity() {
        finish()
    }
}