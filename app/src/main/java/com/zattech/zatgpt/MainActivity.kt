package com.zattech.zatgpt

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zattech.zatgpt.databinding.ActivityMainBinding
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.generateImage.setOnClickListener{
            startActivity(Intent(this,ImageGenerateActivity::class.java))
        }
        binding.talkWithBot.setOnClickListener{
            startActivity(Intent(this,TalkActivity::class.java))
        }
        binding.chatWithBot.setOnClickListener{
            startActivity(Intent(this, com.zattech.zatgpt.ChatActivity::class.java))
        }
    }
}