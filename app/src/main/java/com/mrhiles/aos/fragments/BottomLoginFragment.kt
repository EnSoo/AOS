package com.mrhiles.aos.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mrhiles.aos.R
import com.mrhiles.aos.activities.LoginActivity
import com.mrhiles.aos.activities.MainActivity
import com.mrhiles.aos.databinding.FragmentBottomLoginBinding

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
            startActivity(intent)
            val ma: MainActivity = context as MainActivity
            ma.findViewById<BottomNavigationView>(R.id.bnv).selectedItemId= R.id.menu_bnv_home
            dialog.dismiss()
            ma.fragmentLoad()
        }
        // 네이버 클릭 시
        binding.bnvNaverLogin.setOnClickListener {
            intent.putExtra("login_type","naver")
            startActivity(intent)
            val ma: MainActivity = context as MainActivity
            ma.findViewById<BottomNavigationView>(R.id.bnv).selectedItemId= R.id.menu_bnv_home
            dialog.dismiss()
            ma.fragmentLoad()
        }
        // 카카오 클릭 시
        binding.bnvKakaoLogin.setOnClickListener {
            intent.putExtra("login_type","kakao")
            startActivity(intent)
            val ma: MainActivity = context as MainActivity
            ma.findViewById<BottomNavigationView>(R.id.bnv).selectedItemId= R.id.menu_bnv_home
            dialog.dismiss()
            ma.fragmentLoad()
        }

    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog.behavior.state= BottomSheetBehavior.STATE_EXPANDED
        // 둘러보기 클릭 시
        binding.bnvTour.setOnClickListener{ dialog.dismiss() }
        return dialog
    }


}