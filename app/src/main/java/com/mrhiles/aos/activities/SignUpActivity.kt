package com.mrhiles.aos.activities

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mrhiles.aos.G
import com.mrhiles.aos.R
import com.mrhiles.aos.databinding.ActivitySignUpBinding
import com.mrhiles.aos.network.RetrofitHelper
import com.mrhiles.aos.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener { clickSignUp() }

        // 뒤로가기 클릭 시
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun clickSignUp() {
        var email=binding.inputLayoutEmail.editText!!.text.toString()
        var password=binding.inputLayoutPassword.editText!!.text.toString()
        var name=binding.inputLayoutName.editText!!.text.toString()
        var nickName=binding.inputLayoutNickname.editText!!.text.toString()
        if(email=="" || password=="" || name=="" || nickName=="") { // 누락된 정보가 있을 경우
            Toast.makeText(this, "누락된 정보가 있습니다. 다시 입력하세요", Toast.LENGTH_SHORT).show()
            binding.inputLayoutEmail.editText!!.setText("")
            binding.inputLayoutPassword.editText!!.setText("")
            binding.inputLayoutName.editText!!.setText("")
            binding.inputLayoutNickname.editText!!.setText("")
        } else {
            val retrofit= RetrofitHelper.getunsafeRetrofitInstance(G.baseUrl)
            val retrofitService=retrofit.create(RetrofitService::class.java)
            val call=retrofitService.getSignUp(email,password,name,nickName)
            call.enqueue( object : Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val error = response.body()
                        error ?: return
                        if(error=="1202") { // 누락된 정보가 있을 경우
                            Toast.makeText(this@SignUpActivity, "누락된 정보가 있습니다. 다시 입력하세요", Toast.LENGTH_SHORT).show()
                            binding.inputLayoutEmail.editText!!.setText("")
                            binding.inputLayoutPassword.editText!!.setText("")
                            binding.inputLayoutName.editText!!.setText("")
                            binding.inputLayoutNickname.editText!!.setText("")
                        } else if (error=="1201") { // 회원 추가 실패 시
                            Toast.makeText(this@SignUpActivity, "회원 가입 진행 중 에러가 발생 했습니다.", Toast.LENGTH_SHORT).show()
                            binding.inputLayoutEmail.editText!!.setText("")
                            binding.inputLayoutPassword.editText!!.setText("")
                            binding.inputLayoutName.editText!!.setText("")
                            binding.inputLayoutNickname.editText!!.setText("")
                        } else if(error=="1200") { // 회원 추가 성공 시
                            Toast.makeText(this@SignUpActivity, "회원 가입이 성공적으로 완료 되었습니다.", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }
        val imm=this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputLayoutEmail.getWindowToken(),0);
        imm.hideSoftInputFromWindow(binding.inputLayoutPassword.getWindowToken(),0);
        imm.hideSoftInputFromWindow(binding.inputLayoutName.getWindowToken(),0);
        imm.hideSoftInputFromWindow(binding.inputLayoutNickname.getWindowToken(),0);
    }
}