package com.mrhiles.aos.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mrhiles.aos.databinding.FragmentBottomHomeBinding
import com.mrhiles.aos.databinding.FragmentBottomLoginBinding

class BottomLoginFragment : BottomSheetDialogFragment() {
    private val binding by lazy { FragmentBottomLoginBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 이메일 클릭 시
        binding.bnvAppleLogin.setOnClickListener { Toast.makeText(requireContext(), "이메일 클릭 시", Toast.LENGTH_SHORT).show() }
        // 네이버 클릭 시
        binding.bnvNaverLogin.setOnClickListener { Toast.makeText(requireContext(), "네이버 클릭 시", Toast.LENGTH_SHORT).show() }
        // 카카오 클릭 시
        binding.bnvKakaoLogin.setOnClickListener { Toast.makeText(requireContext(), "카카오 클릭 시", Toast.LENGTH_SHORT).show() }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        dialog.behavior.state= BottomSheetBehavior.STATE_EXPANDED
        // 둘러보기 클릭 시
        binding.bnvTour.setOnClickListener{ dialog.dismiss() }
        return dialog

    }

}