package com.mrhiles.aos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mrhiles.aos.activities.MainActivity
import com.mrhiles.aos.databinding.FragmentBottomProfileBinding
import com.mrhiles.aos.network.ServiceRequest

class BottomProfileFragment : Fragment(){
    private val binding by lazy { FragmentBottomProfileBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ma: MainActivity = activity as MainActivity
        binding.logout.setOnClickListener{
            val serviceRequest=ServiceRequest(requireContext(),"","")
            serviceRequest.logout()
        }
        Toast.makeText(context, "내 정보를 볼 수 있습니다.", Toast.LENGTH_SHORT).show()
    }
}