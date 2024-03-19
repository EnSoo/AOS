package com.mrhiles.aos.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mrhiles.aos.R
import com.mrhiles.aos.activities.MainActivity
import com.mrhiles.aos.databinding.FragmentBottomProfileBinding
import com.mrhiles.aos.network.Service

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
            val service=Service(requireContext(),"","")
            service.logout()
        }
    }
}