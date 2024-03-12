package com.mrhiles.aos.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mrhiles.aos.R
import com.mrhiles.aos.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private val binding by lazy { ActivityChatBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }
}