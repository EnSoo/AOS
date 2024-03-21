package com.mrhiles.aos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mrhiles.aos.databinding.FragmentBottomReservedBinding

class BottomReservedFragment : Fragment(){
    private val binding by lazy { FragmentBottomReservedBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(context, "내가 신청한 강의를 볼 수 있습니다.", Toast.LENGTH_SHORT).show()
    }
}