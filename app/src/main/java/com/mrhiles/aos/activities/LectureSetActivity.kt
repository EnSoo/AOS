package com.mrhiles.aos.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mrhiles.aos.data.Lecture
import com.mrhiles.aos.data.studyRoomFaovr
import com.mrhiles.aos.databinding.ActivityLectureSetBinding
import com.mrhiles.aos.network.ServiceRequest


// 강의 생성, 수정
class LectureSetActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLectureSetBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //시작일, 종료일 누를 경우 달력 표시
        binding.inputStartDate.editText?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this)
            datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
                binding.inputStartDate.editText?.setText("$year-$month-$dayOfMonth")
            }
            datePickerDialog.show()
        }
        binding.inputEndDate.editText?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this)
            datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
                binding.inputEndDate.editText?.setText("$year-$month-$dayOfMonth")
            }
            datePickerDialog.show()
        }

        // 뒤로가기 버튼 및 취소 버튼 클릭 시 액티비티 종료
        binding.arrowBack.setOnClickListener { finish() }
        binding.btnCancel.setOnClickListener { finish() }
        binding.btnSubmit.setOnClickListener {
            val type=intent.getStringExtra("type")

            val textInputLayoutList:MutableMap<String,TextInputLayout> = mutableMapOf()

            val titleInputLayout=binding.inputTitle
            val introductionInputLayout=binding.inputIntroduction
            val startDateInputLayout=binding.inputStartDate
            val endDateInputLayout=binding.inputEndDate
            val searchLocationInputLayout=binding.inputSearchLocation
            val placeNameInputLayout=binding.inputPlaceName
            val joinMaxLayout=binding.inputJoinMax
            val joinMinInputLayout=binding.inputJoinMin
            val notificationInputLayout=binding.inputNotification
            val contractInputLayout=binding.inputContract
            textInputLayoutList["title"]=titleInputLayout
            textInputLayoutList["introduction"]=introductionInputLayout
            textInputLayoutList["startDate"]=startDateInputLayout
            textInputLayoutList["endDate"]=endDateInputLayout
            textInputLayoutList["searchLocation"]=searchLocationInputLayout
            textInputLayoutList["placeName"]=placeNameInputLayout
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
            val lecture= Lecture(
                title= tl["title"]!!.editText!!.text.toString(),
                introduction = tl["introduction"]!!.editText!!.text.toString(),
                start_date = tl["startDate"]!!.editText!!.text.toString(),
                end_date = tl["endDate"]!!.editText!!.text.toString(),
                search_location = tl["searchLocation"]!!.editText!!.text.toString(),
                place_name = tl["placeName"]!!.editText!!.text.toString(),
                join_max = tl["joinMax"]!!.editText!!.text.toString(),
                join_min = tl["joinMin"]!!.editText!!.text.toString(),
                notification = tl["notification"]!!.editText!!.text.toString(),
                contract = tl["contract"]!!.editText!!.text.toString(),
                type=type
            )
            val serviceRequest= ServiceRequest(this,"/user/lecture.php",lecture)
            serviceRequest.serviceRequest("")
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
}