package com.mrhiles.aos.activities

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.mrhiles.aos.data.LoadStudyRoomFaovr
import com.mrhiles.aos.data.LoginResponse
import com.mrhiles.aos.databinding.ActivityLoginBinding
import com.mrhiles.aos.network.LoginProcess
import com.mrhiles.aos.network.ServiceRequest
import com.mrhiles.aos.data.studyRoomFaovr
import com.mrhiles.aos.network.ServiceFavorRequestCallback
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    // SQLite 작업을 위해
    private lateinit var db: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val loginType: String? = intent.getStringExtra("login_type")

        when(loginType) {
            "email" -> binding.entire.visibility= View.VISIBLE
            "naver" -> clickNaver()
            "kakao" -> clickKakao()
        }
        // 로그인 버튼 클릭 시
        binding.btnSignin.setOnClickListener {
            clickEmail()
            binding.entire.visibility= View.INVISIBLE
        }
        binding.bnvKakaoLogin.setOnClickListener { clickNaver() }
        binding.bnvKakaoLogin.setOnClickListener { clickKakao() }

        // 이메일 회원가입 클릭 시
        binding.btnSignup.setOnClickListener { startActivity(Intent(this, SignUpActivity::class.java)) }

        // 뒤로가기 클릭 시
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    // 이메일 로그인
    private fun clickEmail() {
        var email=binding.inputLayoutEmail.editText!!.text.toString()
        var password=binding.inputLayoutPassword.editText!!.text.toString()
        val loginProcessResult=LoginProcess(this,"email", "",email,password)
        val call= loginProcessResult.getCall()
        call.enqueue( object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val s= response.body()
                    s ?: return
                    loginProcessResult.responseData=s

                    val error=loginProcessResult.responseData.error
                    if(error != "400" ) {
                        if(error=="5200") {
                            Toast.makeText(this@LoginActivity, "이메일 로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                            loginProcessResult.setResult()
                            favorLoad()
                        }
                        else Toast.makeText(this@LoginActivity, "이메일 로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@LoginActivity, "서비스 서버에서 토큰 발급이 성공적으로 이루어지지 않았습니다.", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
        val imm=this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputLayoutEmail.getWindowToken(),0);
        imm.hideSoftInputFromWindow(binding.inputLayoutPassword.getWindowToken(),0);
    }
    //네이버 간편 로그인
    private fun clickNaver() {
        // 네아로 SDK 초기화
        NaverIdLoginSDK.initialize(this, "YQOhvcuPJbnpf4yLQWNh", "ZdeJyRYW2s", "AOS")

        // 로그인 요청
        NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback {
            override fun onError(errorCode: Int, message: String) {
                Log.d("NaverLoginError","$message")
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Log.d("NaverLoginFailure","$message")
            }

            override fun onSuccess() {
                Log.d("NaverLogin","네이버 액세스 토큰 발급 성공")

                val accessToken:String? = NaverIdLoginSDK.getAccessToken()
                accessToken ?: return
                val loginProcessResult=LoginProcess(this@LoginActivity,"naver", accessToken, "","")
                val call= loginProcessResult.getCall()
                call.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val s= response.body()
                            s ?: return
                            loginProcessResult.responseData=s

                            val error=loginProcessResult.responseData.error
                            if(error != "400" ) {
                                if(error=="5200") {
                                    Toast.makeText(this@LoginActivity, "네이버 간편 로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                                    loginProcessResult.setResult()
                                    favorLoad()
                                }
                                else Toast.makeText(this@LoginActivity, " 네이버 간편 로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@LoginActivity, "서비스 서버에서 토큰 발급이 성공적으로 이루어지지 않았습니다.", Toast.LENGTH_SHORT).show()
                            }
                            finish()
                        }
                    }
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

            }
        })
    }

    //카카오 간편 로그인
    private fun clickKakao() {
        val callback:(OAuthToken?, Throwable?)->Unit = { token, error ->
            if(error != null) {
                Log.d("KakaoFailure","$error")
            }else{
                Log.d("KakaoLogin","카카오 액세스 토큰 발급 성공")

                val accessToken:String = token!!.accessToken

                val loginProcessResult=LoginProcess(this@LoginActivity,"kakao", accessToken, "","")
                val call= loginProcessResult.getCall()
                call.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val s= response.body()

                            s ?: return
                            loginProcessResult.responseData=s
                            val error=loginProcessResult.responseData.error
                            if(error != "400" ) {
                                if(error=="5200") {
                                    Toast.makeText(this@LoginActivity, "카카오 간편 로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                                    loginProcessResult.setResult()
                                    favorLoad()
                                }
                                else Toast.makeText(this@LoginActivity, "${error}: 카카오 간편 로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@LoginActivity, "서비스 서버에서 토큰 발급이 성공적으로 이루어지지 않았습니다.", Toast.LENGTH_SHORT).show()
                            }
                            finish()
                        }
                    }
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })


            }
        }

// 카카오톡이 사용가능하면 이를 이용하여 로그인하고 없으면 카카오계정으로 로그인하기
        if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
            UserApiClient.instance.loginWithKakaoTalk(this, callback= callback)
        }else{
            UserApiClient.instance.loginWithKakaoAccount(this, callback= callback)
        }

    }
    private fun favorLoad() {
        //sqllite 작업 시작
        // "study.db"라는 이름으로 데이터베이스 파일을 만들거나 열어서 참조하기
        db= ContextWrapper(this).openOrCreateDatabase("study", Context.MODE_PRIVATE,null)

        // "favor"라는 이름의 표(테이블) 만들기 - SQL 쿼리문을 사용하여.. CRUD 작업수행
        db.execSQL("CREATE TABLE IF NOT EXISTS favor(id TEXT PRIMARY KEY, place_name TEXT, category_name TEXT, phone TEXT, address_name TEXT, x TEXT, y TEXT, place_url TEXT)")

        val studyRoomFaovr= studyRoomFaovr(type="load")
        val serviceRequest= ServiceRequest(this,"/user/favor.php",studyRoomFaovr, callbackFavor = object : ServiceFavorRequestCallback{
            override fun onServiceFavorResponseSuccess(response: List<LoadStudyRoomFaovr>?) {
                response?.forEach {
                    db.execSQL("INSERT INTO favor VALUES('${it.id}','${it.place_name}','${it.category_name}','${it.phone}','${it.address_name}'," +
                            "'${it.x}','${it.y}','${it.place_url}')")
                }
            }

            override fun onServiceFavorResponseFailure() {}

        }).serviceRequest()
    }

}