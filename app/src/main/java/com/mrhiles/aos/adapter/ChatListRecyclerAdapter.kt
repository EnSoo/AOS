package com.mrhiles.aos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.mrhiles.aos.data.ChatRoom
import com.mrhiles.aos.databinding.RecyclerAdapterChatRoomListBinding

class ChatListRecyclerAdapter(val context:Context, val chatLists:List<ChatRoom>) : Adapter<ChatListRecyclerAdapter.VH>() {
    inner class VH(val binding : RecyclerAdapterChatRoomListBinding) : ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=VH(RecyclerAdapterChatRoomListBinding.inflate(LayoutInflater.from(context),parent,false))
    override fun getItemCount()=chatLists.size
    override fun onBindViewHolder(holder: VH, position: Int) {
        val chatRoom=chatLists[position]
        Glide.with(context).load(chatRoom.roomImageUrl).into(holder.binding.ivChatRoomProfile)
        holder.binding.tvRoomUserList.text=chatRoom.roomUserList
        holder.binding.tvLastMessage.text=chatRoom.lastMessage
        holder.binding.tvLastTime.text=chatRoom.lastTime

        holder.binding.root.setOnClickListener {

        }
    }
}