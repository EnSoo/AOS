package com.mrhiles.aos.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mrhiles.aos.data.Lecture
import com.mrhiles.aos.data.studyRoomFaovr
import com.mrhiles.aos.databinding.ActivityLectureSetBinding
import com.mrhiles.aos.network.ServiceRequest
import java.text.SimpleDateFormat
import java.util.Calendar


// 강의 생성, 수정
class LectureSetActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLectureSetBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val type=intent.getStringExtra("type")

        // 액티비티 제목
        if(type=="add") {
            binding.activityName.text="강의 생성"
        } else if(type=="modify") {
            binding.activityName.text="강의 수정"
        }


        //시작일, 종료일 누를 경우 달력 표시
        binding.inputStartDate.editText?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this)
            datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
                val timePickerDialog=TimePickerDialog(this, TimePickerDialog.OnTimeSetListener() { view, hourOfDay, minute ->
                    val selectedDate= Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth, hourOfDay, minute)

                    val format= SimpleDateFormat("yyyy-MM-dd HH:mm")
                    val formattedDate=format.format(selectedDate.time)
                    binding.inputStartDate.editText?.setText(formattedDate)

                    // Open TimePickerDialog for end date after setting start date
                    openEndDatePicker(selectedDate.timeInMillis)
                },0,0,false)
                timePickerDialog.show();
            }
            datePickerDialog.show()
        }
        binding.inputEndDate.editText?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this)
            datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
                val timePickerDialog=TimePickerDialog(this, TimePickerDialog.OnTimeSetListener() { view, hourOfDay, minute ->
                    val selectedDate= Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth, hourOfDay, minute)

                    val format= SimpleDateFormat("yyyy-MM-dd HH:mm")
                    val formattedDate=format.format(selectedDate.time)
                    binding.inputEndDate.editText?.setText(formattedDate)
                },0,0,false)
                timePickerDialog.show();
            }
            datePickerDialog.show()
        }

        // 위치, 장소 에디트 클릭 시 MapActivity 동작
        binding.inputLocation.editText?.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("type","lecture")
            resultLauncher.launch(intent)
        }
        binding.inputPlaceName.editText?.setOnClickListener {
            // NaverMapActivity로 이동
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("type","lecture")
            resultLauncher.launch(intent)
        }

        // 뒤로가기 버튼 및 취소 버튼 클릭 시 액티비티 종료
        binding.arrowBack.setOnClickListener { finish() }
        binding.btnCancel.setOnClickListener { finish() }
        binding.btnSubmit.setOnClickListener {
            val textInputLayoutList:MutableMap<String,TextInputLayout> = mutableMapOf()

            val titleInputLayout=binding.inputTitle
            val introductionInputLayout=binding.inputIntroduction
            val startDateInputLayout=binding.inputStartDate
            val endDateInputLayout=binding.inputEndDate
            val studyroomIdInputLayout=binding.inputStudyroomId
            val locationInputLayout=binding.inputLocation
            val placeNameInputLayout=binding.inputPlaceName
            val xInputLayout=binding.inputX
            val yInputLayout=binding.inputY
            val joinMaxLayout=binding.inputJoinMax
            val joinMinInputLayout=binding.inputJoinMin
            val notificationInputLayout=binding.inputNotification
            val contractInputLayout=binding.inputContract
            textInputLayoutList["title"]=titleInputLayout
            textInputLayoutList["introduction"]=introductionInputLayout
            textInputLayoutList["startDate"]=startDateInputLayout
            textInputLayoutList["endDate"]=endDateInputLayout
            textInputLayoutList["studyroom_id"]=studyroomIdInputLayout
            textInputLayoutList["location"]=locationInputLayout
            textInputLayoutList["placeName"]=placeNameInputLayout
            textInputLayoutList["x"]=xInputLayout
            textInputLayoutList["y"]=yInputLayout
            textInputLayoutList["joinMax"]=joinMaxLayout
            textInputLayoutList["joinMin"]=joinMinInputLayout
            textInputLayoutList["notification"]=notificationInputLayout
            textInputLayoutList["contract"]=contractInputLayout

            if(!checkAllInputFields(textInputLayoutList)) {
                Toast.makeText(this, "빈 입력폼이 있습니다. 확인해주세요.", Toast.LENGTH_SHORT).show()
            } else { lectureProcess(textInputLayoutList,type!!) }
        }
    }

    private fun lectureProcess(textInputLayoutList:MutableMap<String,TextInputLayout>, type:String) {
        textInputLayoutList.also {tl ->
            var lectureId=""
            if(type=="modify") {
                lectureId=intent.getStringExtra("lecture_id")!!
            }
            val lecture= Lecture(
                lecture_id = lectureId,
                title= tl["title"]!!.editText!!.text.toString(),
                introduction = tl["introduction"]!!.editText!!.text.toString(),
                start_date = tl["startDate"]!!.editText!!.text.toString(),
                end_date = tl["endDate"]!!.editText!!.text.toString(),
                studyroom_id = tl["studyroom_id"]!!.editText!!.text.toString(),
                location = tl["location"]!!.editText!!.text.toString(),
                place_name = tl["placeName"]!!.editText!!.text.toString(),
                x = tl["x"]!!.editText!!.text.toString(),
                y = tl["y"]!!.editText!!.text.toString(),
                join_max = tl["joinMax"]!!.editText!!.text.toString(),
                join_min = tl["joinMin"]!!.editText!!.text.toString(),
                notification = tl["notification"]!!.editText!!.text.toString(),
                contract = tl["contract"]!!.editText!!.text.toString(),
                type=type
            )
            val serviceRequest= ServiceRequest(this,"/user/lecture.php",lecture)
            serviceRequest.serviceRequest(this)
        }
    }

    fun checkAllInputFields(textInputLayoutList:MutableMap<String,TextInputLayout>) : Boolean{

        textInputLayoutList.forEach{
            if(it.value.editText?.text.toString().trim().isEmpty()) {
                it.value.error="입력 필드를 확인해주세요"
                return false
            } else {
                it.value.error=null
            }
        }
        return true
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),{result ->
        if(result.resultCode == RESULT_OK){
            val intent=result.data
            val location=intent?.getStringExtra("location")
            val place_name=intent?.getStringExtra("place_name")
            val studyroom_id=intent?.getStringExtra("studyroom_id")
            val x=intent?.getStringExtra("x") // X 좌표값, 경위도인 경우 longitude(경도)
            val y=intent?.getStringExtra("y") // Y 좌표값, 경위도인 경우 latitue(위도)
            val studyroomIdInputEditText=binding.inputStudyroomId.editText
            val locationInputEditText=binding.inputLocation.editText
            val placeNameInputEditText=binding.inputPlaceName.editText
            val xEditText=binding.inputX.editText
            val yEditText=binding.inputY.editText
            locationInputEditText?.setText(location)
            placeNameInputEditText?.setText(place_name)
            studyroomIdInputEditText?.setText(studyroom_id)
            xEditText?.setText(x)
            yEditText?.setText(y)

        } else {
            Toast.makeText(this, "위치 선택을 취소 했습니다.", Toast.LENGTH_SHORT).show();
        }
    })

    private fun openEndDatePicker(startDateInMillis: Long) {
        val datePickerDialog = DatePickerDialog(this)
        datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
            val selectedEndDate = Calendar.getInstance()
            selectedEndDate.set(year, month, dayOfMonth)

            val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                selectedEndDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedEndDate.set(Calendar.MINUTE, minute)

                val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
                val formattedEndDate = format.format(selectedEndDate.timeInMillis)

                if (selectedEndDate.timeInMillis < startDateInMillis) {
                    // End date is before start date, show error message
                    Toast.makeText(this, "종료 날짜는 시작 날짜보다 미래여야 합니다.", Toast.LENGTH_SHORT).show()
                    binding.inputEndDate.editText?.setText("") // Clear end date field
                } else {
                    binding.inputEndDate.editText?.setText(formattedEndDate)
                }
            }, 0, 0, false)
            timePickerDialog.show()
        }
        datePickerDialog.show()
    }
}