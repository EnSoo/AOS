package com.mrhiles.aos.network

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.mrhiles.aos.G
import com.mrhiles.aos.R
import com.mrhiles.aos.activities.LoginActivity
import com.mrhiles.aos.activities.MainActivity
import com.mrhiles.aos.data.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder

class ServiceRequest(
    val context : Context,
    val serviceUrl : String,
    var params: Any
) {
    private lateinit var error:String
    private lateinit var code:String
    private lateinit var responseData:String
    private lateinit var accessToken: String
    private lateinit var refreshToken: String
    private lateinit var db: SQLiteDatabase
    private fun getToken() {
        // 글로벌 변수에서 액세스 토큰 및 리프레시 토큰 가져오기
        accessToken=G.accessToken
        refreshToken=G.refreshToken
    }
    private fun setRetrofitService() : RetrofitService {
        val retrofit= RetrofitHelper.getunsafeRetrofitInstance(G.baseUrl)
        val retrofitService=retrofit.create(RetrofitService::class.java)
        return retrofitService
    }

    fun serviceRequest(processObject :Any) {
        if(G.isLogin) { // 로그인 상태 경우만 실행
            getToken()
            val retrofitService = setRetrofitService()
            // 파라미터를 Json 형태로 변환
            var requestData: requestData = requestData(params, URLEncoder.encode(accessToken, "UTF-8"))
            val call = retrofitService.serviceRequest(serviceUrl, requestData)
            call.enqueue(object : Callback<responseData> {
                override fun onResponse(
                    call: Call<responseData>,
                    response: Response<responseData>
                ) {
                    Log.d("s","${response}")
                    if (response.isSuccessful) {
                        val s = response.body()

                        s ?: return
                        error = s.error
                        code = s.code
                        responseData = s.params
                        if (error == "5301") {  // 액세스 토큰이 만료 되었을 경우 리프레쉬로 요청
                            getAccessToken()
                        } else if (error == "5302" || error == "5303" || error == "5204") { // 5302, 리프레쉬 토큰 만료, 5303 유효하지 않은 토큰일 경우, 5204 액세스 토큰 재발급 실패
                            Toast.makeText(context, "유효하지 않은 토큰으로 작업하였으므로 로그아웃 합니다.", Toast.LENGTH_SHORT).show()
                            logout()
                        } else if (error == "5300") {  // 유효한 토큰일 경우
                            serviceProcess(processObject)
                        } else if (error == "5200") { // 액세스 토큰 재발급 요청이 정상적으로 이루어 졌을 경우 서비스 재요청
                            serviceRequest(processObject)

                        }
                    }
                }

                override fun onFailure(call: Call<responseData>, t: Throwable) {
                    Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()

                }
            })
        } else {
            //로그인 상태가 아닐 경우 activity 띄워야 함
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra("login_type",G.userInfo.loginType)
            context.startActivity(intent)
        }
    }

// 서비스별로 멘트를 다르게 처리해야 함
    private fun serviceProcess(processObject:Any) {
        if(code == "200") {
            when(serviceUrl) { // 서비스에 따라 파싱이 필요
                "/user/favor.php" -> favorProcess(processObject as ImageView)
            }
        } else {
            Toast.makeText(context, "오류가 있어 처리 되지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun favorProcess(imageView:ImageView) {
        // 성공만 있으면 되므로...

        //sqllite 작업 시작
        // "study.db"라는 이름으로 데이터베이스 파일을 만들거나 열어서 참조하기
        db= ContextWrapper(context).openOrCreateDatabase("study", Context.MODE_PRIVATE,null)

        // "favor"라는 이름의 표(테이블) 만들기 - SQL 쿼리문을 사용하여.. CRUD 작업수행
        db.execSQL("CREATE TABLE IF NOT EXISTS favor(id TEXT PRIMARY KEY, place_name TEXT, category_name TEXT, phone TEXT, address_name TEXT, x TEXT, y TEXT, place_url TEXT)")
        val param=params as studyRoomFaovr
        if(param.type=="remove") { // favor 삭제 기능일 경우
            imageView.setImageResource(R.drawable.ic_favor_border)
            param.apply {
                db.execSQL("DELETE FROM favor WHERE id=?", arrayOf(id))
            }
            Toast.makeText(context, "찜을 삭제 했습니다.", Toast.LENGTH_SHORT).show()
        } else if (param.type=="add"){ // favor 추가 기능일 경우
            imageView.setImageResource(R.drawable.ic_favor_full)

            param.apply {
                db.execSQL("INSERT INTO favor VALUES('$id','$place_name','$category_name','$phone','$address_name','$x','$y','$place_url')")
            }
            Toast.makeText(context, "찜을 추가 했습니다.", Toast.LENGTH_SHORT).show()
        } else if (param.type=="load") { //load 할 경우
            val studyRoomFavorList:Array<LoadStudyRoomFaovr> =Gson().fromJson(responseData,Array<LoadStudyRoomFaovr>::class.java)
            studyRoomFavorList.forEach {
                db.execSQL("INSERT INTO favor VALUES('${it.id}','${it.place_name}','${it.category_name}','${it.phone}','${it.address_name}'," +
                        "'${it.x}','${it.y}','${it.place_url}')")
            }
        }
    }

// 로그아웃. 네이버, 카카오 등 브라우저 로그인을 취소해야 함
    fun logout() {
        getToken()
        val retrofitService=setRetrofitService()
        val call=retrofitService.logout(URLEncoder.encode(refreshToken, "UTF-8"))
        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val error=response.body()

                    error ?: return
                    val ma: MainActivity = context as MainActivity
                    if(error =="7201") {
                        Toast.makeText(context, "로그아웃 실패", Toast.LENGTH_SHORT).show()
                    }
                    if(error =="7200" || error =="6200" || error =="6201") { // 로그인된 회원에서 찾을 수 없음 6201
                        Toast.makeText(context, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                        // sharedPreferences 초기화
                        initsharedPreferences()

                        // 글로벌변수 초기화
                        initGlobal()

                        //sqllite 초기화
                        initSqlLite()

                        ma.findViewById<BottomNavigationView>(R.id.bnv).selectedItemId= R.id.menu_bnv_home
                    }

// 간편 로그인 취소 처리 추가 부분
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun initsharedPreferences() {
        val sharedPreferences = context.getSharedPreferences("logininfo", Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.putBoolean("is_login",false)
        editor.putString("access_token","")
        editor.putString("refresh_token","")
        editor.putString("login_type","")
        editor.putString("providerId","")
        editor.putString("email","")
        editor.apply()
    }

    private fun initGlobal() {
        // 글로벌 변수 설정
        G.isLogin=false
        G.accessToken=""
        G.refreshToken=""
        G.userInfo= UserInfo("","","")
    }

    private fun initSqlLite() {
        db= ContextWrapper(context).openOrCreateDatabase("study", Context.MODE_PRIVATE,null)
        db.execSQL("DELETE FROM favor", null)
    }

    fun getAccessToken() {
        val retrofitService=setRetrofitService()
        val call=retrofitService.tokenGenrate(URLEncoder.encode(refreshToken, "UTF-8"),"refresh_token")
        call.enqueue(object : Callback<UserCheck>{
            override fun onResponse(call: Call<UserCheck>, response: Response<UserCheck>) {
                if (response.isSuccessful) {
                    val s= response.body()
                    s ?: return
                    error=s.error
                    accessToken=s.access_token
                }
            }

            override fun onFailure(call: Call<UserCheck>, t: Throwable) {
                Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}