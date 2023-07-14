package com.zattech.zatgpt
//import android.text.Editable

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.zattech.zatgpt.databinding.ActivityChatBinding
import com.google.gson.Gson
import com.zattech.zatgpt.adapter.MessageAdapter
import com.zattech.zatgpt.api.ApiUtilities
import com.zattech.zatgpt.models.MessageModel
import com.zattech.zatgpt.models.request.ChatRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.*

class ChatActivity : AppCompatActivity() {
    private val RQ_SPEECH_REC=102
    private lateinit var binding: ActivityChatBinding
    var  list=ArrayList<MessageModel>()
    private  lateinit var mLayoutManager:LinearLayoutManager
    private lateinit var adapter: MessageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtn.setOnClickListener{finish()}
        mLayoutManager=LinearLayoutManager(this)
        mLayoutManager.stackFromEnd=true
        adapter=MessageAdapter(list)
        binding.recyclerView.adapter=adapter
        binding.recyclerView.layoutManager=mLayoutManager

         binding.micbtn.setOnClickListener{
             askSpeechInput()
         }


        binding.sendbtn.setOnClickListener{
            if( binding.userMsg.text!!.isEmpty()){
                Toast.makeText(this, "Please ask your question", Toast.LENGTH_SHORT).show()
            }else{
//                binding.textView2.setVisibility(View.GONE)
                binding.imageView2.setVisibility(View.GONE)
                binding.textView2.text = null



                callAPI()
            }
        }
    }





    private fun callAPI(){
        binding.recyclerView.recycledViewPool.clear()
        val userInput = binding.userMsg.text.toString()
        list.add(MessageModel(true, false, userInput))
//        list.add(MessageModel(true,false,binding.userMsg.text.toString()))
        adapter.notifyItemInserted(list.size-1)
        binding.recyclerView.smoothScrollToPosition(list.size-1)

        val apiInterface= ApiUtilities.getApiInterface()
//        list.add(MessageModel(false, false,"Typing..."))
        val requestBody=RequestBody.create(MediaType.parse("application/json"),
        Gson().toJson(
            ChatRequest(
                250,
                "text-davinci-003",
                binding.userMsg.text.toString(),
                0.7
            )
        )
        )
        val contentType="application/json"
         val authorization="Bearer ${Utils.API_KEY}"
        binding.userMsg.text!!.clear()

        lifecycleScope.launch(Dispatchers.IO )

        {
             try {
                 val response = apiInterface.getChat(contentType, authorization, requestBody)
                 val textResponse = response.choices.first().text
                 list.add(MessageModel(false, false,textResponse))
                 withContext(Dispatchers.Main) {
                     adapter.notifyItemInserted(list.size-1)
                     binding.recyclerView.recycledViewPool.clear()
                     binding.recyclerView.smoothScrollToPosition(list.size - 1)
                 }
//


                  } catch (e:Exception){
                 withContext (Dispatchers.Main){
                     Toast.makeText(this@ChatActivity,e.message,Toast.LENGTH_SHORT).show()
                     binding.userMsg.text!!.clear()
             }
                  }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==RQ_SPEECH_REC && resultCode== RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding.userMsg.setText(result?.get(0).toString())


        }
     }

    private fun askSpeechInput(){
        if (!SpeechRecognizer.isRecognitionAvailable(this)){
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
        }else{
            val i=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Something!")
            startActivityForResult(i,RQ_SPEECH_REC)


        }



}

}














//
//
//
//
//
//
//
//
//
//
//
//
//package com.example.zatgpt
//import android.app.Activity
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
////import android.text.Editable
//
//import android.speech.RecognizerIntent
//import android.speech.SpeechRecognizer
//import android.speech.tts.TextToSpeech
//import android.text.Editable
//import android.widget.Adapter
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.withCreated
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.zatgpt.adapter.MessageAdapter
//import com.example.zatgpt.api.ApiInterface
//import com.example.zatgpt.api.ApiUtilities
//import com.example.zatgpt.databinding.ActivityChatBinding
//import com.example.zatgpt.models.MessageModel
//import com.example.zatgpt.models.request.ChatRequest
//import com.google.gson.Gson
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import okhttp3.MediaType
//import okhttp3.RequestBody
//import java.util.Locale
//
//class ChatActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
//    lateinit var tts:TextToSpeech
//    private val RQ_SPEECH_REC=102
//    private lateinit var binding:ActivityChatBinding
//    var  list=ArrayList<MessageModel>()
//    private  lateinit var mLayoutManager:LinearLayoutManager
//    private lateinit var adapter:MessageAdapter
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding=ActivityChatBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        binding.backBtn.setOnClickListener{finish()}
//        mLayoutManager=LinearLayoutManager(this)
//        mLayoutManager.stackFromEnd=true
//        adapter=MessageAdapter(list)
//        binding.recyclerView.adapter=adapter
//        binding.recyclerView.layoutManager=mLayoutManager
//        tts = TextToSpeech(this,this)
//
//        binding.micbtn.setOnClickListener{
//            askSpeechInput()
//        }
//
//
//        binding.sendbtn.setOnClickListener{
//            if( binding.userMsg.text!!.isEmpty()){
//                Toast.makeText(this, "Please ask your question", Toast.LENGTH_SHORT).show()
//            }else{
//
//                callAPI()
//            }
//        }
//    }
//
//
//
//
//
//    private fun callAPI(){
//        binding.recyclerView.recycledViewPool.clear()
//        val userInput = binding.userMsg.text.toString()
//        list.add(MessageModel(true, false, userInput))
////        list.add(MessageModel(true,false,binding.userMsg.text.toString()))
//        adapter.notifyItemInserted(list.size-1)
//        binding.recyclerView.smoothScrollToPosition(list.size-1)
//
//        val apiInterface=ApiUtilities.getApiInterface()
////        list.add(MessageModel(false, false,"Typing..."))
//        val requestBody=RequestBody.create(MediaType.parse("application/json"),
//            Gson().toJson(
//                ChatRequest(
//                    250,
//                    "text-davinci-003",
//                    binding.userMsg.text.toString(),
//                    0.7
//                )
//            )
//        )
//        val contentType="application/json"
//        val authorization="Bearer ${Utils.API_KEY}"
//        binding.userMsg.text!!.clear()
//
//        lifecycleScope.launch(Dispatchers.IO )
//
//        {
//            try {
//                val response = apiInterface.getChat(contentType, authorization, requestBody)
//                val textResponse = response.choices.first().text
//
//                tts.speak(textResponse,TextToSpeech.QUEUE_FLUSH,null)
//                list.add(MessageModel(false, false,textResponse))
//                withContext(Dispatchers.Main) {
//                    adapter.notifyItemInserted(list.size-1)
//                    binding.recyclerView.recycledViewPool.clear()
//                    binding.recyclerView.smoothScrollToPosition(list.size - 1)
//                }
////
//
//
//            } catch (e:Exception){
//                withContext (Dispatchers.Main){
//                    Toast.makeText(this@ChatActivity,e.message,Toast.LENGTH_SHORT).show()
//                    binding.userMsg.text!!.clear()
//                }
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode==RQ_SPEECH_REC && resultCode==Activity.RESULT_OK) {
//            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
//            binding.userMsg.setText(result?.get(0).toString())
//
//
//        }
//    }
//
//    private fun askSpeechInput(){
//        if (!SpeechRecognizer.isRecognitionAvailable(this)){
//            Toast.makeText(this,"something went wrong",Toast.LENGTH_SHORT).show()
//        }else{
//            val i=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())
//            i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Something!")
//            startActivityForResult(i,RQ_SPEECH_REC)
//
//
//        }
//
//
//
//    }
//
//    override fun onInit(status: Int) {
//        if(status==TextToSpeech.SUCCESS){
//            val res:Int=tts.setLanguage(Locale.US)
//            if(res==TextToSpeech.LANG_MISSING_DATA || res ==TextToSpeech.LANG_NOT_SUPPORTED ){
//                Toast.makeText(this,"Language not supportes",Toast.LENGTH_SHORT).show()
//            }
//        }
//        else{
//            Toast.makeText(this,"Failed to initialize",Toast.LENGTH_SHORT).show()
//        }
//    }
//
//}
//
