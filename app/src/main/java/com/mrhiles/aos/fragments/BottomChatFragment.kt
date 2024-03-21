package com.mrhiles.aos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mrhiles.aos.adapter.ChatListRecyclerAdapter
import com.mrhiles.aos.data.ChatRoom
import com.mrhiles.aos.databinding.FragmentBottomChatBinding

class BottomChatFragment : Fragment(){
    private val binding by lazy { FragmentBottomChatBinding.inflate(layoutInflater) }

    private val chatLists by lazy { mutableListOf<ChatRoom>() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 채팅목록 불러오기
        chatLists.add(ChatRoom(1,"https://ssl.pstatic.net/melona/libs/1412/1412850/974b452fd3acdd6f6d79_20240229170447847.png","사용자 이름1, 사용자 이름2","메시지","오후 2:39",1))
        binding.chatRecycler.adapter=ChatListRecyclerAdapter(requireContext(),chatLists)

    }
}