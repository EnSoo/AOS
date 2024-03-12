package com.mrhiles.aos.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mrhiles.aos.activities.LoginActivity
import com.mrhiles.aos.activities.MainActivity
import com.mrhiles.aos.activities.StudyRoomDetailActivity
import com.mrhiles.aos.databinding.FragmentBottomHomeBinding
import com.mrhiles.aos.databinding.FragmentBottomLoginBinding
import com.mrhiles.aos.network.RetrofitHelper
import com.mrhiles.aos.network.RetrofitService
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BottomLoginFragment : BottomSheetDialogFragment() {
    private val binding by lazy { FragmentBottomLoginBinding.inflate(layoutInflater) }
    val dialog: BottomSheetDialog by lazy{ BottomSheetDialog(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val intent = Intent(requireContext(), LoginActivity::class.java)
        // 이메일 클릭 시
        binding.bnvAppleLogin.setOnClickListener {
            intent.putExtra("login_type","email")
            dialog.dismiss()
            startActivity(intent)
        }
        // 네이버 클릭 시
        binding.bnvNaverLogin.setOnClickListener {
            intent.putExtra("login_type","naver")
            dialog.dismiss()
            startActivity(intent)
        }
        // 카카오 클릭 시
        binding.bnvKakaoLogin.setOnClickListener {
            intent.putExtra("login_type","kakao")
            dialog.dismiss()
            startActivity(intent)
        }

    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog.behavior.state= BottomSheetBehavior.STATE_EXPANDED
        // 둘러보기 클릭 시
        binding.bnvTour.setOnClickListener{ dialog.dismiss() }
        return dialog

    }


}

//kakaologin 액세스토큰 정보 저장
//val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
//    if (error != null) {
//        // 로그인 실패
//        Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
//    } else {
//        // 로그인 성공
//        Toast.makeText(this, "카카오 로그인 성공", Toast.LENGTH_SHORT).show()
//
//        // 액세스 토큰 발급
//        val accessToken = token?.accessToken
//
//        // 서버에 액세스 토큰 전송
//        // ... (서버 API 호출 코드) ...
//    }
//}
//val sharedPreferences = getSharedPreferences("kakao_login", Context.MODE_PRIVATE)
//val editor = sharedPreferences.edit()
//editor.putString("access_token", token?.accessToken)
//editor.apply()